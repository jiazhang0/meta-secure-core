SUMMARY = "Tools for TPM2"
DESCRIPTION = "This project builds a set of command-line tools for \
interacting with a TPM (Trusted Platform Module) 2."
HOMEPAGE = "https://github.com/tpm2-software/tpm2-tools"
SECTION = "security/tpm"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=91b7c548d73ea16537799e8060cea819"

DEPENDS = "tpm2-abrmd tpm2-tss openssl curl autoconf-archive-native"

inherit autotools pkgconfig
