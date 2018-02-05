CFLAGS_remove += "-pie -fpie"

# We need -no-pie in case the default is to generate pie code.
#
do_compile_append_class-target() {
    ${CC} ${CFLAGS} ${LDFLAGS} -no-pie -static \
        sys-utils/switch_root.o \
        -o switch_root.static
}

do_install_append_class-target() {
    install -d "${D}${sbindir}"
    install -m 0700 "${B}/switch_root.static" \
        "${D}${sbindir}/switch_root.static"
}

PACKAGES =+ "${PN}-switch-root.static"

FILES_${PN}-switch-root.static = "${sbindir}/switch_root.static"
