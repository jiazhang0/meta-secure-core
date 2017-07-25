DESCRIPTION = "The default external IMA policy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
    file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420 \
"

SRC_URI = "\
    file://ima_policy.default \
"

S = "${WORKDIR}"

do_install() {
    install -d "${D}${sysconfdir}/ima"
    install -m 0400 "${WORKDIR}/ima_policy.default" \
        "${D}${sysconfdir}/ima"
}

FILES_${PN} = "${sysconfdir}"
