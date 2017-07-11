SUMMARY = "The root image of SecureCore."
LICENSE = "MIT"

SECURE_CORE_IMAGE_EXTRA_INSTALL_append += "\
    ${@bb.utils.contains("DISTRO_FEATURES", "efi-secure-boot", \
                         "packagegroup-efi-secure-boot", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "tpm", \
                         "packagegroup-tpm", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "tpm2", \
                         "packagegroup-tpm2", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "ima", \
                         "packagegroup-ima", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "encrypted-storage", \
                         "packagegroup-encrypted-storage", "", d)} \
"

IMAGE_INSTALL = "\
    packagegroup-core-boot \
    packagegroup-core-lsb \
    kernel-initramfs \
    ${SECURE_CORE_IMAGE_EXTRA_INSTALL} \
"

IMAGE_LINGUAS = " "

INITRAMFS_IMAGE = "secure-core-image-initramfs"

inherit core-image

IMAGE_ROOTFS_SIZE ?= "8192"
IMAGE_ROOTFS_EXTRA_SPACE_append = "\
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", " + 4096", "" ,d)} \
"
