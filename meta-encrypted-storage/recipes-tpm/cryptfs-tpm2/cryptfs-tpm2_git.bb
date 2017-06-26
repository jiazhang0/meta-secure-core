SUMMARY = "A tool used to create, persist, evict a passphrase \
for full-disk-encryption with TPM 2.0"
DESCRIPTION = " \
This project provides with an implementation for \
creating, persisting and evicting a passphrase with TPM 2.0. \
The passphrase and its associated primary key are automatically \
created by RNG engine in TPM. In order to avoid saving the \
context file, the created passphrase and primary key are always \
persistent in TPM. \
"
SECTION = "devel"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=89c8ce1346a3dfe75379e84f3ba9d641"

SRC_URI = " \
    git://github.com/WindRiver-OpenSourceLabs/cryptfs-tpm2.git \
"
SRCREV = "5ee0580eb94dc9cb6629b6b5e6de0a3a2eef2b64"
PV = "0.6.0+git${SRCPV}"

DEPENDS += "tpm2.0-tss tpm2-abrmd pkgconfig-native"
RDEPENDS_${PN} += "libtss2 libtctidevice libtctisocket"

PACKAGES =+ " \
    ${PN}-initramfs \
"

PARALLEL_MAKE = ""

S = "${WORKDIR}/git"

EXTRA_OEMAKE = " \
    sbindir="${sbindir}" \
    libdir="${libdir}" \
    includedir="${includedir}" \
    tpm2_tss_includedir="${STAGING_INCDIR}/sapi" \
    tpm2_tss_libdir="${STAGING_LIBDIR}" \
    tpm2_tabrmd_includedir="${STAGING_INCDIR}" \
    CC="${CC}" \
    PKG_CONFIG="${STAGING_BINDIR_NATIVE}/pkg-config" \
    EXTRA_CFLAGS="${CFLAGS}" \
    EXTRA_LDFLAGS="${LDFLAGS}" \
"

do_install() {
    oe_runmake install DESTDIR="${D}"

    if [ x"${@bb.utils.contains('DISTRO_FEATURES', 'encrypted-storage', '1', '0', d)}" = x"1" ]; then
        install -m 0500 ${S}/script/init.cryptfs ${D}
    fi
}

FILES_${PN}-initramfs = "\
    /init.cryptfs \
"
