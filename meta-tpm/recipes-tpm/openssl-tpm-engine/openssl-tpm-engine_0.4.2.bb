DESCRIPTION = " OpenSSL secure engine based on TPM hardware"
HOMEPAGE = "http://www.openssl.org/"
SECTION = "libs/network"
LICENSE = "openssl"

DEPENDS += "openssl trousers"
RDEPENDS_${PN} += "libcrypto libtspi"

SRC_URI = "\
    http://sourceforge.net/projects/trousers/files/OpenSSL%20TPM%20Engine/0.4.2/openssl_tpm_engine-0.4.2.tar.gz \
    file://0001-create-tpm-key-support-well-known-key-option.patch \
    file://0002-libtpm-support-env-TPM_SRK_PW.patch \
    file://0003-Fix-not-building-libtpm.la.patch \
"
SRC_URI[md5sum] = "5bc8d66399e517dde25ff55ce4c6560f"
SRC_URI[sha256sum] = "2df697e583053f7047a89daa4585e21fc67cf4397ee34ece94cf2d4b4f7ab49c"
LIC_FILES_CHKSUM = "file://LICENSE;md5=11f0ee3af475c85b907426e285c9bb52"

inherit autotools-brokensep

S = "${WORKDIR}/openssl_tpm_engine-${PV}"

do_configure_prepend() {
    cd "${S}"
    cp LICENSE COPYING
    touch NEWS AUTHORS ChangeLog
}

FILES_${PN}-staticdev += "${libdir}/ssl/engines/libtpm.la"
FILES_${PN}-dbg += "${libdir}/ssl/engines/.debug \
	${libdir}/engines/.debug \
	${prefix}/local/ssl/lib/engines/.debug \
"
FILES_${PN} += "${libdir}/ssl/engines/libtpm.so* \
	${libdir}/engines/libtpm.so* \
	${libdir}/libtpm.so* \
	${prefix}/local/ssl/lib/engines/libtpm.so* \
"

do_install_append() {
    install -m 0755 -d "${D}${libdir}/engines"
    install -m 0755 -d "${D}${prefix}/local/ssl/lib/engines"
    install -m 0755 -d "${D}${libdir}/ssl/engines"

    cp -f "${D}${libdir}/openssl/engines/libtpm.so.0.0.0" "${D}${libdir}/libtpm.so.0"
    cp -f "${D}${libdir}/openssl/engines/libtpm.so.0.0.0" "${D}${libdir}/engines/libtpm.so"
    cp -f "${D}${libdir}/openssl/engines/libtpm.so.0.0.0" "${D}${prefix}/local/ssl/lib/engines/libtpm.so"
    mv -f "${D}${libdir}/openssl/engines/libtpm.so.0.0.0" "${D}${libdir}/ssl/engines/libtpm.so"
    mv -f "${D}${libdir}/openssl/engines/libtpm.la" "${D}${libdir}/ssl/engines/libtpm.la"
    rm -rf "${D}${libdir}/openssl"
}

INSANE_SKIP_${PN} = "libdir"
INSANE_SKIP_${PN}-dbg = "libdir"
