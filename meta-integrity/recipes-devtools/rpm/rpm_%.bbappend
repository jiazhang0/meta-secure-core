require ${@bb.utils.contains('DISTRO_FEATURES', 'ima', 'rpm-integrity.inc', '', d)}
