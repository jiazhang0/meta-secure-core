include ${BPN}.inc

DEFAULT_PREFERENCE = "-1"

DEPENDS += "tpm2-tss libtss2 libtss2-mu libtss2-tcti-device libtss2-tcti-mssim"

PVBASE := "${PV}"
PV = "${PVBASE}.${SRCPV}"

SRC_URI += " \
    git://github.com/01org/${BPN}.git;protocol=git;branch=master;name=${BPN};destsuffix=${BPN} \
    file://tpm2-abrmd_git.default \
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

do_install_append() {
    install -d "${D}${sysconfdir}/init.d"
    install -m 0755 "${WORKDIR}/tpm2-abrmd-init.sh" "${D}${sysconfdir}/init.d/tpm2-abrmd"

    install -d "${D}${sysconfdir}/default"
    install -m 0644 "${WORKDIR}/tpm2-abrmd_git.default" "${D}${sysconfdir}/default/tpm2-abrmd"
}

RDEPENDS_${PN} += "tpm2-tss"
