require packagegroup-tpm2.inc

RDEPENDS:${PN} += "\
    tpm2-abrmd \
    tpm2-tools \
    rng-tools \
"

RRECOMMENDS:${PN} += "\
    kernel-module-tpm-rng \
"
