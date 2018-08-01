include ${BPN}.inc

DEFAULT_PREFERENCE = "-1"

PVBASE := "${PV}"
PV = "${PVBASE}.${SRCPV}"

SRC_URI += " \
    git://github.com/01org/${BPN}.git;protocol=git;branch=master;name=${BPN};destsuffix=${BPN} \
    "

def version_git(d):
    version = d.getVar("PREFERRED_VERSION_%s" % d.getVar('PN'))
    if version is not None and "git" in version:
        return d.getVar("AUTOREV")
    else:
        return "cceb6c12ebb335aacb49207ee13d2f2fc833580a"
SRCREV ?= '${@version_git(d)}'

S = "${WORKDIR}/${BPN}"

do_configure_prepend () {
    # execute the bootstrap script
    currentdir="$(pwd)"
    cd "${S}"
    AUTORECONF=true ./bootstrap
    cd "${currentdir}"
}
