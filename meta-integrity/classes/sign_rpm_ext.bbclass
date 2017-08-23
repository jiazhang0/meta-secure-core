# RPM_GPG_NAME and RPM_GPG_PASSPHRASE must be configured in your build
# environment. By default, the values for the sample keys are configured
# in meta-signing-key.

RPM_SIGN_FILES = "${@bb.utils.contains('DISTRO_FEATURES', 'ima', '1', '0', d)}"
# By default, the values below are applicable for the sample keys provided
# by meta-signing-key.
RPM_FSK_PATH ?= "${@uks_ima_keys_dir(d) + 'x509_ima.key'}"
RPM_FSK_PASSWORD ?= "password"

inherit sign_rpm user-key-store

python () {
    gpg_path = d.getVar('GPG_PATH', True)
    if not gpg_path:
        gpg_path = d.getVar('TMPDIR', True) + '/.gnupg'
        d.setVar('GPG_PATH', gpg_path)

    if not os.path.exists(gpg_path):
        status, output = oe.utils.getstatusoutput('mkdir -m 0700 -p %s' % gpg_path)
        if status:
            raise bb.build.FuncFailed('Failed to create gpg keying %s: %s' %
                                      (gpg_path, output))
}

do_package_index[depends] += "signing-keys-native:do_check_public_keys"
do_package_write_rpm[depends] += "signing-keys-native:do_check_public_keys"
