LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

ALLOW_EMPTY_${PN} = "1"

S = "${WORKDIR}"

# Install the minimal stuffs for the common uses between initramfs
# and linux rootfs.
# @util-linux: mount, umount
# @cryptsetup: cryptsetup
# @cryptfs-tpm: tpm_gen_dmcrypt_key, tpm_unwrap_dmcrypt_key 
# @kmod: modprobe
# @coreutils: cat, mkdir, mknod, cp, rm
# @trousers: tcsd
RDEPENDS_${PN} = " \
    util-linux-mount \
    util-linux-umount \
    cryptsetup \
    kmod \
    coreutils \
    cryptfs-tpm2 \
    procps \
"
