SUMMARY = "The TCG TPM2 Software Stack"
DESCRIPTION = "The TCG (Trusted Computing Group) Software Stack (TSS) \
provides a standard API for accessing the functions of the TPM. \
Application developers can use this software to develop interoperable \
client applications."
HOMEPAGE = "https://github.com/tpm2-software/tpm2-tss"
SECTION = "security/tpm"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=0b1d631c4218b72f6b05cb58613606f4"

DEPENDS = "autoconf-archive-native libgcrypt"

PROVIDES = "${PACKAGES}"

inherit autotools pkgconfig

EXTRA_OECONF += " \
    --with-udevrulesdir=${sysconfdir}/udev/rules.d \
"

PACKAGES = " \
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
    ${PN} \
    ${PN}-doc \
    ${PN}-dbg \
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
