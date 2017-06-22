SUMMARY = "TrouSerS - An open-source TCG Software Stack implementation."
DESCRIPTION = " \
Trousers is an open-source TCG Software Stack (TSS), released under the \
Common Public License. Trousers aims to be compliant with the current (1.1b) \
and upcoming (1.2) TSS specifications available from the Trusted Computing \
Group website: http://www.trustedcomputinggroup.org. \
"
HOMEPAGE = "https://sourceforge.net/projects/trousers"
SECTION = "security/tpm"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8031b2ae48ededc9b982c08620573426"

SRC_URI = " \
           http://sourceforge.net/projects/trousers/files/trousers/0.3.14/trousers-0.3.14.tar.gz;subdir=${PN}-${PV} \
           file://fix-deadlock-and-potential-hung.patch \
           file://trousers.init.sh \
           file://fix-event-log-parsing-problem.patch \
           file://fix-incorrect-report-of-insufficient-buffer.patch \
           file://trousers-conditional-compile-DES-related-code.patch \
           file://Fix-segment-fault-if-client-hostname-cannot-be-retri.patch \
           file://trousers-udev.rules \
           file://tcsd.service \
           file://tcsd.conf \
          "

SRC_URI[md5sum] = "4a476b4f036dd20a764fb54fc24edbec"
SRC_URI[sha256sum] = "ce50713a261d14b735ec9ccd97609f0ad5ce69540af560e8c3ce9eb5f2d28f47"

S = "${WORKDIR}/${PN}-${PV}"

DEPENDS = "openssl"

inherit autotools pkgconfig useradd update-rc.d
inherit ${@bb.utils.contains('VIRTUAL-RUNTIME_init_manager', 'systemd', 'systemd', '', d)}

PACKAGECONFIG ?= "gmp "
PACKAGECONFIG[gmp] = "--with-gmp, --with-gmp=no, gmp"
PACKAGECONFIG[gtk] = "--with-gui=gtk, --with-gui=none, gtk+"

PROVIDES = "${PACKAGES}"
PACKAGES =+ " \
             libtspi \
             libtspi-dbg \
             libtspi-dev \
             libtspi-doc \
             libtspi-staticdev \
            "

FILES_libtspi = " \
                 ${libdir}/libtspi.so.* \
                "
FILES_libtspi-dbg = " \
                     ${prefix}/src/debug/${PN}/${PV}-${PR}/${PN}-${PV}/src/tspi \
                     ${prefix}/src/debug/${PN}/${PV}-${PR}/${PN}-${PV}/src/trspi \
                     ${prefix}/src/debug/${PN}/${PV}-${PR}/${PN}-${PV}/src/include/*.h \
                     ${prefix}/src/debug/${PN}/${PV}-${PR}/${PN}-${PV}/src/include/tss \
                    "
FILES_libtspi-dev = " \
                     ${includedir} \
                     ${libdir}/*.so \
                     ${libdir}/*.so.1 \
                    "
FILES_libtspi-doc = " \
                     ${mandir}/man3 \
                    "
FILES_libtspi-staticdev = " \
                           ${libdir}/*.la \
                           ${libdir}/*.a \
                          "
FILES_${PN}-dbg = " \
                   ${prefix}/src/debug/${PN}/${PV}-${PR}/${PN}-${PV}/src/tcs \
                   ${prefix}/src/debug/${PN}/${PV}-${PR}/${PN}-${PV}/src/tcsd \
                   ${prefix}/src/debug/${PN}/${PV}-${PR}/${PN}-${PV}/src/tddl \
                   ${prefix}/src/debug/${PN}/${PV}-${PR}/${PN}-${PV}/src/trousers \
                   ${prefix}/src/debug/${PN}/${PV}-${PR}/${PN}-${PV}/src/include/trousers \
                  "
FILES_${PN}-dev += "${libdir}/trousers"
FILES_${PN} += "${systemd_unitdir}/system/tcsd.service"
CONFFILES_${PN} += "${sysconfig}/tcsd.conf"

INITSCRIPT_NAME = "trousers"
INITSCRIPT_PARAMS = "start 99 2 3 4 5 . stop 19 0 1 6 ."

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "tss"
USERADD_PARAM_${PN} = "-M -d /var/lib/tpm -s /bin/false -g tss tss"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "tcsd.service"
SYSTEMD_AUTO_ENABLE = "enable"

TPM_CAPS_x86 = 'device/caps'
FAMILY_MAJOR_x86 = 'TCG version: 1.2'
TPM_CAPS_x86-64 = 'device/caps'
FAMILY_MAJOR_x86-64 = 'TCG version: 1.2'

do_install_append() {
    install -d "${D}${sysconfdir}/init.d"
    install -m 0600 "${WORKDIR}/tcsd.conf" "${D}${sysconfdir}"
    chown tss:tss "${D}${sysconfdir}/tcsd.conf"
    install -m 0755 "${WORKDIR}/trousers.init.sh" "${D}${sysconfdir}/init.d/trousers"

    install -d "${D}${sysconfdir}/udev/rules.d"
    install -m 0644 "${WORKDIR}/trousers-udev.rules" \
        "${D}${sysconfdir}/udev/rules.d/45-trousers.rules"

    install -d "${D}${systemd_unitdir}/system"
    install -m 0644 "${WORKDIR}/tcsd.service" "${D}${systemd_unitdir}/system"
    sed -i 's:@TPM_CAPS@:${TPM_CAPS}:' "${D}${systemd_unitdir}/system/tcsd.service"
    sed -i 's/@FAMILY_MAJOR@/${FAMILY_MAJOR}/' "${D}${systemd_unitdir}/system/tcsd.service"
}

BBCLASSEXTEND = "native"
