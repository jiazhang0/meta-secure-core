include ${BPN}.inc

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "f7a962c6e3d2997efe8949ac7aec8283"
SRC_URI[sha256sum] = "ac05028347a9fa1da79b5d53b998193de0c3a76000badb961c3feb8b8a0e8e8e"

S = "${WORKDIR}/${BPN}-${PV}"
