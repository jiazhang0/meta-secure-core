SUMMARY = "The utility to manipulate machines owner keys which managed in shim"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "\
    git://github.com/lcp/mokutil.git \
"

S = "${WORKDIR}/git"
SRCREV = "e19adc575c1f9d8f08b7fbc594a0887ace63f83f"
PV = "0.3.0+git${SRCPV}"

inherit autotools pkgconfig

DEPENDS += "openssl efivar"
RDEPENDS_${PN} += "openssl efivar"

EXTRA_OEMAKE += "\
    EFIVAR_LIBS='-L${STAGING_LIBDIR} -lefivar' \
    OPENSSL_LIBS='-L${STAGING_LIBDIR} -lssl -lcrypto' \
"

FILES_${PN} += "${datadir}/bash-completion/*"
