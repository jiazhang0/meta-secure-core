#PACKAGECONFIG:append = " \
#    ${@bb.utils.contains('DISTRO_FEATURES', 'luks', \
#                         'cryptsetup', '', d)} \
#"
