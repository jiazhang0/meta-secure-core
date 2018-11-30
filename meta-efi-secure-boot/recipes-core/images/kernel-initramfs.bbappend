require ${@bb.utils.contains('DISTRO_FEATURES', 'efi-secure-boot', 'kernel-initramfs-efi-secure-boot.inc', '', d)}
