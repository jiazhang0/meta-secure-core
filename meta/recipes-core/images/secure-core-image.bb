require secure-core-image.inc

IMAGE_INSTALL += "\
    packagegroup-core-lsb \
"

inherit extrausers
EXTRA_USERS_PARAMS_prepend += " usermod -P toor root;"
