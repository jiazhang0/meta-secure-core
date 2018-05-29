require packagegroup-tpm2.inc

RDEPENDS_${PN} += "\
    tpm2-abrmd \
    tpm2-tools \
    rng-tools \
"

RRECOMMENDS_${PN} += "\
    kernel-module-tpm-rng \
"
