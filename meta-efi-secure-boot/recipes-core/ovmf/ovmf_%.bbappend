require ${@bb.utils.contains('DISTRO_FEATURES', 'efi-secure-boot', '${BPN}-efi-secure-boot.inc', '', d)}
