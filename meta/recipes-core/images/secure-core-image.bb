require secure-core-image.inc

IMAGE_INSTALL += "\
    util-linux \
"

inherit extrausers
EXTRA_USERS_PARAMS_prepend += " usermod -P toor root;"
