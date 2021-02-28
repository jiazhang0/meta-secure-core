SUMMARY = "Utilities for signing UEFI binaries for use with secure boot"

LICENSE = "GPLv3"

LIC_FILES_CHKSUM = "\
    file://LICENSE.GPLv3;md5=9eef91148a9b14ec7f9df333daebc746 \
    file://COPYING;md5=a7710ac18adec371b84a9594ed04fd20 \
"

DEPENDS += "binutils openssl gnu-efi gnu-efi-native"
DEPENDS += "help2man-native coreutils-native openssl-native util-linux-native"

PV = "0.8+git${SRCPV}"

SRC_URI = "\
    git://git.kernel.org/pub/scm/linux/kernel/git/jejb/sbsigntools.git;protocol=https;name=sbsigntool \
"
SRCREV="f12484869c9590682ac3253d583bf59b890bb826"

S = "${WORKDIR}/git"

inherit native  autotools-brokensep pkgconfig

def efi_arch(d):
    import re
    arch = d.getVar("TARGET_ARCH")
    if re.match("i[3456789]86", arch):
        return "ia32"
    return arch

# Avoids build breaks when using no-static-libs.inc
#DISABLE_STATIC_class-target = ""

#EXTRA_OECONF_remove_class-target += "\
#    --with-libtool-sysroot \
#"

EXTRA_OEMAKE += "\
    INCLUDES='-I${S}/lib/ccan.git' \
    EFI_CPPFLAGS='-I${STAGING_INCDIR}/efi \
                  -I${STAGING_INCDIR}/efi/${@efi_arch(d)}' \
"

do_configure() {
    cd "${S}"
    ./autogen.sh
    oe_runconf
}

BBCLASSEXTEND = "native nativesdk"
