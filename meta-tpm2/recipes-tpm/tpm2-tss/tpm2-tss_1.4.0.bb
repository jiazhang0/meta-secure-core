include ${BPN}.inc

LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=500b2e742befc3da00684d8a1d5fd9da"

PROVIDES = "${PACKAGES}"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "3fadb8ee8a4e506b9732d2a9dfcad77c"
SRC_URI[sha256sum] = "cf8784cc536be16e6fba47f77033e093a6aeaed8420877ac9f42f77fb7b09031"

S = "${WORKDIR}/${BPN}-${PV}"

PACKAGES = " \
    ${PN}-dbg \
    ${PN}-doc \
    libtss2 \
    libtss2-dev \
    libtss2-staticdev \
    libtctidevice \
    libtctidevice-dev \
    libtctidevice-staticdev \
    libtctisocket \
    libtctisocket-dev \
    libtctisocket-staticdev \
    "

FILES_libtss2 = " \
    ${libdir}/libsapi.so.* \
    "
FILES_libtss2-dev = " \
    ${includedir}/sapi \
    ${includedir}/tcti/common.h \
    ${libdir}/libsapi.so \
    ${libdir}/pkgconfig/sapi.pc \
    "
FILES_libtss2-staticdev = " \
    ${libdir}/libsapi.a \
    ${libdir}/libsapi.la \
    "

FILES_libtctidevice = "${libdir}/libtcti-device.so.*"
FILES_libtctidevice-dev = " \
    ${includedir}/tcti/tcti_device.h \
    ${libdir}/libtcti-device.so \
    ${libdir}/pkgconfig/tcti-device.pc \
    "
FILES_libtctidevice-staticdev = "${libdir}/libtcti-device.*a"

FILES_libtctisocket = "${libdir}/libtcti-socket.so.*"
FILES_libtctisocket-dev = " \
    ${includedir}/tcti/tcti_socket.h \
    ${libdir}/libtcti-socket.so \
    ${libdir}/pkgconfig/tcti-socket.pc \
    "
FILES_libtctisocket-staticdev = "${libdir}/libtcti-socket.*a"
