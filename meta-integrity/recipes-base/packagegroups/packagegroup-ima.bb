DESCRIPTION = "Linux Integrity Measurement Architecture (IMA) subsystem"

include packagegroup-ima.inc

DEPENDS += "\
    ima-evm-utils-native \
    attr-native \
"

RDEPENDS_${PN} += "\
    attr \
    util-linux-switch-root.static \
"

# Note any private key is not available if user key signing model used.
RRECOMMENDS_${PN} += "\
    key-store-ima-privkey \
    key-store-system-trusted-privkey \
"
