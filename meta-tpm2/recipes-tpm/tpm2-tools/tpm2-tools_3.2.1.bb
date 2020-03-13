include ${BPN}.inc

SRC_URI = "\
    https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz \
    file://0001-test-fix-yaml.load-warning.patch \
"
SRC_URI[md5sum] = "17f22e9b47682f4601eb55324282ad6e"
SRC_URI[sha256sum] = "568ff32f99e0835db5d8cea2dce781b6cd6c1034026514240995dae5d9e728b0"

S = "${WORKDIR}/${BPN}-${PV}"
