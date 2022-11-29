SUMMARY = "The bootloader capable of authenticating the PE and non-PE files."
DESCRIPTION = "The SELoader is designed to authenticate the non-PE files, \
such as grub configuration, initrd, grub modules, which cannot be verified \
by the MOK Verify Protocol registered by shim loader. \
\
In order to conveniently authenticate the PE file with gBS->LoadImage() \
and gBS->StartImage(), the SELoader hooks EFI Security2 Architectural \
Protocol and employs MOK Verify Protocol to verify the PE file. If only \
UEFI Secure Boot is enabled, the SELoader just simplily calls \
gBS->LoadImage() and gBS->StartImage() to allow BIOS to verify PE file. \
\
The SELoader publishes MOK2 Verify Protocol which provides a flexible \
interface to allow the bootloader to verify the file, file buffer or \
memory buffer without knowing the file format. \
"
AUTHOR = "Jia Zhang"
HOMEPAGE = "https://github.com/jiazhang0/SELoader"
SECTION = "bootloaders"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9bf404642f21afb4ad89f95d7bc91ee"

DEPENDS += "\
    gnu-efi sbsigntool-native openssl-native ovmf \
"

PV = "0.4.6+git${SRCPV}"

SRC_URI = "\
    git://github.com/jiazhang0/SELoader.git;branch=master;protocol=https \
"
SRCREV = "8b90f76a8df51d9020e67824026556434f407086"

S = "${WORKDIR}/git"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'
PARALLEL_MAKE = ""

inherit deploy user-key-store

EXTRA_OEMAKE = "\
    CROSS_COMPILE="${TARGET_PREFIX}" \
    SBSIGN=${STAGING_BINDIR_NATIVE}/sbsign \
    gnuefi_libdir=${STAGING_LIBDIR} \
    LIB_GCC="`${CC} -print-libgcc-file-name`" \
"

EFI_ARCH:x86 = "ia32"
EFI_ARCH:x86-64 = "x64"

EFI_TARGET = "/boot/efi/EFI/BOOT"

python do_sign() {
    sb_sign(d.expand('${B}/Src/Efi/SELoader.efi'), \
            d.expand('${B}/Src/Efi/SELoader.efi.signed'), d)
}
addtask sign after do_compile before do_install
do_sign[prefuncs] += "check_deploy_keys"

do_install() {
    install -d ${D}${EFI_TARGET}

    oe_runmake install EFI_DESTDIR=${D}${EFI_TARGET}
    # Remove precompiled files, now provided by OVMF
    rm -f ${D}${EFI_TARGET}/Hash2DxeCrypto.efi
    rm -f ${D}${EFI_TARGET}/Pkcs7VerifyDxe.efi

    if [ x"${UEFI_SB}" = x"1" ]; then
        if [ x"${MOK_SB}" != x"1" ]; then
            mv "${D}${EFI_TARGET}/SELoader${EFI_ARCH}.efi" \
                "${D}${EFI_TARGET}/boot${EFI_ARCH}.efi"
        fi
    fi
}

do_deploy() {
    # Deploy the unsigned images for manual signing
    install -d "${DEPLOYDIR}/efi-unsigned"

    install -m 0600 "${B}/Src/Efi/SELoader.efi" \
        "${DEPLOYDIR}/efi-unsigned/SELoader${EFI_ARCH}.efi"

    # Deploy the signed images
    if [ x"${UEFI_SB}" = x"1" -a x"${MOK_SB}" != x"1" ]; then
        SEL_NAME=boot
    else
        SEL_NAME=SELoader
    fi
    install -m 0600 "${D}${EFI_TARGET}/${SEL_NAME}${EFI_ARCH}.efi" \
        "${DEPLOYDIR}/${SEL_NAME}${EFI_ARCH}.efi"
}
addtask deploy after do_install before do_build

RDEPENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'efi-secure-boot', 'ovmf-pkcs7-efi', '', d)}"

FILES:${PN} += "${EFI_TARGET}"

SSTATE_ALLOW_OVERLAP_FILES += "${DEPLOY_DIR_IMAGE}/efi-unsigned"
