#PACKAGECONFIG_append += "\
#    ${@bb.utils.contains('DISTRO_FEATURES', 'encrypted-storage', \
#                         'cryptsetup', '', d)} \
#"
