SUMMARY = "The utility to manipulate machines owner keys which managed in shim"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS += "openssl efivar virtual/crypt"

PV = "0.3.0+git${SRCPV}"

SRC_URI = "\
    git://github.com/lcp/mokutil.git;branch=master;protocol=https \
    file://0001-mokutil.c-fix-typo-enrollement-enrollment.patch \
"
SRCREV = "e19adc575c1f9d8f08b7fbc594a0887ace63f83f"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OEMAKE += "\
    EFIVAR_LIBS='-L${STAGING_LIBDIR} -lefivar' \
    OPENSSL_LIBS='-L${STAGING_LIBDIR} -lssl -lcrypto' \
"

COMPATIBLE_HOST = '(i.86|x86_64|arm|aarch64).*-linux'

FILES:${PN} += "${datadir}/bash-completion/*"

RDEPENDS:${PN} += "openssl efivar"
