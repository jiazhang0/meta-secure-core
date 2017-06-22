SUMMARY = " \
  "
DESCRIPTION = " \
  "
SECTION = "tpm"
PR = "r0"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "libtspi"

S = "${WORKDIR}/${BPN}_${PV}"

SRC_URI += " \
    http://twobit.us/${BPN}/${BPN}_${PV}.tar.bz2 \
"

SRC_URI[md5sum] = "98d2a3b816e54bdb17fe97a4294928bc"
SRC_URI[sha256sum] = "0ee784b252537bde4e195bfdedb20efd01ccf106a2b86beae6c8c02b3f7b1470"

inherit autotools
B = "${WORKDIR}/${BPN}_${PV}"
