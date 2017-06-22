SUMMARY = ""
DESCRIPTION = ""
SECTION = "tpm"

# This is a lie. The source for this project is covered by several licenses.
# We're currently working on a way to make this clear for those consuming the
# project. Till then I'm using 'BSD' as a place holder since the Intel license
# is "BSD-like".
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/BSD;md5=3775480a712fc46a69647678acb234cb"

DEPENDS += "autoconf-archive dbus glib-2.0 pkgconfig tpm2.0-tss glib-2.0-native"
RDEPENDS_${PN} += "libgcc dbus-glib"

SRC_URI = " \
           git://github.com/01org/tpm2-abrmd.git;branch=master;name=tpm2-abrmd;destsuffix=tpm2-abrmd \
           file://tpm2-abrmd-init.sh \
           file://tpm2-abrmd.default \
          "

SRCREV = "4f0bd204d07176084b245d005df665fbfdf68db5"
PV = "1.0.0+git${SRCPV}"
S = "${WORKDIR}/${BPN}"

inherit autotools pkgconfig systemd update-rc.d useradd

SYSTEMD_PACKAGES += "${PN}"
SYSTEMD_SERVICE_${PN} = "tpm2-abrmd.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

INITSCRIPT_NAME = "tpm2-abrmd"
INITSCRIPT_PARAMS = "start 99 2 3 4 5 . stop 19 0 1 6 ."

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "tss"
USERADD_PARAM_${PN} = "-M -d /var/lib/tpm -s /bin/false -g tss tss"

# break out tcti into a package: libtcti-tabrmd
# package up the service file

EXTRA_OECONF += " \
                 --with-systemdsystemunitdir=${systemd_system_unitdir} \
                 --with-udevrulesdir=${sysconfdir}/udev/rules.d \
                "

do_configure_prepend() {
	# execute the bootstrap script
	currentdir=$(pwd)
	cd "${S}"
	ACLOCAL="aclocal --system-acdir=${STAGING_DATADIR}/aclocal" ./bootstrap --force
	cd "${currentdir}"
}

do_install_append() {
        install -d "${D}${sysconfdir}/init.d"
        install -m 0755 "${WORKDIR}/tpm2-abrmd-init.sh" "${D}${sysconfdir}/init.d/tpm2-abrmd"
        install -d "${D}${sysconfdir}/default"
        install -m 0644 "${WORKDIR}/tpm2-abrmd.default" "${D}${sysconfdir}/default/tpm2-abrmd"
}

BBCLASSEXTEND = "native"
