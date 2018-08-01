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
        return "35ab16e1d328f709d6146221a371b7341b84b670"
SRCREV ?= '${@version_git(d)}'

S = "${WORKDIR}/${BPN}"

do_configure_prepend () {
    # Execute the bootstrap script, to generate src_vars.mk.
    # The actual autotools bootstrapping is done by the normal
    # do_configure, which does a better job with it (for example,
    # it finds m4 macros also in the native sysroot).
    currentdir="$(pwd)"
    cd "${S}"
    AUTORECONF=true ./bootstrap
    cd "${currentdir}"
}
