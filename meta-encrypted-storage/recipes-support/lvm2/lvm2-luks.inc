#
# Copyright (C) 2019 Wind River Systems, Inc.
#

FILESEXTRAPATHS:prepend := "${THISDIR}/lvm2:"

SRC_URI += "file://0001-10-dm.rules.in-Fix-dmcrypt-hanging-on-hand-over-from.patch"
