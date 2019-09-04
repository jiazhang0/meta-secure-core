include ${BPN}.inc

LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=500b2e742befc3da00684d8a1d5fd9da"
SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz \
           file://0001-build-update-for-ax_code_coverage.m4-version-2019.01.patch \
"

SRC_URI[md5sum] = "593873bb023a0f8bcb93d12bc6640918"
SRC_URI[sha256sum] = "1369aee648b33128b9ee8e3ad87f5fc6dc37c2077b9f134223ea04f4809a99c3"

S = "${WORKDIR}/${BPN}-${PV}"
