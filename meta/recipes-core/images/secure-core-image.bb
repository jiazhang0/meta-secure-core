require secure-core-image.inc

IMAGE_INSTALL += "\
    util-linux \
"

inherit extrausers
EXTRA_USERS_PARAMS += "usermod -P toor root;"
