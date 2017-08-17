# RPM_GPG_NAME and RPM_GPG_PASSPHRASE must be configured in your build
# environment. By default, the values for the sample keys are configured
# in meta-signing-key.
RPM_GPG_NAME ?= "SecureCore"
RPM_GPG_PASSPHRASE ?= "SecureCore"

RPM_GPG_BACKEND ?= "local"
# SHA-256 is used for the file checksum digest.
RPM_FILE_CHECKSUM_DIGEST ?= "8"

RPM_SIGN_FILES = "${@bb.utils.contains('DISTRO_FEATURES', 'ima', '1', '0', d)}"
# By default, the values below are applicable for the sample keys provided
# by meta-signing-key.
RPM_FSK_PATH ?= "${@uks_ima_keys_dir(d) + 'x509_ima.key'}"
RPM_FSK_PASSWORD ?= "password"

inherit sign_rpm user-key-store
