DESCRIPTION = "Linux Integrity Measurement Architecture (IMA) subsystem"

include packagegroup-ima.inc

DEPENDS += "\
    ima-evm-utils-native \
    attr-native \
"

RDEPENDS:${PN} += "\
    attr \
    ima-inspect \
    util-linux-switch-root.static \
"

# Note any private key is not available if user key signing model used.
RRECOMMENDS:${PN} += "\
    key-store-ima-cert \
    key-store-system-trusted-cert \
"
