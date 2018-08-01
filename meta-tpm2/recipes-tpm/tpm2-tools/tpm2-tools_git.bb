include ${BPN}.inc

DEFAULT_PREFERENCE = "-1"

PVBASE := "${PV}"
PV = "${PVBASE}.${SRCPV}"

SRC_URI = "git://github.com/tpm2-software/${BPN}.git;protocol=git;branch=master;name=${BPN};destsuffix=${BPN}"

def version_git(d):
    version = d.getVar("PREFERRED_VERSION_%s" % d.getVar('PN'))
    if version is not None and "git" in version:
        return d.getVar("AUTOREV")
    else:
        return "e105149f07c9b944f69599ab67cd8b018ad880d2"
SRCREV ?= '${@version_git(d)}'

S = "${WORKDIR}/${BPN}"

do_configure_prepend () {
	# execute the bootstrap script
	currentdir=$(pwd)
	cd ${S}
	AUTORECONF=true ./bootstrap
	cd ${currentdir}
}
