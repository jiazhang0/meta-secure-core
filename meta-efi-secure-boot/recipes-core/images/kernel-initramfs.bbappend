inherit user-key-store deploy

# Always fetch the latest initramfs image
do_install[nostamp] = "1"

fakeroot python do_sign() {
    if d.getVar('BUNDLE', True) == '0':
        for compr in d.getVar('INITRAMFS_FSTYPES').split():
            uks_sel_sign(d.expand('${D}/boot/${INITRAMFS_IMAGE}${INITRAMFS_EXT_NAME}.') + compr, d)
    else:
        uks_sel_sign(d.expand('${D}/boot/${KERNEL_IMAGETYPE}-initramfs${INITRAMFS_EXT_NAME}'), d)
}
addtask sign after do_install before do_deploy do_package
do_sign[prefuncs] += "check_deploy_keys"

do_deploy() {
    install -d "${DEPLOYDIR}"
    for SIG in ${D}/boot/*.p7b; do
        install -m 0644 ${SIG} ${DEPLOYDIR}
    done
}
addtask deploy after do_install before do_build

python do_package_prepend () {
    if d.getVar('BUNDLE') == '1':
        d.appendVar(d.expand('ALTERNATIVE_${PN}'), ' ' + d.expand('${KERNEL_IMAGETYPE}' + '-initramfs.p7b'))
        d.setVarFlag('ALTERNATIVE_LINK_NAME', d.expand('${KERNEL_IMAGETYPE}') + '-initramfs.p7b', d.expand('/boot/${KERNEL_IMAGETYPE}-initramfs.p7b'))
        d.setVarFlag('ALTERNATIVE_TARGET', d.expand('${KERNEL_IMAGETYPE}') + '-initramfs.p7b', d.expand('/boot/${KERNEL_IMAGETYPE}-initramfs${INITRAMFS_EXT_NAME}.p7b'))
        d.setVarFlag('ALTERNATIVE_PRIORITY', d.expand('${KERNEL_IMAGETYPE}') + '-initramfs.p7b', '50101')
    else:
        for compr in d.getVar('INITRAMFS_FSTYPES').split():
            d.appendVar(d.expand('ALTERNATIVE_${PN}'), ' ' + d.expand('${INITRAMFS_IMAGE}') + '.p7b')
            d.setVarFlag('ALTERNATIVE_LINK_NAME', d.expand('${INITRAMFS_IMAGE}') + '.p7b', d.expand('/boot/${INITRAMFS_IMAGE}.p7b'))
            d.setVarFlag('ALTERNATIVE_TARGET', d.expand('${INITRAMFS_IMAGE}') + '.p7b', d.expand('/boot/${INITRAMFS_IMAGE}${INITRAMFS_EXT_NAME}.' + compr + '.p7b'))
            d.setVarFlag('ALTERNATIVE_PRIORITY', d.expand('${INITRAMFS_IMAGE}') + '.p7b', '50101')
}
