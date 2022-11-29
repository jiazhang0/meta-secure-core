include ${@bb.utils.contains('DISTRO_FEATURES', 'luks', '${BPN}-luks.inc', '', d)}
