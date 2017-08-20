DESCRIPTION = "The packages used for luks in initramfs."

require packagegroup-luks.inc

RDEPENDS_${PN} += "\
    cryptfs-tpm2-initramfs \
    packagegroup-tpm2-initramfs \
"
