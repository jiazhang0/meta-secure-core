FILESEXTRAPATHS_prepend := "${THISDIR}/rpm:"

SRC_URI += "\
    file://0001-Pass-sign-arguments-to-signature-deletion-too.patch \
    file://0002-Beat-some-sense-into-rpmsign-cli-parsing.patch \
    file://0003-Fix-thinko-typo-in-file-signing-error-message.patch \
    file://0004-Bury-get_fskpass-inside-rpmsign-utility.patch \
    file://0005-Dont-advertise-file-signing-features-if-support-not-.patch \
    file://0006-Remove-bunch-of-redundant-environ-declarations.patch \
    file://0007-Dont-push-NULL-bodied-macros-in-case-of-get_fskpass-.patch \
    file://0008-Move-key-password-helper-variables-to-local-scope.patch \
    file://0009-Use-rpm-memory-allocator-so-we-dont-need-to-check-fo.patch \
    file://0010-Fix-a-number-of-problems-in-get_fskpass.patch \
    file://0011-Bump-file-digests-to-SHA256-by-default-finally.patch \
"

PACKAGECONFIG = "${@bb.utils.contains('DISTRO_FEATURES', 'ima', 'imaevm', '', d)}"

IMA signing support is provided by RPM plugin.
EXTRA_OECONF_remove += "\
    --disable-plugins \
"
