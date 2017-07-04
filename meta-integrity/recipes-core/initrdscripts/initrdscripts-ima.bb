DESCRIPTION = "The initrd script for Linux Integrity Measurement Architecture (IMA)"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420 \
"

SRC_URI = "\
    file://init.ima \
"

S = "${WORKDIR}"

ALLOW_EMPTY_${PN} = "1"

do_install() {
    install -m 0500 "${WORKDIR}/init.ima" "${D}"
}

FILES_${PN} += "\
    /init.ima \
"

# Install the minimal stuffs only, and don't care how the external
# environment is configured.

# @bash: sh
# @coreutils: echo, printf
# @grep: grep
# @gawk: awk
# @util-linux: mount, umount
# @ima-evm-utils: evmctl
RDEPENDS_${PN} += "\
    coreutils \
    grep \
    gawk \
    util-linux-mount \
    util-linux-umount \
    ima-evm-utils \
    ima-policy \
"

RRECOMMENDS_${PN} += "\
    key-store-ima-cert \
"
