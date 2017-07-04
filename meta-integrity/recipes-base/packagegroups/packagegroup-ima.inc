LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420 \
"

S = "${WORKDIR}"

ALLOW_EMPTY_${PN} = "1"

RDEPENDS_${PN} = "\
    ima-evm-utils \
"
