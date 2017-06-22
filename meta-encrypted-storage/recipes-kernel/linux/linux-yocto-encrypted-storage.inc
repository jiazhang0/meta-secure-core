FILESEXTRAPATHS_prepend := "${THISDIR}/linux-yocto:"

SRC_URI += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'encrypted-storage', \
                         'file://dmcrypt.scc file://dmcrypt.cfg', '', d)} \
"
