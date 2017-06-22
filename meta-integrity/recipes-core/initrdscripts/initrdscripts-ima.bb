DESCRIPTION = "The initrd script for Linux Integrity Measurement Architecture (IMA)"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

S = "${WORKDIR}"

ALLOW_EMPTY_${PN} = "1"

SRC_URI = "\
    file://init.ima \
"

do_install() {
    if [ x"${@bb.utils.contains('DISTRO_FEATURES', 'ima', '1', '0', d)}" = x"1" ]; then
        install -m 0500 ${WORKDIR}/init.ima ${D}
    fi
}

FILES_${PN} += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ima', '/init.ima', '', d)} \
"

# Install the minimal stuffs only, and don't care how the external
# environment is configured.
# @bash: sh
# @coreutils: echo, mkdir, mknod, dirname, basename, cp, rm, sleep
#             seq, printf, cut
# @grep: grep
# @gawk: awk
# @kmod: modprobe, depmod
# @net-tools: ifconfig
# @trousers: tcsd
# @procps: pkill
# @util-linux: blkid, mount, umount
RDEPENDS_${PN} += "\
    bash \
    coreutils \
    grep \
    gawk \
    kmod \
    net-tools \
    procps \
    util-linux-blkid \
    util-linux-mount \
    util-linux-umount \
"
