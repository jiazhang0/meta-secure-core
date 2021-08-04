FILESEXTRAPATHS:prepend := "${THISDIR}/rpm:"

PACKAGECONFIG:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ima', 'imaevm', '', d)} \
    "

# IMA signing support is provided by RPM plugin.
EXTRA_OECONF:remove += "\
    --disable-plugins \
"
EXTRA_OECONF:append:class-native = " --disable-inhibit-plugin"

SRC_URI:append = " \
                  file://macros.ima \
                 "

do_install:append () {
    install -d ${D}${sysconfdir}/rpm
    install -m 0644 ${WORKDIR}/macros.ima ${D}${sysconfdir}/rpm/
}
