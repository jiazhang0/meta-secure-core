require ${@bb.utils.contains('DISTRO_FEATURES', 'ima', 'base-files-integrity.inc', '', d)}
