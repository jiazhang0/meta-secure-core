# RPM_GPG_NAME and RPM_GPG_PASSPHRASE must be configured in your build
# environment. By default, the values for the sample keys are configured
# in meta-signing-key.

RPM_SIGN_FILES = "${@bb.utils.contains('DISTRO_FEATURES', 'ima', '1', '0', d)}"
# By default, the values below are applicable for the sample keys provided
# by meta-signing-key.
RPM_FSK_PATH ?= "${@uks_ima_keys_dir(d) + 'x509_ima.key'}"
RPM_FSK_PASSWORD ?= "password"

inherit sign_rpm user-key-store

GPG_DEP = "${@'' if d.getVar('GPG_BIN') else 'gnupg-native:do_populate_sysroot pinentry-native:do_populate_sysroot'}"

python check_rpm_public_key () {
    check_gpg_key('RPM', uks_rpm_keys_dir, d)
}

check_rpm_public_key[lockfiles] = "${TMPDIR}/gpg_key.lock"
check_rpm_public_key[prefuncs] += "check_deploy_keys"
do_package_write_rpm[depends] += "${GPG_DEP}"
do_rootfs[depends] += "${GPG_DEP}"

python do_package_write_rpm_prepend() {
    bb.build.exec_func("check_rpm_public_key", d)
}

python do_rootfs_prepend() {
    bb.build.exec_func("check_rpm_public_key", d)
}

python () {
    gpg_path = d.getVar('GPG_PATH', True)
    if not gpg_path:
        gpg_path = d.getVar('TMPDIR', True) + '/.gnupg'
        d.setVar('GPG_PATH', gpg_path)
}
