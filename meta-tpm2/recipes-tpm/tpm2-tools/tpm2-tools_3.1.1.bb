include ${BPN}.inc

SRC_URI = "\
    https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz \
"
SRC_URI[md5sum] = "ad9e856c4cbd8a19eb205d74ab635adc"
SRC_URI[sha256sum] = "c7f0cdca51ef2006503f60c462b6d183c9b9dc038f4c3f74a89c111088fed8aa"

S = "${WORKDIR}/${BPN}-${PV}"
