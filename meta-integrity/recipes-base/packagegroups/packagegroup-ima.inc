LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420 \
"

S = "${WORKDIR}"

ALLOW_EMPTY_${PN} = "1"

# Check and deploy keys to ${DEPLOY_DIR_IMAGE}
inherit user-key-store

do_install[postfuncs] += "check_deploy_keys"

RDEPENDS_${PN} = "\
    ima-evm-utils \
"
