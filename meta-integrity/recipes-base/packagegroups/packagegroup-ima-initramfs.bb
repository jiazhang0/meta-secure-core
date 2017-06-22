DESCRIPTION = "Linux Integrity Measurement Architecture (IMA) subsystem for initramfs"

include packagegroup-ima.inc

RDEPENDS_${PN} += " \
    util-linux-mount \
    util-linux-umount \
    gawk \
    ima-policy \
    key-store-ima-cert \
    initrdscripts-ima \
"
