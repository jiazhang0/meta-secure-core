require ${@bb.utils.contains('DISTRO_FEATURES', 'efi-secure-boot', 'linux-yocto-efi-secure-boot.inc', '', d)}
