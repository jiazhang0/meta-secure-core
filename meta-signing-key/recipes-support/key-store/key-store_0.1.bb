DESCRIPTION = "Key store for key installation"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
    file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
"

S = "${WORKDIR}"

inherit user-key-store

ALLOW_EMPTY_${PN} = "1"

KEY_DIR = "${sysconfdir}/keys"
# For RPM verification
RPM_KEY_DIR = "${sysconfdir}/pki/rpm-gpg"

# For ${PN}-system-trusted-cert
SYSTEM_CERT = "${KEY_DIR}/system_trusted_key.crt"

# For ${PN}-secondary-trusted-cert
SECONDARY_TRUSTED_CERT = "${KEY_DIR}/secondary_trusted_key.crt"
SECONDARY_TRUSTED_DER_ENC_CERT = "${KEY_DIR}/x509_secondary_system_trusted_key.der"

# For ${PN}-modsign-cert
MODSIGN_CERT = "${KEY_DIR}/modsign_key.crt"

# For ${PN}-ima-cert
IMA_CERT = "${KEY_DIR}/x509_ima.der"

python () {
    if not (uks_signing_model(d) in "sample", "user"):
        return

    pn = d.getVar('PN', True) + '-rpm-pubkey'
    d.setVar('PACKAGES_prepend', pn + ' ')
    d.setVar('FILES_' + pn, d.getVar('RPM_KEY_DIR', True) + '/RPM-GPG-KEY-' + d.getVar('RPM_GPG_NAME', True))
    d.setVar('CONFFILES_' + pn, d.getVar('RPM_KEY_DIR', True) + '/RPM-GPG-KEY-' + d.getVar('RPM_GPG_NAME', True))
    mlprefix = d.getVar('MLPREFIX')
    d.appendVar('RDEPENDS_' + pn, ' %srpm' % mlprefix)
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

    key_dir="${@uks_secondary_trusted_keys_dir(d)}"
    install -m 0644 "$key_dir/secondary_trusted_key.crt" \
        "${D}${SECONDARY_TRUSTED_CERT}"
    openssl x509 -inform PEM -outform DER -in "${D}${SECONDARY_TRUSTED_CERT}" \
        -out "${D}${SECONDARY_TRUSTED_DER_ENC_CERT}"

    key_dir="${@uks_modsign_keys_dir(d)}"
    install -m 0644 "$key_dir/modsign_key.crt" \
        "${D}${MODSIGN_CERT}"

    key_dir="${@uks_ima_keys_dir(d)}"
    install -m 0644 "$key_dir/x509_ima.der" "${D}${IMA_CERT}"
}

do_install[prefuncs] += "check_deploy_keys"

SYSROOT_PREPROCESS_FUNCS += "key_store_sysroot_preprocess"

key_store_sysroot_preprocess() {
    sysroot_stage_dir "${D}${sysconfdir}" "${SYSROOT_DESTDIR}${sysconfdir}"
}

pkg_postinst_ontarget_${PN}-rpm-pubkey() {
    keydir="${RPM_KEY_DIR}"

    [ ! -d "$keydir" ] && mkdir -p "$keydir"

    # XXX: only import the new key
    for keyfile in `ls $keydir/RPM-GPG-KEY-*`; do
        [ -s "$keyfile" ] || continue

        rpm --import "$keyfile" || {
            echo "Unable to import the public key $keyfile"
            exit 1
        }
    done
}

PACKAGES = "\
    ${PN}-system-trusted-cert \
    ${PN}-secondary-trusted-cert \
    ${PN}-modsign-cert \
    ${PN}-ima-cert \
"

# Note any private key is not available if user key signing model used.
PACKAGES_DYNAMIC = "\
    ${PN}-rpm-pubkey \
"

FILES_${PN}-system-trusted-cert = "${SYSTEM_CERT}"
CONFFILES_${PN}-system-trusted-cert = "${SYSTEM_CERT}"

FILES_${PN}-secondary-trusted-cert = "\
    ${SECONDARY_TRUSTED_CERT} \
    ${SECONDARY_TRUSTED_DER_ENC_CERT} \
    "
CONFFILES_${PN}-secondary-trusted-cert = "\
    ${SECONDARY_TRUSTED_CERT} \
    ${SECONDARY_TRUSTED_DER_ENC_CERT} \
    "

FILES_${PN}-modsign-cert = "${MODSIGN_CERT}"
CONFFILES_${PN}-modsign-cert = "${MODSIGN_CERT}"

FILES_${PN}-ima-cert = "${IMA_CERT}"
CONFFILES_${PN}-ima-cert = "${IMA_CERT}"
