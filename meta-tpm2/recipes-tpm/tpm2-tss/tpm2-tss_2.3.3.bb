SUMMARY = "Software stack for TPM2."
DESCRIPTION = "OSS implementation of the TCG TPM2 Software Stack (TSS2) "
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=500b2e742befc3da00684d8a1d5fd9da"
SECTION = "tpm"

DEPENDS = "autoconf-archive-native libgcrypt openssl"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "8dec49505bdb29acb61c837c5e8dce68"
SRC_URI[sha256sum] = "9d8c8866829e3ff5e1649ecd82498cb1329e0691139458e53b69e6061febe3d3"

inherit autotools pkgconfig systemd extrausers

PACKAGECONFIG ??= ""
PACKAGECONFIG[oxygen] = ",--disable-doxygen-doc, "

EXTRA_OECONF += "--enable-static --with-udevrulesdir=${nonarch_base_libdir}/udev/rules.d/"
EXTRA_OECONF_remove = " --disable-static"


EXTRA_USERS_PARAMS = "\
	useradd -p '' tss; \
	groupadd tss; \
	"

PROVIDES = "${PACKAGES}"
PACKAGES = " \
    ${PN} \
    ${PN}-dbg \
    ${PN}-doc \
    libtss2-mu \
    libtss2-mu-dev \
    libtss2-mu-staticdev \
    libtss2-tcti-device \
    libtss2-tcti-device-dev \
    libtss2-tcti-device-staticdev \
    libtss2-tcti-mssim \
    libtss2-tcti-mssim-dev \
    libtss2-tcti-mssim-staticdev \
    libtss2 \
    libtss2-dev \
    libtss2-staticdev \
"

FILES_libtss2-tcti-device = "${libdir}/libtss2-tcti-device.so.*"
FILES_libtss2-tcti-device-dev = " \
    ${includedir}/tss2/tss2_tcti_device.h \
    ${libdir}/pkgconfig/tss2-tcti-device.pc \
    ${libdir}/libtss2-tcti-device.so"
FILES_libtss2-tcti-device-staticdev = "${libdir}/libtss2-tcti-device.*a"

FILES_libtss2-tcti-mssim = "${libdir}/libtss2-tcti-mssim.so.*"
FILES_libtss2-tcti-mssim-dev = " \
    ${includedir}/tss2/tss2_tcti_mssim.h \
    ${libdir}/pkgconfig/tss2-tcti-mssim.pc \
    ${libdir}/libtss2-tcti-mssim.so"
FILES_libtss2-tcti-mssim-staticdev = "${libdir}/libtss2-tcti-mssim.*a"

FILES_libtss2-mu = "${libdir}/libtss2-mu.so.*"
FILES_libtss2-mu-dev = " \
    ${includedir}/tss2/tss2_mu.h \
    ${libdir}/pkgconfig/tss2-mu.pc \
    ${libdir}/libtss2-mu.so"
FILES_libtss2-mu-staticdev = "${libdir}/libtss2-mu.*a"

FILES_libtss2 = "${libdir}/libtss2*so.*"
FILES_libtss2-dev = " \
    ${includedir} \
    ${libdir}/pkgconfig \
    ${libdir}/libtss2*so"
FILES_libtss2-staticdev = "${libdir}/libtss*a"

FILES_${PN} = "${libdir}/udev ${nonarch_base_libdir}/udev"

RDEPENDS_libtss2 = "libgcrypt"
