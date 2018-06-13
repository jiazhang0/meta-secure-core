include ${BPN}.inc

DEFAULT_PREFERENCE = "-1"

DEPENDS += "tpm2-tss libtss2 libtss2-mu libtss2-tcti-device libtss2-tcti-mssim"

PVBASE := "${PV}"
PV = "${PVBASE}.${SRCPV}"

SRC_URI += " \
    git://github.com/01org/${BPN}.git;protocol=git;branch=master;name=${BPN};destsuffix=${BPN} \
    file://tpm2-abrmd_git.default \
    "

SRCREV = "${AUTOREV}"

S = "${WORKDIR}/${BPN}"

do_configure_prepend () {
	# execute the bootstrap script
	currentdir=$(pwd)
	cd ${S}
	AUTORECONF=true ./bootstrap
	cd ${currentdir}
}

do_install_append() {
	install -d "${D}${sysconfdir}/init.d"
	install -m 0755 "${WORKDIR}/tpm2-abrmd-init.sh" "${D}${sysconfdir}/init.d/tpm2-abrmd"

	install -d "${D}${sysconfdir}/default"
	install -m 0644 "${WORKDIR}/tpm2-abrmd_git.default" "${D}${sysconfdir}/default/tpm2-abrmd"
}

RDEPENDS_${PN} += "tpm2-tss"
