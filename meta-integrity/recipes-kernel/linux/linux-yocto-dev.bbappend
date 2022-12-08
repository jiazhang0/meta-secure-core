require ${@bb.utils.contains('DISTRO_FEATURES', 'ima', 'linux-yocto-integrity.inc', '', d)}
