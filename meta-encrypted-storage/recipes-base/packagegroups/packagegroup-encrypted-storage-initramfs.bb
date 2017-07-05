DESCRIPTION = "The packages used for encrypted storage in initramfs."

require packagegroup-encrypted-storage.inc

RDEPENDS_${PN} += "\
    cryptfs-tpm2-initramfs \
    packagegroup-tpm2-initramfs \
"
