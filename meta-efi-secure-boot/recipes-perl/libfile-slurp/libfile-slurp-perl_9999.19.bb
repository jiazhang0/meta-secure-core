DESCRIPTION = "Slurp entire files into variables."
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://README;beginline=37;endline=41;md5=255fbd5f98a90d51d9908d31271ae4d4"
SRC_URI = "http://search.cpan.org/CPAN/authors/id/U/UR/URI/File-Slurp-9999.19.tar.gz"

S = "${WORKDIR}/File-Slurp-${PV}"

inherit cpan
BBCLASSEXTEND="native"
PACKAGE_ARCH = "all"

SRC_URI[md5sum] = "7d584cd15c4f8b9547765eff8c4ef078"
SRC_URI[sha256sum] = "ce29ebe995097ebd6e9bc03284714cdfa0c46dc94f6b14a56980747ea3253643"
