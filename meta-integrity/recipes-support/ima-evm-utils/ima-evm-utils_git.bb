LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = " \
	git://git.code.sf.net/p/linux-ima/ima-evm-utils \
	file://0001-Don-t-build-man-pages.patch \
	file://0001-Install-evmctl-to-sbindir-rather-than-bindir.patch \
"
SRCREV = "3e2a67bdb0673581a97506262e62db098efef6d7"
PV = "1.0+git${SRCPV}"

S = "${WORKDIR}/git"

PACKAGES =+ "${PN}-evmctl.static"

DEPENDS += "openssl attr keyutils"
RDEPENDS_${PN}_class-target += "libcrypto libattr keyutils"

inherit pkgconfig autotools

# Specify any options you want to pass to the configure script using EXTRA_OECONF:
EXTRA_OECONF = ""

CFLAGS_remove += "-pie -fpie"

do_compile_append_class-target() {
    ${CC} ${CFLAGS} ${LDFLAGS} -static \
        -include config.h -L=${libdir} \
        -Wl,--start-group -lcrypto -lkeyutils -ldl \
        ${S}/src/evmctl.c ${S}/src/libimaevm.c \
        -Wl,--end-group -o ${B}/src/evmctl.static
}

do_install_append_class-target() {
    install -m 0700 ${B}/src/evmctl.static ${D}${sbindir}/evmctl.static
}

FILES_${PN}-dev += "${includedir}"
FILES_${PN}-evmctl.static = "${sbindir}/evmctl.static"

BBCLASSEXTEND = "native nativesdk"
