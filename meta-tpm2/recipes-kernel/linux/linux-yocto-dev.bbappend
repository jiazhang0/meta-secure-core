require ${@bb.utils.contains('DISTRO_FEATURES', 'tpm2', 'linux-yocto-tpm2.inc', '', d)}
