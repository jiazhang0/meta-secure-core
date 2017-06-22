FILESEXTRAPATHS_prepend := "${THISDIR}/linux-yocto:"

IMA_ENABLED = "${@bb.utils.contains('DISTRO_FEATURES', 'ima', '1', '0', d)}"

DEPENDS += "${@'key-store openssl-native' if d.getVar('IMA_ENABLED', True) == '1' else ''}"
# key-store-ima-cert is required in runtime but we hope it is available
# in initramfs only. So we don't add it to RDEPENDS_${PN} here.

SRC_URI += " \
    ${@'file://ima.scc file://ima.cfg' if d.getVar('IMA_ENABLED', True) == '1' else ''} \
"

do_configure_append() {
    [ -f "${STAGING_DIR_TARGET}${sysconfdir}/keys/system_trusted_key.pem" ] &&
        openssl x509 -in "${STAGING_DIR_TARGET}${sysconfdir}/keys/system_trusted_key.pem" \
            -outform DER -out "${B}/system_trusted_cert.x509" ||
        true
}
