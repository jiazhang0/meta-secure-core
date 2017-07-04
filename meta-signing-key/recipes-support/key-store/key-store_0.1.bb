DESCRIPTION = "Key store for key installation"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420 \
"

inherit user-key-store

S = "${WORKDIR}"

ALLOW_EMPTY_${PN} = "1"

PACKAGES =+ "\
    ${PN}-system-trusted-cert \
    ${PN}-ima-cert \
"

# Note any private key is not available if user key signing model used.
PACKAGES_DYNAMIC += "\
    ${PN}-ima-privkey \
    ${PN}-system-trusted-privkey \
    ${PN}-rpm-pubkey \
"

KEY_DIR = "${sysconfdir}/keys"
# For RPM verification
RPM_KEY_DIR = "${sysconfdir}/pki/rpm-gpg"

# For ${PN}-system-trusted-privkey
SYSTEM_PRIV_KEY = "${KEY_DIR}/system_trusted_key.key"

# For ${PN}-ima-privkey
IMA_PRIV_KEY = "${KEY_DIR}/privkey_evm.crt"

# For ${PN}-system-trusted-cert
SYSTEM_CERT = "${KEY_DIR}/system_trusted_key.crt"

# For ${PN}-ima-cert
IMA_CERT = "${KEY_DIR}/x509_evm.der"

python () {
    if uks_signing_model(d) != "sample":
        return

    pn = d.getVar('PN', True) + '-system-trusted-privkey'
    d.setVar('PACKAGES_prepend', pn + ' ')
    d.setVar('FILES_' + pn, d.getVar('SYSTEM_PRIV_KEY', True))
    d.setVar('CONFFILES_' + pn, d.getVar('SYSTEM_PRIV_KEY', True))

    pn = d.getVar('PN', True) + '-ima-privkey'
    d.setVar('PACKAGES_prepend', pn + ' ')
    d.setVar('FILES_' + pn, d.getVar('IMA_PRIV_KEY', True))
    d.setVar('CONFFILES_' + pn, d.getVar('IMA_PRIV_KEY', True))

    pn = d.getVar('PN', True) + '-rpm-pubkey'
    d.setVar('PACKAGES_prepend', pn + ' ')
    d.setVar('FILES_' + pn, d.getVar(d.getVar('RPM_KEY_DIR', True) + '/RPM-GPG-KEY-*', True))
    d.setVar('CONFFILES_' + pn, d.getVar(d.getVar('RPM_KEY_DIR', True) + 'RPM-GPG-KEY-*', True))
    d.appendVar('RDEPENDS_' + pn, ' rpm')
}

do_install() {
    install -d "${D}${RPM_KEY_DIR}"

    for f in `ls ${WORKDIR}/RPM-GPG-KEY-* 2>/dev/null`; do
        [ ! -f "$f" ] && continue

        install -m 0644 "$f" "${D}${RPM_KEY_DIR}"
    done

    key_dir="${@uks_rpm_keys_dir(d)}"
    if [ -n "$key_dir" ]; then
        for f in `ls $key_dir/RPM-GPG-KEY-* 2>/dev/null`; do
            [ ! -s "$f" ] && continue

            install -m 0644 "$f" "${D}${RPM_KEY_DIR}"
        done
    fi

    install -d "${D}${KEY_DIR}"

    key_dir="${@uks_system_trusted_keys_dir(d)}"
    install -m 0644 "$key_dir/system_trusted_key.crt" "${D}${SYSTEM_CERT}"

    if [ "${@uks_signing_model(d)}" = "sample" ]; then
        install -m 0400 "$key_dir/system_trusted_key.key" "${D}${SYSTEM_PRIV_KEY}"
    fi

    key_dir="${@uks_ima_keys_dir(d)}"
    install -m 0644 "$key_dir/x509_ima.der" "${D}${IMA_CERT}"

    if [ "${@uks_signing_model(d)}" = "sample" ]; then
        install -m 0400 "$key_dir/x509_ima.key" "${D}${IMA_PRIV_KEY}"
    fi
}

SYSROOT_PREPROCESS_FUNCS += "key_store_sysroot_preprocess"

key_store_sysroot_preprocess() {
    sysroot_stage_dir "${D}${sysconfdir}" "${SYSROOT_DESTDIR}${sysconfdir}"
}

pkg_postinst_${PN}-rpm-pubkey() {
    if [ -z "$D" ]; then
        keydir="${RPM_KEY_DIR}"

        [ ! -d "$keydir" ] && mkdir -p "$keydir"

        # XXX: only import the new key
        for keyfile in `ls $keydir/RPM-GPG-KEY-*`; do
            [ ! -f "$keyfile" ] && continue

            ! rpm --import "$keyfile" && {
                echo "Unable to import the public key $keyfile"
                exit 1
            }
        done
    fi
}

FILES_${PN}-system-trusted-cert = "${SYSTEM_CERT}"
CONFFILES_${PN}-system-trusted-cert = "${SYSTEM_CERT}"

FILES_${PN}-ima-cert = "${IMA_CERT}"
CONFFILES_${PN}-ima-cert = "${IMA_CERT}"
