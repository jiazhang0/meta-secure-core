include ${BPN}.inc

SRC_URI += " \
    https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz \
    file://0001-build-update-for-ax_code_coverage.m4-version-2019.01.patch \
    file://tpm2-abrmd.default \
"
SRC_URI[md5sum] = "a71faf008de2e444265b0d1d889cab2e"
SRC_URI[sha256sum] = "ff0ed283b0300cd784d6bf2b042e167020f8443602974e53b924e9fd98a4b515"
