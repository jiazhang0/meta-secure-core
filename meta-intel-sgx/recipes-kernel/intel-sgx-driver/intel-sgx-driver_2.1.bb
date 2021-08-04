SUMMARY = "Intel SGX Linux DDDriver"
DESCRIPTION = "Intel(R) Software Guard Extensions (Intel(R) SGX) \
is an Intel technology for application developers seeking to \
protect select code and data from disclosure or modification."
HOMEPAGE = "https://github.com/intel/linux-sgx-driver"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://License.txt;md5=b54f8941f6087efb6be3deb0f1e617f7"

DEPENDS = "virtual/kernel"

PV = "2.1+git${SRCPV}"

SRC_URI = "\
    git://github.com/intel/linux-sgx-driver.git \
"
SRCREV = "2a509c203533f9950fa3459fe91864051bc021a2"

S = "${WORKDIR}/git"

inherit module

EXTRA_OEMAKE += "KDIR='${STAGING_KERNEL_DIR}'"

MODULE_NAME = "isgx"

do_install () {
    dir="${D}/lib/modules/${KERNEL_VERSION}/kernel/${MODULE_NAME}"

    install -d "$dir"
    install -m 0644 "${MODULE_NAME}.ko" "$dir"
}

RPROVIDES:${PN} += "kernel-module-${MODULE_NAME}"
