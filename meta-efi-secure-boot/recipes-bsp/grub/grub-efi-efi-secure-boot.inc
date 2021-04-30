DEPENDS += "openssl-native"
FILESEXTRAPATHS_prepend := "${THISDIR}/grub-efi:"

GRUB_SIGN_VERIFY_STRICT ?= "1"

EXTRA_SRC_URI = "\
    ${@'file://efi-secure-boot.inc file://password.inc' if d.getVar('UEFI_SB', True) == '1' else ''} \
"

GRUB_MOKVERIFY_PATCH = " \
   file://mok2verify-support-to-verify-non-PE-file-with-PKCS-7.patch \
   file://mok2verify-multiboot.patch \
   file://verify-all-buffiles.patch \
"

SRC_URI_append_class-target += "\
    file://0001-pe32.h-add-header-structures-for-TE-and-DOS-executab.patch \
    file://0002-shim-add-needed-data-structures.patch \
    file://0003-efi-chainloader-implement-an-UEFI-Exit-service-for-s.patch \
    file://0004-efi-chainloader-port-shim-to-grub.patch \
    file://0005-efi-chainloader-use-shim-to-load-and-verify-an-image.patch \
    file://0006-efi-chainloader-boot-the-image-using-shim.patch \
    file://0007-efi-chainloader-take-care-of-unload-undershim.patch \
    file://chainloader-handle-the-unauthenticated-image-by-shim.patch \
    file://chainloader-Don-t-check-empty-section-in-file-like-..patch \
    file://chainloader-Actually-find-the-relocations-correctly-.patch \
    file://efi-chainloader-implemented-for-32-bit.patch \
    file://Grub-get-and-set-efi-variables.patch \
    file://uefi_verify.patch \
    file://0001-grub-verify-Add-strict_security-variable.patch \
    file://0001-fs-ext2-fix-the-file-not-found-error-when-symlink-fi.patch \
    file://grub-efi.cfg \
    file://boot-menu.inc \
    ${@d.getVar('GRUB_MOKVERIFY_PATCH', True) if d.getVar('UEFI_SELOADER', True) == '1' else ''} \
    ${EXTRA_SRC_URI} \
"

# functions efi_call_foo and efi_shim_exit are not implemented for arm64 yet
COMPATIBLE_HOST_aarch64 = 'null'

GRUB_PREFIX_DIR ?= "/EFI/BOOT"
EFI_BOOT_PATH ?= "/boot/efi/EFI/BOOT"

GRUB_SECURE_BOOT_MODULES += "${@'efivar password_pbkdf2 ' if d.getVar('UEFI_SB', True) == '1' else ''}"

GRUB_SIGNING_MODULES += "${@'pgp gcry_rsa gcry_sha256 gcry_sha512 --pubkey %s ' % d.getVar('GRUB_PUB_KEY', True) \
  if d.getVar('GRUB_SIGN_VERIFY', True) == '1' else ''}"

GRUB_SELOADER_MODULES += "${@'mok2verify ' if d.getVar('UEFI_SELOADER', True) == '1' else ''}"

GRUB_BUILDIN_append_class-target += "\
  tftp reboot chain \
  ${GRUB_SECURE_BOOT_MODULES} \
  ${GRUB_SIGNING_MODULES} \
  ${GRUB_SELOADER_MODULES}"

# For efi_call_foo and efi_shim_exit
CFLAGS_append_class-target = " -fno-toplevel-reorder"

# Set a default root specifier.
inherit user-key-store

python __anonymous () {
    if d.getVar('UEFI_SB', True) != "1":
        return

    # Override the default filename if efi-secure-boot enabled.
    # grub-efi must be renamed as grub${arch}.efi for working with shim
    # or SELoader.
    import re

    if d.getVar('UEFI_SELOADER', True) == "1":
        target = d.getVar('TARGET_ARCH', True)
        if target == "x86_64":
            grubimage = "grubx64.efi"
        elif re.match('i.86', target):
            grubimage = "grubia32.efi"
        else:
            raise bb.parse.SkipPackage("grub-efi is incompatible with target %s" % target)

        d.setVar("GRUB_IMAGE", grubimage)
}

do_compile_append_class-target() {
	if [ "${GRUB_SIGN_VERIFY}" = "1" -a "${GRUB_SIGN_VERIFY_STRICT}" = "1" ] ; then
		cat<<EOF>${WORKDIR}/cfg
set strict_security=1
EOF
  else
    > ${WORKDIR}/cfg
	fi
	cat<<EOF>>${WORKDIR}/cfg
search.file (\$cmdpath)${GRUB_PREFIX_DIR}/grub.cfg root
set prefix=(\$root)${GRUB_PREFIX_DIR}
EOF
}

do_compile_append_class-native() {
    make grub-editenv
}

do_install_append_class-native() {
    install -m 0755 grub-editenv "${D}${bindir}"
}

do_install_append_class-target() {
    local menu="${WORKDIR}/boot-menu.inc"

    # Enable the default IMA rules if IMA is enabled and luks is disabled.
    # This is because unseal operation will fail when any PCR is extended
    # due to updating the aggregate integrity value by the default IMA rules.
    [ x"${IMA}" = x"1" -a x"${@bb.utils.contains('DISTRO_FEATURES', 'luks', '1', '0', d)}" != x"1" ] && {
        ! grep -q "ima_policy=tcb" "$menu" &&
            sed -i 's/^\s*linux\s\+.*bzImage.*/& ima_policy=tcb/g' "$menu"
    }

    # Replace the root parameter in boot command line with BOOT_CMD_ROOT,
    # which can be configured. It is helpful when secure boot is enabled.
    [ -n "${BOOT_CMD_ROOT}" ] && {
        sed -i "s,root=/dev/hda2,root=${BOOT_CMD_ROOT},g" "$menu"
    }

    # Install the stacked grub configs.
    install -d "${D}${EFI_BOOT_PATH}"
    install -m 0600 "${WORKDIR}/grub-efi.cfg" "${D}${EFI_BOOT_PATH}/grub.cfg"
    install -m 0600 "$menu" "${D}${EFI_BOOT_PATH}"
    [ x"${UEFI_SB}" = x"1" ] && {
        install -m 0600 "${WORKDIR}/efi-secure-boot.inc" "${D}${EFI_BOOT_PATH}"
        install -m 0600 "${WORKDIR}/password.inc" "${D}${EFI_BOOT_PATH}"
    }

    # Create the initial environment block with empty item.
    grub-editenv "${D}${EFI_BOOT_PATH}/grubenv" create

    install -d "${D}${EFI_BOOT_PATH}/${GRUB_TARGET}-efi"
    grub-mkimage -c ../cfg -p "${GRUB_PREFIX_DIR}" -d "./grub-core" \
        -O "${GRUB_TARGET}-efi" -o "${B}/${GRUB_IMAGE}" \
        ${GRUB_BUILDIN}

    install -m 0644 "${B}/${GRUB_IMAGE}" "${D}${EFI_BOOT_PATH}/${GRUB_IMAGE}"

    # Install the modules to grub-efi's search path
    make -C grub-core install DESTDIR="${D}${EFI_BOOT_PATH}" pkglibdir=""

    # Remove .module
    rm -f ${D}${EFI_BOOT_PATH}/${GRUB_TARGET}-efi/*.module
}

python do_sign_prepend_class-target() {
    bb.build.exec_func("check_deploy_keys", d)
    if d.getVar('GRUB_SIGN_VERIFY') == '1':
        bb.build.exec_func("check_boot_public_key", d)
}

fakeroot python do_sign_class-target() {
    image_dir = d.getVar('D', True)
    efi_boot_path = d.getVar('EFI_BOOT_PATH', True)
    grub_image = d.getVar('GRUB_IMAGE', True)
    dir = image_dir + efi_boot_path + '/'

    sb_sign(dir + grub_image, dir + grub_image, d)
    uks_bl_sign(dir + 'grub.cfg', d)
    uks_bl_sign(dir + 'boot-menu.inc', d)

    if d.getVar('UEFI_SB', True) == "1":
        uks_bl_sign(dir + 'efi-secure-boot.inc', d)
        uks_bl_sign(dir + 'password.inc', d)
}

python do_sign() {
}
addtask sign after do_install before do_deploy do_package

fakeroot do_chownboot() {
    if [ "${GRUB_SIGN_VERIFY}" = "1"]; then
        chown root:root -R "${D}${EFI_BOOT_PATH}/grub.cfg${SB_FILE_EXT}"
        chown root:root -R "${D}${EFI_BOOT_PATH}/boot-menu.inc${SB_FILE_EXT}"
        [ x"${UEFI_SB}" = x"1" ] && {
            chown root:root -R "${D}${EFI_BOOT_PATH}/efi-secure-boot.inc${SB_FILE_EXT}"
            chown root:root -R "${D}${EFI_BOOT_PATH}/password.inc${SB_FILE_EXT}"
        }
    fi
}
addtask chownboot after do_deploy before do_package

# Append the do_deploy() in oe-core.
do_deploy_append_class-target() {
    install -m 0644 "${D}${EFI_BOOT_PATH}/${GRUB_IMAGE}" "${DEPLOYDIR}"
    install -m 0664 "${DEPLOYDIR}/${GRUB_IMAGE}" "${DEPLOYDIR}/${GRUB_IMAGE_PREFIX}${GRUB_IMAGE}"

    # Deploy the stacked grub configs.
    install -m 0600 "${D}${EFI_BOOT_PATH}/grubenv" "${DEPLOYDIR}"
    install -m 0600 "${D}${EFI_BOOT_PATH}/grub.cfg" "${DEPLOYDIR}"
    install -m 0600 "${D}${EFI_BOOT_PATH}/boot-menu.inc" "${DEPLOYDIR}"
    if [ "${GRUB_SIGN_VERIFY}" = "1" ]; then

        install -m 0600 "${D}${EFI_BOOT_PATH}/grub.cfg${SB_FILE_EXT}" "${DEPLOYDIR}"
        install -m 0600 "${D}${EFI_BOOT_PATH}/boot-menu.inc${SB_FILE_EXT}" "${DEPLOYDIR}"
    fi
    [ x"${UEFI_SB}" = x"1" ] && {
        install -m 0600 "${D}${EFI_BOOT_PATH}/efi-secure-boot.inc" "${DEPLOYDIR}"
        install -m 0600 "${D}${EFI_BOOT_PATH}/password.inc" "${DEPLOYDIR}"
    }

    if [ "${UEFI_SB}" = "1" ] && [ "${GRUB_SIGN_VERIFY}" = "1" ]; then
        install -m 0600 "${D}${EFI_BOOT_PATH}/efi-secure-boot.inc${SB_FILE_EXT}" "${DEPLOYDIR}"
        install -m 0600 "${D}${EFI_BOOT_PATH}/password.inc${SB_FILE_EXT}" "${DEPLOYDIR}"
    fi


    install -d "${DEPLOYDIR}/efi-unsigned"
    install -m 0644 "${B}/${GRUB_IMAGE}" "${DEPLOYDIR}/efi-unsigned"
    install -m 0644 "${B}/${GRUB_IMAGE}" "${DEPLOYDIR}/efi-unsigned/${GRUB_IMAGE_PREFIX}${GRUB_IMAGE}"
    PSEUDO_DISABLED=1 cp -af "${D}${EFI_BOOT_PATH}/${GRUB_TARGET}-efi" "${DEPLOYDIR}/efi-unsigned"
}

FILES_${PN} += "${EFI_BOOT_PATH}"

CONFFILES_${PN} += "\
    ${EFI_BOOT_PATH}/grub.cfg \
    ${EFI_BOOT_PATH}/grubenv \
    ${EFI_BOOT_PATH}/boot-menu.inc \
    ${EFI_BOOT_PATH}/efi-secure-boot.inc \
"
