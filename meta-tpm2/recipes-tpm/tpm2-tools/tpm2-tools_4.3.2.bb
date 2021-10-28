SUMMARY = "Tools for TPM2."
DESCRIPTION = "tpm2-tools"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://doc/LICENSE;md5=a846608d090aa64494c45fc147cc12e3"
SECTION = "tpm"

DEPENDS = "tpm2-abrmd tpm2-tss openssl curl autoconf-archive"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz \
           file://0001-tests-switch-to-python3.patch \
           file://0001-build-only-use-Werror-for-non-release-builds.patch \
          "

SRC_URI[md5sum] = "1d06d8940db8d055daf840716872ee89"
SRC_URI[sha256sum] = "e2802d4093a24b2c65b1f913d0f4c68eadde9b8fd8a9b7a3b17a6e50765e8350"

inherit autotools pkgconfig bash-completion
