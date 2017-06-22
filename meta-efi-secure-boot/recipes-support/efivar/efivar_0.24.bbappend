FILESEXTRAPATHS_prepend := "${THISDIR}/efivar:"

SRC_URI += "\
    file://Remove-use-of-deprecated-readdir_r.patch \
"

# In dp.h, 'for' loop initial declarations are used
CFLAGS_append = " -std=gnu99"

# In order to install headers and libraries to sysroot
do_install_append() {
    oe_runmake DESTDIR=${D} install
}
