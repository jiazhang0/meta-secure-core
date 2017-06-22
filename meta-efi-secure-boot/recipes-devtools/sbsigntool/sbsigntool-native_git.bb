SUMMARY = "Signing utility for UEFI secure boot"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.GPLv3;md5=9eef91148a9b14ec7f9df333daebc746"

SRC_URI = "git://kernel.ubuntu.com/jk/sbsigntool \
    file://ccan.git.tar.bz2 \
    file://disable-man-page-creation.patch \
    file://Fix-for-multi-sign.patch \
    file://sbsign-add-x-option-to-avoid-overwrite-existing-sign.patch \
    file://fix-mixed-implicit-and-normal-rules.patch;apply=0 \
    file://image-fix-the-segment-fault-caused-by-the-uninitiali.patch \
"

SRCREV="951ee95a301674c046f55330cd7460e1314deff2"
PV = "0.6+git${SRCPV}"

inherit autotools-brokensep pkgconfig native

DEPENDS_append = " binutils-native openssl-native gnu-efi-native util-linux-native"

S = "${WORKDIR}/git"

do_configure() {
    cd ${S}
    rm -rf lib/ccan.git
    git clone ${WORKDIR}/ccan.git lib/ccan.git
    cd lib/ccan.git && git apply ${WORKDIR}/fix-mixed-implicit-and-normal-rules.patch && cd -

    OLD_CC="${CC}"

    if [ ! -e lib/ccan ]; then
        export CC="${BUILD_CC}"
        export TMPDIR=${B}
        lib/ccan.git/tools/create-ccan-tree \
            --build-type=automake lib/ccan \
                talloc read_write_all build_assert array_size || exit 2
    fi

    export CC="${OLD_CC}"
    ./autogen.sh --noconfigure
    oe_runconf
}

EXTRA_OEMAKE += " \
    INCLUDES='-I../lib/ccan.git/' \
    EFI_CPPFLAGS='-DEFI_FUNCTION_WRAPPER \
                  -I${STAGING_INCDIR}/efi \
                  -I${STAGING_INCDIR}/efi/${BUILD_ARCH}' \
"
