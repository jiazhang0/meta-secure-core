FILESEXTRAPATHS_prepend := "${THISDIR}/rpm:"

PACKAGECONFIG_append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ima', 'imaevm', '', d)} \
    "

# IMA signing support is provided by RPM plugin.
EXTRA_OECONF_remove += "\
    --disable-plugins \
"
EXTRA_OECONF_append_class-native = " --disable-inhibit-plugin"
