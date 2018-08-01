include ${BPN}.inc

SRC_URI += " \
    https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz \
    file://tpm2-abrmd.default \
"
SRC_URI[md5sum] = "533bb7b16e9335c32f67e80961542e19"
SRC_URI[sha256sum] = "b012a6c3e4462a411eaafd3dc8d3b13ef4118348acfd5108b68a57c8c0a5ed9c"
