DEPENDS += "openssl-native"
inherit user-key-store deploy

# Always fetch the latest initramfs image
do_install[nostamp] = "1"

fakeroot python do_sign() {
    if d.getVar('BUNDLE', True) == '0':
        for compr in d.getVar('INITRAMFS_FSTYPES').split():
            uks_bl_sign(d.expand('${D}/boot/${INITRAMFS_IMAGE}${INITRAMFS_EXT_NAME}.') + compr, d)
    else:
        uks_bl_sign(d.expand('${D}/boot/${KERNEL_IMAGETYPE}-initramfs${INITRAMFS_EXT_NAME}'), d)
}
addtask sign after do_install before do_deploy do_package
do_sign[prefuncs] += "check_deploy_keys"
do_sign[prefuncs] += "${@'check_boot_public_key' if d.getVar('GRUB_SIGN_VERIFY', True) == '1' else ''}"

do_deploy() {
    install -d "${DEPLOYDIR}"
    for SIG in ${D}/boot/*${SB_FILE_EXT}; do
        install -m 0644 ${SIG} ${DEPLOYDIR}
    done
}
addtask deploy after do_install before do_package

python do_package_prepend () {
    ext = d.expand('${SB_FILE_EXT}')
    if d.getVar('BUNDLE') == '1':
        d.appendVar(d.expand('ALTERNATIVE_${PN}'), ' ' + d.expand('${KERNEL_IMAGETYPE}' + '-initramfs' + ext))
        d.setVarFlag('ALTERNATIVE_LINK_NAME', d.expand('${KERNEL_IMAGETYPE}') + '-initramfs' + ext, d.expand('/boot/${KERNEL_IMAGETYPE}-initramfs' + ext))
        d.setVarFlag('ALTERNATIVE_TARGET', d.expand('${KERNEL_IMAGETYPE}') + '-initramfs' + ext, d.expand('/boot/${KERNEL_IMAGETYPE}-initramfs${INITRAMFS_EXT_NAME}' + ext))
        d.setVarFlag('ALTERNATIVE_PRIORITY', d.expand('${KERNEL_IMAGETYPE}') + '-initramfs' + ext, '50101')
    else:
        for compr in d.getVar('INITRAMFS_FSTYPES').split():
            d.appendVar(d.expand('ALTERNATIVE_${PN}'), ' ' + d.expand('${INITRAMFS_IMAGE}') + ext)
            d.setVarFlag('ALTERNATIVE_LINK_NAME', d.expand('${INITRAMFS_IMAGE}') + ext, d.expand('/boot/${INITRAMFS_IMAGE}') + ext)
            d.setVarFlag('ALTERNATIVE_TARGET', d.expand('${INITRAMFS_IMAGE}') + ext, d.expand('/boot/${INITRAMFS_IMAGE}${INITRAMFS_EXT_NAME}.' + compr + ext))
            d.setVarFlag('ALTERNATIVE_PRIORITY', d.expand('${INITRAMFS_IMAGE}') + ext, '50101')
}
