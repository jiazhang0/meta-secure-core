DESCRIPTION = "Basic packagegroup for TCG TSS and utilities that use it."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
    file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420 \
"

inherit packagegroup

RDEPENDS_${PN} = "\
    trousers \
    tpm-tools \
    openssl-tpm-engine \
    rng-tools \
"

RRECOMMENDS_${PN} = "\
    kernel-module-tpm-rng \
    kernel-module-tpm-tis \
    kernel-module-tpm-atmel \
    kernel-module-tpm-infineon \
"
