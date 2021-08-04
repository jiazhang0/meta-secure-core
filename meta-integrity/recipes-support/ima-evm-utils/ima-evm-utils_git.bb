LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "openssl attr keyutils"

PV = "1.2.1+git${SRCPV}"

SRC_URI = "\
    git://git.code.sf.net/p/linux-ima/ima-evm-utils;branch=ima-evm-utils-1.2.y \
    file://0001-Don-t-build-man-pages.patch \
    file://0001-Install-evmctl-to-sbindir-rather-than-bindir.patch \
    file://0001-ima-evm-utils-include-sys-types.h-in-header-to-fix-b.patch \
    file://0001-libimaevm-retrieve-correct-algo-for-v2-signature.patch \
"
SRCREV = "3eab1f93b634249c1720f65fcb495b1996f0256e"

S = "${WORKDIR}/git"

inherit pkgconfig autotools

# Specify any options you want to pass to the configure script using EXTRA_OECONF:
EXTRA_OECONF = ""

FILES:${PN}-dev += "${includedir}"

RDEPENDS:${PN}:class-target += "libcrypto libattr keyutils"

BBCLASSEXTEND = "native nativesdk"
