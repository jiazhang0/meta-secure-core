SUMMARY = "Linux Key Management Utilities"
DESCRIPTION = "Keyutils is a set of utilities for managing the key retention \
facility in the kernel, which can be used by filesystems, block devices and \
more to gain and retain the authorization and encryption keys required to \
perform secure operations."
SECTION = "base"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENCE.GPL;md5=5f6e72824f5da505c1f4a7197f004b45"

PV = "1.5.9+git${SRCPV}"

SRC_URI = "\
    git://git.kernel.org/pub/scm/linux/kernel/git/dhowells/keyutils.git \
    file://keyutils_fix_library_install.patch \
    file://keyutils-fix-the-cflags-for-all-of-targets.patch \
"
SRC_URI_append_arm = " file://keyutils-remove-m32-m64.patch"
SRC_URI_append_aarch64 = " file://keyutils-remove-m32-m64.patch"
SRC_URI_append_mips = " file://keyutils-remove-m32-m64.patch"
SRC_URI_append_mips64 = " file://keyutils-remove-m32-m64.patch"
SRC_URI_append_x86 = " file://keyutils_fix_x86_cflags.patch"
SRC_URI_append_x86-64 = " file://keyutils_fix_x86-64_cflags.patch"
SRC_URI_append_powerpc = "file://keyutils-remove-m32-m64.patch"
SRCREV = "9209a0c8fd63afc59f644e078b40cec531409c30"

S = "${WORKDIR}/git"

inherit autotools-brokensep

INSTALL_FLAGS = "\
    LIBDIR=${libdir} \
    USRLIBDIR=${libdir} \
    BINDIR=${bindir} \
    SBINDIR=${sbindir} \
    ETCDIR=${sysconfdir} \
    SHAREDIR=${datadir} \
    MANDIR=${mandir} \
    INCLUDEDIR=${includedir} \
    DESTDIR=${D} \
"

do_install() {
    cd ${S} && oe_runmake ${INSTALL_FLAGS} install
}

FILES_${PN} += "${datadir}/request-key-debug.sh"

BBCLASSEXTEND = "native nativesdk"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "200"
ALTERNATIVE_${PN}-doc = "keyrings.7 persistent-keyring.7 process-keyring.7 \
                         session-keyring.7 thread-keyring.7 user-keyring.7 \
                         user-session-keyring.7 \
"
ALTERNATIVE_LINK_NAME[keyrings.7] = "${mandir}/man7/keyrings.7"
ALTERNATIVE_LINK_NAME[persistent-keyring.7] = "${mandir}/man7/persistent-keyring.7"
ALTERNATIVE_LINK_NAME[process-keyring.7] = "${mandir}/man7/process-keyring.7"
ALTERNATIVE_LINK_NAME[session-keyring.7] = "${mandir}/man7/session-keyring.7"
ALTERNATIVE_LINK_NAME[thread-keyring.7] = "${mandir}/man7/thread-keyring.7"
ALTERNATIVE_LINK_NAME[user-keyring.7] = "${mandir}/man7/user-keyring.7"
ALTERNATIVE_LINK_NAME[user-session-keyring.7] = "${mandir}/man7/user-session-keyring.7"
