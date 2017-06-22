SUMMARY = "A generic signing tool framework"
DESCRIPTION = " \
This project targets to provide a generic signing framework. This framework \
separates the signing request and signing process and correspondingly forms \
the so-called signlet and signaturelet. \
Each signaturelet only concerns about the details about how to construct the \
layout of a signature format, and signlet only cares how to construct the \
signing request. \
"
SECTION = "devel"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=d9bf404642f21afb4ad89f95d7bc91ee"

SRC_URI = " \
    git://github.com/jiazhang0/libsign.git \
"
SRCREV = "dfab84b4235a36bb395bc6663e50578bb2f9edca"
PV = "0.3.2+git${SRCPV}"

DEPENDS += "openssl"
RDEPENDS_${PN}_class-target += "libcrypto"
RDEPENDS_${PN}_class-native += "openssl"

PARALLEL_MAKE = ""

S = "${WORKDIR}/git"

EXTRA_OEMAKE = " \
    CC="${CC}" \
    bindir="${STAGING_BINDIR}" \
    libdir="${STAGING_LIBDIR}" \
    includedir="${STAGING_INCDIR}" \
    EXTRA_CFLAGS="${CFLAGS}" \
    EXTRA_LDFLAGS="${LDFLAGS}" \
    SIGNATURELET_DIR="${libdir}/signaturelet" \
    BINDIR="${bindir}" \
    LIBDIR="${libdir}" \
"

do_install() {
    oe_runmake install DESTDIR="${D}"
}

FILES_${PN} += " \
    ${libdir}/signaturelet \
"

BBCLASSEXTEND = "native"
