PACKAGECONFIG_append += "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', \
                         'ima', '', d)} \
"
