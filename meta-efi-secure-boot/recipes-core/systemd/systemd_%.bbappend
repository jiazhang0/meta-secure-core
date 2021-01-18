require ${@bb.utils.contains('DISTRO_FEATURES', 'efi-secure-boot', 'systemd-efi-secure-boot.inc', '', d)}
