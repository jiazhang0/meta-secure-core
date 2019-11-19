SUMMARY = "Tools to support reading and manipulating the UEFI signature database"
DESCRIPTION = "\
From the EFI Tools package in the Linux user-space, it's now possible \
to read and manipulate the UEFI signatures database via the new \
efi-readvar and efi-updatevar commands. Aside from needing efitools \
1.4, the EFIVARFS file-system is also needed, which was only introduced \
in the Linux 3.8 kernel. \
"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=e28f66b16cb46be47b20a4cdfe6e99a1"

DEPENDS_append += "\
    help2man-native openssl-native sbsigntool-native \
    libfile-slurp-perl-native \
"

PV = "1.9.2+git${SRCPV}"

SRC_URI = "\
    git://git.kernel.org/pub/scm/linux/kernel/git/jejb/efitools.git \
    file://Fix-for-the-cross-compilation.patch \
    file://Kill-all-the-build-warning-caused-by-implicit-declar.patch \
    file://Fix-the-wrong-dependency-for-blacklist.esl.patch \
    file://LockDown-run-system-warm-reset-after-the-key-provisi.patch \
    file://Allow-to-override-tools-for-target-build.patch \
    file://Fix-help2man-failure.patch \
    file://Don-t-build-PreLoader.efi.patch \
    file://Reuse-xxdi.pl.patch \
    file://Add-static-keyword-for-IsValidVariableHeader.patch \
    file://Dynamically-load-openssl.cnf-for-openssl-1.0.x-and-1.patch \
    file://0001-console.c-Fix-compilation-against-latest-usr-include.patch \
"
SRCREV = "392836a46ce3c92b55dc88a1aebbcfdfc5dcddce"

PARALLEL_MAKE = ""

S = "${WORKDIR}/git"

inherit perlnative

EXTRA_OEMAKE = "\
    HELP2MAN='${STAGING_BINDIR_NATIVE}/help2man' \
    OPENSSL='${STAGING_BINDIR_NATIVE}/openssl' \
    SBSIGN='${STAGING_BINDIR_NATIVE}/sbsign' \
    NM='${NM}' AR='${AR}' \
    OPENSSL_LIB='${STAGING_LIBDIR_NATIVE}' \
    EXTRA_LDFLAGS='${LDFLAGS}' \
"
EXTRA_OEMAKE_append_x86 += " ARCH=ia32"
EXTRA_OEMAKE_append_x86-64 += " ARCH=x86_64"

EFI_BOOT_PATH = "/boot/efi/EFI/BOOT"

do_compile_prepend() {
    sed -i -e "1s:#!.*:#!/usr/bin/env nativeperl:" xxdi.pl 
}

do_install() {
    oe_runmake install DESTDIR='${D}${base_prefix}'
}

fakeroot python do_sign_class-target() {
    if d.getVar('GRUB_SIGN_VERIFY', True) != '1':
        return

    image_dir = d.getVar('D', True)
    efi_boot_path = d.getVar('EFI_BOOT_PATH', True)
    uks_boot_sign(os.path.join(image_dir + efi_boot_path, 'LockDown.efi'), d)
}
addtask sign after do_install before do_deploy do_package
do_sign[prefuncs] += "${@'check_boot_public_key' if d.getVar('GRUB_SIGN_VERIFY', True) == '1' else ''}"

fakeroot python do_sign() {
}

FILES_${PN} += "${EFI_BOOT_PATH}"

SSTATE_DUPWHITELIST += "${DEPLOY_DIR_IMAGE}/LockDown.efi"
