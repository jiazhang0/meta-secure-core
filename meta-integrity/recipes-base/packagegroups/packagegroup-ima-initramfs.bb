DESCRIPTION = "Linux Integrity Measurement Architecture (IMA) subsystem for initramfs"

include packagegroup-ima.inc

RDEPENDS:${PN} += "\
    initrdscripts-ima \
"
