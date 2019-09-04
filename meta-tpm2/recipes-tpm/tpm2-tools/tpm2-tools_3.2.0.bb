include ${BPN}.inc

SRC_URI = "\
    https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz \
"
SRC_URI[md5sum] = "af389756402fa26aa3f08aa4abfc5d88"
SRC_URI[sha256sum] = "ad79ee83e2d4b34302e8883eaf313b27dbfabfd9cbc8ebcd95cf78fa097aef14"

S = "${WORKDIR}/${BPN}-${PV}"
