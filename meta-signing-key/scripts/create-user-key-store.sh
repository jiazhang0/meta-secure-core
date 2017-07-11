#!/bin/bash

_S="${BASH_SOURCE[0]}"
_D=`dirname "$_S"`
ROOT_DIR="`cd "$_D" && pwd`"

KEYS_DIR="$ROOT_DIR/user-keys"

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

pem2der() {
    local src="$1"
    local dst="${src/.crt/.der}"

    openssl x509 -in "$src" -outform DER -out "$dst"
}

ca_sign() {
    local key_dir="$1"
    local key_name="$2"
    local ca_key_dir="$3"
    local ca_key_name="$4"
    local subject="$5"
    local encrypted="$6"

    # Self signing ?
    if [ "$key_name" = "$ca_key_name" ]; then
        openssl req -new -x509 -newkey rsa:2048 \
            -sha256 -nodes -days 3650 \
            -subj "$subject" \
            -keyout "$key_dir/$key_name.key" \
            -out "$key_dir/$key_name.crt"
    else
        if [ -z "$encrypted" ]; then
            openssl req -new -newkey rsa:2048 \
                -sha256 -nodes \
                -subj "$subject" \
                -keyout "$key_dir/$key_name.key" \
                -out "$key_dir/$key_name.csr"
        else
            # Prompt user to type the password
            openssl genrsa -des3 -out "$key_dir/$key_name.key" 2048

            openssl req -new -sha256 \
                -subj "$subject" \
                -key "$key_dir/$key_name.key" \
                -out "$key_dir/$key_name.csr"
        fi

        local ca_cert="$ca_key_dir/$ca_key_name.crt"
        local ca_cert_form="PEM"

        [ ! -s "$ca_cert" ] && {
            ca_cert="$ca_key_dir/$ca_key_name.der"
            ca_cert_form="DER"
        }

        openssl x509 -req -in "$key_dir/$key_name.csr" \
            -CA "$ca_cert" \
            -CAform "$ca_cert_form" \
            -CAkey "$ca_key_dir/$ca_key_name.key" \
            -set_serial 1 -days 3650 \
            -extfile "$ROOT_DIR/openssl.cnf" -extensions v3_req \
            -out "$key_dir/$key_name.crt"

        rm -f "$key_dir/$key_name.csr"
    fi
}

create_uefi_sb_user_keys() {
    local key_dir="$UEFI_SB_KEYS_DIR"

    [ ! -d "$key_dir" ] && mkdir -p "$key_dir"

    ca_sign "$key_dir" PK "$key_dir" PK \
        "/CN=PK Certificate/"
    ca_sign "$key_dir" KEK "$key_dir" PK \
        "/CN=KEK Certificate"
    ca_sign "$key_dir" DB "$key_dir" KEK \
        "/CN=DB Certificate"
}

create_mok_sb_user_keys() {
    local key_dir="$MOK_SB_KEYS_DIR"

    [ ! -d "$key_dir" ] && mkdir -p "$key_dir"

    ca_sign "$key_dir" shim_cert "$key_dir" shim_cert \
        "/CN=Shim Certificate/"
    ca_sign "$key_dir" vendor_cert "$key_dir" vendor_cert \
        "/CN=Vendor Certificate/"
}

create_system_user_key() {
    local key_dir="$SYSTEM_KEYS_DIR"

    [ ! -d "$key_dir" ] && mkdir -p "$key_dir"

    ca_sign "$key_dir" system_trusted_key "$key_dir" system_trusted_key \
        "/CN=System Trusted Certificate/"
}

create_ima_user_key() {
    local key_dir="$IMA_KEYS_DIR"

    [ ! -d "$key_dir" ] && mkdir -p "$key_dir"

    ca_sign "$key_dir" x509_ima "$SYSTEM_KEYS_DIR" system_trusted_key \
        "/CN=IMA Trusted Certificate/" "enc"

    pem2der "$key_dir/x509_ima.crt"
    rm -f "$key_dir/x509_ima.crt"
}

create_user_keys() {
    echo "Creating the user keys for UEFI Secure Boot"
    create_uefi_sb_user_keys

    echo "Creating the user keys for MOK Secure Boot"
    create_mok_sb_user_keys

    echo "Creating the user key for system"
    create_system_user_key

    echo "Creating the user key for IMA appraisal"
    create_ima_user_key
}

create_user_keys

