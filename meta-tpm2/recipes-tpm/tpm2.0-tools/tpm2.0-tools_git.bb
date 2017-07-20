SUMMARY = "Tools for TPM2."
DESCRIPTION = "tpm2.0-tools"
SECTION = "security/tpm"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=91b7c548d73ea16537799e8060cea819"

DEPENDS += "tpm2.0-tss tpm2-abrmd openssl curl autoconf-archive pkgconfig"

PV = "2.1.0+git${SRCPV}"

SRC_URI = "\
    git://github.com/01org/tpm2.0-tools.git \
    file://0001-Fix-build-failure-with-glib-2.0.patch \
    file://0002-tpm2-tools-use-dynamic-linkage-with-tpm2-abrmd.patch \
"
SRCREV = "97306d6dc1fc5f3142c50efe3189bd46ff35b5a0"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF += "\
    --with-tcti-device \
    --without-tcti-socket \
    --with-tcti-tabrmd \
"

EXTRA_OEMAKE += "\
    CFLAGS="${CFLAGS} -Wno-implicit-fallthrough" \
    LIBS=-ldl \
"

do_configure_prepend() {
    # execute the bootstrap script
    currentdir="$(pwd)"
    cd "${S}"
    ACLOCAL="aclocal --system-acdir=${STAGING_DATADIR}/aclocal" \
        ./bootstrap --force
    cd "${currentdir}"
}

RDEPENDS_${PN} += "libtss2 libtctidevice"
