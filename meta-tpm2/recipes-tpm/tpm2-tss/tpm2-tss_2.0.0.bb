include ${BPN}.inc

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "048ea77be36f881b7b6ecefbc1cf7dbd"
SRC_URI[sha256sum] = "7dfd05f7d2c4d5339d1c9ecbdba25f4ea6df70e96b09928e15e0560cce02d525"

S = "${WORKDIR}/${BPN}-${PV}"
