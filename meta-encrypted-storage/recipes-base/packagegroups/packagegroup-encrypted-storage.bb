DESCRIPTION = "The packages used for encrypted storage."

require packagegroup-encrypted-storage.inc

# Install the minimal stuffs only for the linux rootfs.
# The common packages shared between initramfs and rootfs
# are listed in the .inc.

RDEPENDS_${PN} += "\
    util-linux-fdisk \
    parted \
    packagegroup-tpm2 \
"
