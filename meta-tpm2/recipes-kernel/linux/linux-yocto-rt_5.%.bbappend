require ${@bb.utils.contains('DISTRO_FEATURES', 'tpm2', '${BPN}-tpm2.inc', '', d)}
