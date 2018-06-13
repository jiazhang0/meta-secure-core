include ${BPN}.inc

DEPENDS += "libtctidevice libtctisocket libtss2"

SRC_URI += "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "3f5f2461fd98aca0add1187e4705c0de"
SRC_URI[sha256sum] = "859d777a0d2c5d78309c4a2f06879a1e914b41324ea8258920a778a1ad7e38ea"
