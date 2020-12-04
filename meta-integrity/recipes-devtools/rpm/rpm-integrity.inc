FILESEXTRAPATHS_prepend := "${THISDIR}/rpm:"

PACKAGECONFIG_append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ima', 'imaevm', '', d)} \
    "

# IMA signing support is provided by RPM plugin.
EXTRA_OECONF_remove += "\
    --disable-plugins \
"
EXTRA_OECONF_append_class-native = " --disable-inhibit-plugin"

SRC_URI_append = " \
                  file://macros.ima \
                 "

do_install_append () {
    install -d ${D}${sysconfdir}/rpm
    install -m 0644 ${WORKDIR}/macros.ima ${D}${sysconfdir}/rpm/
}
