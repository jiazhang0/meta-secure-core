DEPENDS += "openssl-native"
inherit user-key-store deploy

# Always fetch the latest initramfs image
do_install[nostamp] = "1"

fakeroot python do_sign() {
    if d.getVar('BUNDLE', True) == '0':
        for compr in d.getVar('INITRAMFS_FSTYPES').split():
            uks_sel_sign(d.expand('${D}/boot/${INITRAMFS_IMAGE}${INITRAMFS_EXT_NAME}.') + compr, d)
            uks_boot_sign(d.expand('${D}/boot/${INITRAMFS_IMAGE}${INITRAMFS_EXT_NAME}.') + compr, d)
    else:
        uks_sel_sign(d.expand('${D}/boot/${KERNEL_IMAGETYPE}-initramfs${INITRAMFS_EXT_NAME}'), d)
        uks_boot_sign(d.expand('${D}/boot/${KERNEL_IMAGETYPE}-initramfs${INITRAMFS_EXT_NAME}'), d)
}
addtask sign after do_install before do_deploy do_package
do_sign[prefuncs] += "check_deploy_keys"
do_sign[prefuncs] += "${@'check_boot_public_key' if d.getVar('GRUB_SIGN_VERIFY', True) == '1' else ''}"

do_deploy() {
    install -d "${DEPLOYDIR}"
    if [ "${UEFI_SELOADER}" = "1" ] ; then
        for SIG in ${D}/boot/*.p7b; do
            install -m 0644 ${SIG} ${DEPLOYDIR}
        done
    fi
    if [ "${GRUB_SIGN_VERIFY}" = "1" ] ; then
        for SIG in ${D}/boot/*.sig; do
            install -m 0644 ${SIG} ${DEPLOYDIR}
        done
    fi
}
addtask deploy after do_install before do_build

python do_package_prepend () {
    if d.getVar('BUNDLE') == '1':
        if d.getVar('UEFI_SELOADER', True) == '1':
            d.appendVar(d.expand('ALTERNATIVE_${PN}'), ' ' + d.expand('${KERNEL_IMAGETYPE}' + '-initramfs.p7b'))
            d.setVarFlag('ALTERNATIVE_LINK_NAME', d.expand('${KERNEL_IMAGETYPE}') + '-initramfs.p7b', d.expand('/boot/${KERNEL_IMAGETYPE}-initramfs.p7b'))
            d.setVarFlag('ALTERNATIVE_TARGET', d.expand('${KERNEL_IMAGETYPE}') + '-initramfs.p7b', d.expand('/boot/${KERNEL_IMAGETYPE}-initramfs${INITRAMFS_EXT_NAME}.p7b'))
            d.setVarFlag('ALTERNATIVE_PRIORITY', d.expand('${KERNEL_IMAGETYPE}') + '-initramfs.p7b', '50101')
        if d.getVar('GRUB_SIGN_VERIFY', True) == '1':
            d.appendVar(d.expand('ALTERNATIVE_${PN}'), ' ' + d.expand('${KERNEL_IMAGETYPE}' + '-initramfs.sig'))
            d.setVarFlag('ALTERNATIVE_LINK_NAME', d.expand('${KERNEL_IMAGETYPE}') + '-initramfs.sig', d.expand('/boot/${KERNEL_IMAGETYPE}-initramfs.sig'))
            d.setVarFlag('ALTERNATIVE_TARGET', d.expand('${KERNEL_IMAGETYPE}') + '-initramfs.sig', d.expand('/boot/${KERNEL_IMAGETYPE}-initramfs${INITRAMFS_EXT_NAME}.sig'))
            d.setVarFlag('ALTERNATIVE_PRIORITY', d.expand('${KERNEL_IMAGETYPE}') + '-initramfs.sig', '50101')
    else:
        for compr in d.getVar('INITRAMFS_FSTYPES').split():
            if d.getVar('UEFI_SELOADER', True) == '1':
                d.appendVar(d.expand('ALTERNATIVE_${PN}'), ' ' + d.expand('${INITRAMFS_IMAGE}') + '.p7b')
                d.setVarFlag('ALTERNATIVE_LINK_NAME', d.expand('${INITRAMFS_IMAGE}') + '.p7b', d.expand('/boot/${INITRAMFS_IMAGE}.p7b'))
                d.setVarFlag('ALTERNATIVE_TARGET', d.expand('${INITRAMFS_IMAGE}') + '.p7b', d.expand('/boot/${INITRAMFS_IMAGE}${INITRAMFS_EXT_NAME}.' + compr + '.p7b'))
                d.setVarFlag('ALTERNATIVE_PRIORITY', d.expand('${INITRAMFS_IMAGE}') + '.p7b', '50101')
            if d.getVar('GRUB_SIGN_VERIFY', True) == '1':
                d.appendVar(d.expand('ALTERNATIVE_${PN}'), ' ' + d.expand('${INITRAMFS_IMAGE}') + '.sig')
                d.setVarFlag('ALTERNATIVE_LINK_NAME', d.expand('${INITRAMFS_IMAGE}') + '.sig', d.expand('/boot/${INITRAMFS_IMAGE}.sig'))
                d.setVarFlag('ALTERNATIVE_TARGET', d.expand('${INITRAMFS_IMAGE}') + '.sig', d.expand('/boot/${INITRAMFS_IMAGE}${INITRAMFS_EXT_NAME}.' + compr + '.sig'))
                d.setVarFlag('ALTERNATIVE_PRIORITY', d.expand('${INITRAMFS_IMAGE}') + '.sig', '50101')
}
