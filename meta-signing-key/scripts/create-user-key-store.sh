#!/bin/bash

KEYS_DIR="`pwd`/user-keys"

function show_help()
{
    cat <<EOF
$1 - creation tool for user key store

(C)Copyright 2017, Jia Zhang <lans.zhang2008@gmail.com>

Usage: $1 options...

Options:
 -d <dir>
    Set the path to save the generated user keys.
    Default: `pwd`/user-keys

 -h|--help
    Show this help information.

EOF
}

while [ $# -gt 0 ]; do
    opt=$1
    case $opt in
        -d)
            shift && KEYS_DIR="$1"
            ;;
        -h|--help)
            show_help `basename $0`
            exit 0
            ;;
        *)
            echo "Unsupported option $opt"
            exit 1
            ;;
    esac
    shift
done

echo "KEYS_DIR: $KEYS_DIR"

UEFI_SB_KEYS_DIR="$KEYS_DIR/uefi_sb_keys"
MOK_SB_KEYS_DIR="$KEYS_DIR/mok_sb_keys"
SYSTEM_KEYS_DIR="$KEYS_DIR/system_trusted_keys"
IMA_KEYS_DIR="$KEYS_DIR/ima_keys"

create_uefi_sb_user_keys() {
    local key_dir="$UEFI_SB_KEYS_DIR"

    [ ! -d "$key_dir" ] && mkdir -p "$key_dir"

    # PK is self-signed.
    openssl req -new -x509 -newkey rsa:2048 \
        -sha256 -nodes -days 3650 \
        -subj "/CN=PK Certificate for $USER@`hostname`/" \
        -keyout "$key_dir/PK.key" \
        -out "$key_dir/PK.pem"

    # KEK is signed by PK. 
    openssl req -new -newkey rsa:2048 \
        -sha256 -nodes \
        -subj "/CN=KEK Certificate for $USER@`hostname`" \
        -keyout "$key_dir/KEK.key" \
        -out "$key_dir/KEK.csr"

    openssl x509 -req -in "$key_dir/KEK.csr" \
        -CA "$key_dir/PK.pem" -CAkey "$key_dir/PK.key" \
        -set_serial 1 -days 3650 -out "$key_dir/KEK.pem"

    rm -f "$key_dir/KEK.csr"

    # DB is signed by KEK.
    openssl req -new -newkey rsa:2048 \
        -sha256 -nodes \
        -subj "/CN=DB Certificate for $USER@`hostname`" \
        -keyout "$key_dir/DB.key" \
        -out "$key_dir/DB.csr"

    openssl x509 -req -in "key_dir/DB.csr" \
        -CA "$key_dir/KEK.pem" -CAkey "$key_dir/KEK.key" \
        -set_serial 1 -days 3650 -out "$key_dir/DB.pem"

    rm -f "$key_dir/DB.csr"
}

create_mok_sb_user_keys() {
    local key_dir="$MOK_SB_KEYS_DIR"

    [ ! -d "$key_dir" ] && mkdir -p "$key_dir"

    openssl req -new -x509 -newkey rsa:2048 \
        -sha256 -nodes -days 3650 \
        -subj "/CN=Shim Certificate for $USER@`hostname`/" \
        -keyout "$key_dir/shim_cert.key" -out "$key_dir/shim_cert.pem"

    openssl req -new -x509 -newkey rsa:2048 \
        -sha256 -nodes -days 3650 \
        -subj "/CN=Vendor Certificate for $USER@`hostname`/" \
        -keyout "$key_dir/vendor_cert.key" -out "$key_dir/vendor_cert.pem"
}

create_system_trusted_keys() {
    local key_dir="$SYSTEM_KEYS_DIR"

    [ ! -d "$key_dir" ] && mkdir -p "$key_dir"

    openssl req -new -x509 -newkey rsa:2048 \
        -sha256 -nodes -days 3650 \
        -subj "/CN=System Trusted Certificate/" \
        -keyout "$key_dir/system_trusted_key.key" \
        -out "$key_dir/system_trusted_key.pem"
}

create_ima_user_keys() {
    local key_dir="$IMA_KEYS_DIR"

    [ ! -d "$key_dir" ] && mkdir -p "$key_dir"

    openssl req -new -x509 -newkey rsa:2048 \
        -sha256 -nodes -days 3650 \
        -subj "/CN=IMA Trusted Certificate/" \
        -keyout "$key_dir/x509_ima.key" \
        -outform DER -out "$key_dir/x509_ima.der"
}

create_user_keys() {
    echo "Creating the user keys for UEFI Secure Boot"
    create_uefi_sb_user_keys

    echo "Creating the user keys for MOK Secure Boot"
    create_mok_sb_user_keys

    echo "Creating the system trusted keys"
    create_system_trusted_keys

    echo "Creating the user keys for IMA appraisal"
    create_ima_user_keys
}

create_user_keys

