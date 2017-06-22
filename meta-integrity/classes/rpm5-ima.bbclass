inherit package

PACKAGEFUNCS =+ "package_ima_hook"

# security.ima is generated during the RPM build, and the base64-encoded
# value is written during RPM installation. In addition, if the private
# key is deployed on board, re-sign the updated files during RPM
# installation in higher priority.
python package_ima_hook() {
    packages = d.getVar('PACKAGES', True)
    pkgdest = d.getVar('PKGDEST', True)
    ima_signing_blacklist = d.getVar('IMA_SIGNING_BLACKLIST', True)
    ima_keys_dir = d.getVar('IMA_KEYS_DIR', True)

    pkg_suffix_blacklist = ('dbg', 'dev', 'doc', 'locale', 'staticdev')

    pkg_blacklist = ()
    with open(ima_signing_blacklist, 'r') as f:
        pkg_blacklist = [ _.strip() for _ in f.readlines() ]
        pkg_blacklist = tuple(pkg_blacklist)

    import base64, pipes, stat

    for pkg in packages.split():
        if (pkg.split('-')[-1] in pkg_suffix_blacklist) is True:
            continue

        if pkg.startswith(pkg_blacklist) is True:
            continue

        bb.note("Writing IMA %%post hook for %s ..." % pkg)

        pkgdestpkg = os.path.join(pkgdest, pkg)

        cmd = 'evmctl ima_sign --hashalgo sha256 -n --sigfile --key %s/x509_ima.key ' % (ima_keys_dir)
        sig_list = []
        pkg_sig_list = []

        for _ in pkgfiles[pkg]:
            # Ignore the symbol links.
            if os.path.islink(_):
                continue

            # IMA appraisal is only applied to the regular file.
            if not stat.S_ISREG(os.stat(_)[stat.ST_MODE]):
                continue

            bb.note("Preparing to sign %s ..." % _)

            sh_name = pipes.quote(_)
            print("Signing command: %s" % cmd + sh_name)
            rc, res = oe.utils.getstatusoutput(cmd + sh_name + " >/dev/null")
            if rc:
                bb.fatal('Calculate IMA signature for %s failed with exit code %s:\n%s' % \
                    (_, rc, res if res else ""))

            with open(_ + '.sig', 'rb') as f:
                s = str(base64.b64encode(f.read()).decode('ascii')) + '|'
                sig_list.append(s + os.sep + os.path.relpath(_, pkgdestpkg))

            os.remove(_ + '.sig')

        ima_sig_list = '&'.join(sig_list)

        # When the statically linked binary is updated, use the
        # dynamically linked one to resign or set. This situation
        # occurs in runtime only.
        setfattr_bin = 'setfattr.static'
        evmctl_bin = 'evmctl.static'
        # We don't want to create a statically linked echo program
        # any more.
        safe_echo = '1'
        if pkg == 'attr-setfattr.static':
            setfattr_bin = 'setfattr'
        elif pkg == 'ima-evm-utils-evmctl.static':
            evmctl_bin = 'evmctil'
        elif pkg == 'coreutils':
            safe_echo = '0'

        # The %post is dynamically constructed according to the currently
        # installed package and enviroment.
        postinst = r'''#!/bin/sh

# %post hook for IMA appraisal
ima_resign=0
sig_list="''' + ima_sig_list + r'''"

if [ -z "$D" ]; then
    evmctl_bin="${sbindir}/''' + evmctl_bin + r'''"
    setfattr_bin="${bindir}/''' + setfattr_bin + r'''"

    [ -f "/etc/keys/privkey_evm.pem" -a -x "$evmctl_bin" ] && \
        ima_resign=1

    safe_echo="''' + safe_echo + r'''"

    cond_print()
    {
        [ $safe_echo = "1" ] && echo $1
    }

    saved_IFS="$IFS"
    IFS="&"
    for entry in $sig_list; do
        IFS="|"

        tokens=""
        for token in $entry; do
            tokens="$tokens$token|"
        done

        for sig in $tokens; do
            break
        done

        IFS="$saved_IFS"

        f="$token"

        # If the filesystem doesn't support xattr, skip the following steps.
        res=`"$setfattr_bin" -x security.ima "$f" 2>&1 | grep "Operation not supported$"`
        [ x"$res" != x"" ] && {
            cond_print "Current file system doesn't support to set xattr"
            break
        }

        if [ $ima_resign -eq 0 ]; then
            cond_print "Setting up security.ima for $f ..."

            "$setfattr_bin" -n security.ima -v "0s$sig" "$f" || {
                err=$?
                cond_print "Unable to set up security.ima for $f (err: $err)"
                exit 1
            }
        else
            cond_print "IMA signing for $f ..."

            "$evmctl_bin" ima_sign --hashalgo sha256 "$f" || {
                err=$?
                cond_print "Unable to sign $f (err: $err)"
                exit 1
            }
        fi

        IFS="&"
    done

    IFS="$saved_IFS"
fi

'''
        postinst = postinst + (d.getVar('pkg_postinst_%s' % pkg, True) or '')
        d.setVar('pkg_postinst_%s' % pkg, postinst)
}

do_package[depends] += "ima-evm-utils-native:do_populate_sysroot"
