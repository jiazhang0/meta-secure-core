include packagegroup-encrypted-storage.inc

DESCRIPTION = "The packages used for encrypted storage."

# Install the minimal stuffs only for the linux rootfs.
# The common packages shared between initramfs and rootfs
# are listed in the .inc.
# @util-linux: fdisk
# @parted: parted
RDEPENDS_${PN} += " \
    util-linux-fdisk \
    parted \
    packagegroup-tpm2 \
"
