SUMMARY = "Testcases to exercise the TSS stack/TSS API"
HOMEPAGE = "${SOURCEFORGE_MIRROR}/projects/trousers/files"
SECTION = "console/utils"
DESCRIPTION = "\
    These are the testcases that exercise the TSS stack. They can be run \
    either through the the LTP framework or standalone.  The testcases \
    have been tested against the 20040304 version of LTP. \
    \
    Please do not execute these testcases on a machine where you are actively \
    using the TPM. \
"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "\
    https://sourceforge.net/projects/trousers/files/TSS%20API%20test%20suite/0.3/testsuite-${PV}.tar.gz; \
    file://fix-missing-LDFLAGS-in-compile-command-line.patch; \
    file://fix-failure-of-.so-LD-with-cortexa8t-neon-wrswrap-linux.patch; \
    file://fix-hardcode-path-in-tsstests.sh.patch \
    file://testsuite-transport-init.patch \
    file://Tspi_TPM_LoadMaintenancePubKey01.patch \
    file://transport-Tspi_TPM_Delegate.patch \
    file://common_c_no_des.patch \
    file://Tspi_TPM_CreateIdentity_no_des.patch \
    file://Tspi_TPM_CreateIdentityWithCallbacks_no_des.patch \
"
SRC_URI[md5sum] = "1ebd0e7783178abdfc8c40bc8cb8875f"
SRC_URI[sha256sum] = "5382539fa69cf480d44f924e54a0f2718134b26baa29137ba351a0eef4873c98"

DEPENDS = "trousers"
RDEPENDS_${PN} = "tpm-tools openssl bash"

CFLAGS += "-DOPENSSL_NO_DES"
EXTRA_OEMAKE = " -C tcg 'CC=${CC}' "
LDFLAGS += "-L${STAGING_LIBDIR} -lcrypto -lpthread"

S = "${WORKDIR}/testsuite-${PV}"

do_configure_prepend () {
    cp ${S}/tcg/Makefile ${S}
    cp ${S}/tcg/init/makefile ${S}/tcg/init/Makefile
    # remove test case about DES
    rm -rf ${S}/tcg/context/Tspi_Context_GetCapability13.c
}

testsuite_SUBDIRS = "cmk context data delegation hash highlevel init key nv pcrcomposite policy tpm transport tspi"
do_install () {
    install -d ${D}/opt/tss-testsuite/tcg
    for i in ${testsuite_SUBDIRS}; do \
        echo "Installing ${i}"; \
        cp -rf tcg/${i} ${D}/opt/tss-testsuite/tcg/; \
    done;
    install -m 0755 tsstests.sh ${D}/opt/tss-testsuite
}
 
FILES_${PN} += "/opt/*"
FILES_${PN}-dbg += "/opt/tss-testsuite/tcg/*/.debug /opt/tss-testsuite/tcg/*/*/.debug"
