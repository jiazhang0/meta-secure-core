SUMMARY = "Tools for TPM2."
DESCRIPTION = "tpm2.0-tools"
SECTION = "tpm"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=91b7c548d73ea16537799e8060cea819"

DEPENDS += "tpm2.0-tss tpm2-abrmd openssl curl autoconf-archive pkgconfig"
RDEPENDS_${PN} += "libtss2 libtctidevice"

SRC_URI = " \
    git://github.com/01org/tpm2.0-tools.git;branch=master;name=tpm2.0-tools;destsuffix=tpm2.0-tools \
    file://ax_check_compile_flag.m4 \
    file://ax_check_preproc_flag.m4 \
    file://ax_check_link_flag.m4 \
    file://0001-tpm2-tools-use-dynamic-linkage-with-tpm2-abrmd.patch \
    file://0002-Fix-build-failure-with-glib-2.0.patch \
"

S = "${WORKDIR}/${BPN}"
SRCREV = "ada4c20d23d99b4b489c6c793e4132c1d5234b66"
PV = "2.0.0+git${SRCPV}"

inherit autotools pkgconfig

EXTRA_OECONF += " \
    --with-tcti-device \
    --without-tcti-socket \
    --with-tcti-tabrmd \
"

EXTRA_OEMAKE += " \
    CFLAGS="${CFLAGS} -Wno-implicit-fallthrough" \
"

do_configure_prepend() {
    mkdir -p "${S}/m4"
    cp "${WORKDIR}/ax_check_compile_flag.m4" "${S}/m4"
    cp "${WORKDIR}/ax_check_preproc_flag.m4" "${S}/m4"
    cp "${WORKDIR}/ax_check_link_flag.m4" "${S}/m4"

    # execute the bootstrap script
    currentdir=$(pwd)
    cd "${S}"
    ACLOCAL="aclocal --system-acdir=${STAGING_DATADIR}/aclocal" \
        ./bootstrap --force
    cd "${currentdir}"
}
