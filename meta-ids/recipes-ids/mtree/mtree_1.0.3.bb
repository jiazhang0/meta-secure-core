SUMMARY = "BSD directory hierarchy mapping tool"
DESCRIPTION = "mtree compares a file hierarchy against a specification, creates a specification for a file hierarchy, or modifies a specification."

SECTION = "utils"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=bb19ea4eac951288efda4010c5c669a8"

SRC_URI = "git://github.com/archiecobbs/mtree-port.git \
           file://mtree-getlogin.patch \
           file://configure.ac-automake-error.patch \
           "
SRCREV = "172e1827c381ff3851cc99edb5fd89443cf260e9"

S = "${WORKDIR}/git"

DEPENDS = "openssl"

inherit autotools
