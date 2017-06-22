SUMMARY = "Software stack for TPM2."
DESCRIPTION = "tpm2.0-tss like woah."
SECTION = "tpm"

# This is a lie. The source for this project is covered by several licenses.
# We're currently working on a way to make this clear for those consuming the
# project. Till then I'm using 'BSD' as a place holder since the Intel license
# is "BSD-like".
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/BSD;md5=3775480a712fc46a69647678acb234cb"

# This doesn't seem to work. Keeping it here for completeness. Remove once
# it's fixed upstream.
DEPENDS += "autoconf-archive pkgconfig"
#RDEPENDS_libtss2 += "libmarshal"
#RDEPENDS_libtctidevice += "libmarshal"

SRC_URI = " \
    git://github.com/01org/TPM2.0-TSS.git;protocol=http;branch=1.x;name=TPM2.0-TSS;destsuffix=TPM2.0-TSS \
"

# CAPS? SRSLY?
S = "${WORKDIR}/${@d.getVar('BPN',d).upper()}"

SRCREV = "1fa2f4d12449d5d639032fee28d922fe9d4877b5"
PV = "1.1.0+git${SRCPV}"

RRECOMMENDS_${PN} += "\
    kernel-module-tpm-crb \
    kernel-module-tpm-tis \
"

TPM_DESCRIPTION = 'device/description'
FAMILY_MAJOR = 'TPM 2.0'

PACKAGES = " \
    ${PN}-dbg \
    libtss2 \
    libtss2-dev \
    libtss2-staticdev \
    libtss2-doc \
    libtctidevice \
    libtctidevice-dev \
    libtctidevice-staticdev \
    libtctisocket \
    libtctisocket-dev \
    libtctisocket-staticdev \
    libmarshal \
    libmarshal-dev \
    libmarshal-staticdev \
"

FILES_libtss2 = "${libdir}/libsapi.so.*"
FILES_libtss2-dev = " \
    ${includedir}/sapi \
    ${includedir}/tcti/common.h \
    ${libdir}/libsapi.so \
    ${libdir}/pkgconfig/sapi.pc \
    ${libdir}/libsapi.la \
"
FILES_libtss2-staticdev = " \
    ${libdir}/libsapi.a \
"
FILES_libtss2-doc = " \
    ${mandir} \
"
FILES_libtctidevice = "${libdir}/libtcti-device.so.*"
FILES_libtctidevice-dev = " \
    ${includedir}/tcti/tcti_device.h \
    ${libdir}/libtcti-device.so \
    ${libdir}/pkgconfig/tcti-device.pc \
    ${libdir}/libtcti-device.la \
"
FILES_libtctidevice-staticdev = "${libdir}/libtcti-device.a"
FILES_libtctisocket = "${libdir}/libtcti-socket.so.*"
FILES_libtctisocket-dev = " \
    ${includedir}/tcti/tcti_socket.h \
    ${libdir}/libtcti-socket.so \
    ${libdir}/pkgconfig/tcti-socket.pc \
    ${libdir}/libtcti-socket.la \
"
FILES_libtctisocket-staticdev = "${libdir}/libtcti-socket.a"
FILES_libmarshal = "${libdir}/libmarshal.so.*"
FILES_libmarshal-dev = "${libdir}/libmarshal.la ${libdir}/libmarshal.so"
FILES_libmarshal-staticdev = "${libdir}/libmarshal.a"

inherit autotools

# the autotools / autoconf-archive don't work as expected so we include the
# pthread macro ourselves for now
SRC_URI += " \
    file://ax_pthread.m4 \
    file://ax_check_compile_flag.m4 \
    file://ax_check_preproc_flag.m4 \
    file://ax_check_link_flag.m4 \
"
do_configure_prepend () {
	mkdir -p ${S}/m4
	cp ${WORKDIR}/ax_pthread.m4 ${S}/m4
	cp ${WORKDIR}/ax_check_compile_flag.m4 ${S}/m4
	cp ${WORKDIR}/ax_check_preproc_flag.m4 ${S}/m4
	cp ${WORKDIR}/ax_check_link_flag.m4 ${S}/m4
	# execute the bootstrap script
	currentdir=$(pwd)
	cd ${S}
	ACLOCAL="aclocal --system-acdir=${STAGING_DATADIR}/aclocal" ./bootstrap --force
	cd ${currentdir}
}

