DESCRIPTION = "EFI Secure Boot packages for secure-environment."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
    file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420 \
"

S = "${WORKDIR}"

ALLOW_EMPTY_${PN} = "1"

pkgs = "\
    grub-efi \
    efitools \
    efibootmgr \
    mokutil \
    seloader \
    shim \
"

RDEPENDS_${PN}_x86 = "${pkgs}"
RDEPENDS_${PN}_x86-64 = "${pkgs}"

kmods = "\
    kernel-module-efivarfs \
    kernel-module-efivars \
"

RRECOMMENDS_${PN}_x86 += "${kmods}"
RRECOMMENDS_${PN}_x86-64 += "${kmods}"

IMAGE_INSTALL_remove += "grub"
