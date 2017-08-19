python check_public_keys () {
    gpg_path = d.getVar('GPG_PATH', True)
    gpg_bin = d.getVar('GPG_BIN', True) or \
              bb.utils.which(os.getenv('PATH'), 'gpg')
    gpg_keyid = d.getVar('RPM_GPG_NAME', True)

    # Check RPM_GPG_NAME and RPM_GPG_PASSPHRASE
    cmd = "%s --homedir %s --list-keys -a %s" % \
            (gpg_bin, gpg_path, gpg_keyid)
    status, output = oe.utils.getstatusoutput(cmd)
    if not status:
        return

    # Import RPM_GPG_NAME if not found
    gpg_key = uks_rpm_keys_dir(d) + 'RPM-GPG-PRIVKEY-' + gpg_keyid
    cmd = '%s --batch --homedir %s --import %s --passphrase %s' % \
            (gpg_bin, gpg_path, gpg_key, d.getVar('RPM_GPG_PASSPHRASE', True))
    status, output = oe.utils.getstatusoutput(cmd)
    print (cmd)
    if status:
        raise bb.build.FuncFailed('Failed to import gpg key (%s): %s' %
                                  (gpg_key, output))
}
check_public_keys[cleandirs] = "${B}"
do_get_public_keys[prefuncs] += "check_public_keys"
