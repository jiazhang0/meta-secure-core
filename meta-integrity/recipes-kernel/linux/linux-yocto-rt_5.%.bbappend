require ${@bb.utils.contains('DISTRO_FEATURES', 'ima', '${BPN}-integrity.inc', '', d)}
