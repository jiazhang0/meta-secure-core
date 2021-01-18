DEPENDS += "gnu-efi"
PACKAGECONFIG_append = " efi"
EXTRA_OEMESON += "-Dgnu-efi=true \
                  -Defi-libdir=${STAGING_LIBDIR} \
                  -Defi-includedir=${STAGING_INCDIR}"
