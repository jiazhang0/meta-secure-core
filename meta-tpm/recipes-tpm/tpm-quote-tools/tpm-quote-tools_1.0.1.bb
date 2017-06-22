SUMMARY = " \
  The TPM Quote Tools is a collection of programs that provide support \
  for TPM based attestation using the TPM quote mechanism. \
  "
DESCRIPTION = " \
  The TPM Quote Tools is a collection of programs that provide support \
  for TPM based attestation using the TPM quote mechanism.  The manual \
  page for tpm_quote_tools provides a usage overview. \
  \
  TPM Quote Tools has been tested with TrouSerS on Linux and NTRU on \
  Windows XP.  It was ported to Windows using MinGW and MSYS. \
  "
SECTION = "tpm"
PR = "r0"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ec30b01163d242ecf07d9cd84e3611f"
DEPENDS = "libtspi tpm-tools"

SRC_URI += " \
    http://downloads.sourceforge.net/project/tpmquotetools/1.0.1/tpm-quote-tools-1.0.1.tar.gz \
"

SRC_URI[md5sum] = "bea00c7d5c9bd78bfa42e4e69428de80"
SRC_URI[sha256sum] = "40a6987c009cc24677a7e13a6c4121c0a165e37a588c019ae417d66a3bdfa0b5"

inherit autotools
