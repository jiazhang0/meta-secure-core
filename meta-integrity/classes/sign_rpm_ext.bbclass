#DEPENDS += "gnupg-native"

#RPM_GPG_NAME ?= "SecureCore Sample RPM Signing Key"
#RPM_GPG_PASSPHRASE ?= "password"
RPM_GPG_NAME ?= "testkey"
RPM_GPG_PASSPHRASE ?= "123456"
RPM_GPG_BACKEND ?= "local"
# SHA-256 is used for the file checksum digest.
RPM_FILE_CHECKSUM_DIGEST ?= "8"

RPM_SIGN_FILES = "${@bb.utils.contains('DISTRO_FEATURES', 'ima', '1', '0', d)}"
RPM_FSK_PATH ?= "${@uks_ima_keys_dir(d) + 'x509_ima.key'}"
RPM_FSK_PASSWORD ?= "password"

inherit sign_rpm user-key-store

#python () {
#    if not d.getVar('GPG_PATH', True):
#        d.setVar('GPG_PATH', d.getVar('DEPLOY_DIR_IMAGE', True) + '/.gnupg')
#}
