#!/bin/bash

_S="${BASH_SOURCE[0]}"
_D=`dirname "$_S"`
ROOT_DIR="`cd "$_D" && pwd`"

KEYS_DIR="$ROOT_DIR/user-keys"
GPG_KEYNAME=
GPG_EMAIL=
GPG_COMMENT=
EMPTY_PW=0
GPG_PASS=
GPG_BIN=${GPG_BIN=gpg}
IMA_PASS=
gpg_key_name="SecureCore"
gpg_email="SecureCore@foo.com"
gpg_comment="Package Signing Key"

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

 -c <gpg key comment>
    Set the gpg's key name
    Default: $gpg_comment

 -n <gpg key name>
    Set the gpg's key name
    Default: $gpg_key_name

 -m <gpg key ower's email address>
    Set the ower's email address of the gpg key
    Default: $gpg_email

 -rp <OSTree/RPM passphrase>

 -ip <IMA passphrase>

 -h|--help
    Show this help information.

EOF
}

print_critical() {
    printf "\033[1;35m"
    echo "$@"
    printf "\033[0m"
}

print_error() {
    printf "\033[1;31m"
    echo "$@"
    printf "\033[0m"
}

print_warning() {
    printf "\033[1;33m"
    echo "$@"
    printf "\033[0m"
}

print_info() {
    printf "\033[1;32m"
    echo "$@"
    printf "\033[0m"
}

print_verbose() {
    printf "\033[1;36m"
    echo "$@"
    printf "\033[0m"
}

while [ $# -gt 0 ]; do
    opt=$1
    case $opt in
        -d)
            shift && KEYS_DIR="$1"
            ;;
        -c)
            shift && GPG_COMMENT="$1"
            ;;
        -n)
            shift && GPG_KEYNAME="$1"
            ;;
        -m)
            shift && GPG_EMAIL="$1"
            ;;
	-rp)
	    shift && GPG_PASS="$1"
            ;;
	-ip)
	    shift && IMA_PASS="$1"
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
RPM_KEYS_DIR="$KEYS_DIR/rpm_keys"
MODSIGN_KEYS_DIR="$KEYS_DIR/modsign_keys"
SECONDARY_TRUSTED_KEYS_DIR="$KEYS_DIR/secondary_trusted_keys"

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
	    if [ "$GPG_PASS" = "" ] ; then
		openssl genrsa -des3 -out "$key_dir/$key_name.key" 2048

		openssl req -new -sha256 \
                    -subj "$subject" \
                    -key "$key_dir/$key_name.key" \
                    -out "$key_dir/$key_name.csr"
	    else
		openssl genrsa -des3 -passout "pass:$IMA_PASS" \
		    -out "$key_dir/$key_name.key" 2048
		openssl req -new -sha256 -passin "pass:$IMA_PASS" \
                    -subj "$subject" \
                    -key "$key_dir/$key_name.key" \
                    -out "$key_dir/$key_name.csr"
	    fi

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
    ca_sign "$key_dir" KEK "$key_dir" KEK \
        "/CN=KEK Certificate"
    ca_sign "$key_dir" DB "$key_dir" DB \
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

create_modsign_user_key() {
    local key_dir="$MODSIGN_KEYS_DIR"

    [ ! -d "$key_dir" ] && mkdir -p "$key_dir"

    ca_sign "$key_dir" modsign_key "$key_dir" modsign_key \
        "/CN=MODSIGN Certificate/"
}

create_secondary_user_key() {
    local key_dir="$SECONDARY_TRUSTED_KEYS_DIR"

    [ ! -d "$key_dir" ] && mkdir -p "$key_dir"

    ca_sign "$key_dir" secondary_trusted_key "$SYSTEM_KEYS_DIR" system_trusted_key \
        "/CN=Extra System Trusted Certificate/"
}

create_ima_user_key() {
    local key_dir="$IMA_KEYS_DIR"

    [ ! -d "$key_dir" ] && mkdir -p "$key_dir"

    ca_sign "$key_dir" x509_ima "$SYSTEM_KEYS_DIR" system_trusted_key \
        "/CN=IMA Trusted Certificate/" "enc"

    pem2der "$key_dir/x509_ima.crt"
    rm -f "$key_dir/x509_ima.crt"
}

create_rpm_user_key() {
    local gpg_ver=`$GPG_BIN --version | head -1 | awk '{ print $3 }' | awk -F. '{ print $1 }'`
    local key_dir="$RPM_KEYS_DIR"

    [ ! -d "$key_dir" ] && mkdir -m 0700 -p "$key_dir"

    local priv_key="$key_dir/RPM-GPG-PRIVKEY-$gpg_key_name"
    local pub_key="$key_dir/RPM-GPG-KEY-$gpg_key_name"

    if [ "$gpg_ver" != "1" -a "$gpg_ver" != "2" ]; then
	echo "ERROR: GPG Version 1 or 2 are required for key generation and signing"
	exit 1
    fi
    USE_PW=""
    if [ "$GPG_PASS" != "" ] ; then
	    USE_PW="Passphrase: $GPG_PASS"
    fi
    cat >"$key_dir/gen_rpm_keyring" <<EOF
Key-Type: RSA
Key-Length: 4096
Name-Real: $gpg_key_name
Name-Comment: $gpg_comment
Name-Email: $gpg_email
Expire-Date: 0
$USE_PW
%commit
%echo RPM keyring $gpg_key_name created
EOF

    pinentry=""
    if [ "$gpg_ver" = "2" ] ; then
	    pinentry="--pinentry-mode=loopback"
	    echo "allow-loopback-pinentry" > $key_dir/gpg-agent.conf
	    gpg-connect-agent --homedir "$key_dir" reloadagent /bye
    fi
    $GPG_BIN --homedir "$key_dir" --batch --yes --gen-key "$key_dir/gen_rpm_keyring"

    $GPG_BIN --homedir "$key_dir" -k

    $GPG_BIN --homedir "$key_dir" --export --armor "$gpg_key_name" > "$pub_key"

    $GPG_BIN --homedir "$key_dir" --export-secret-keys $pinentry --passphrase "$GPG_PASS" --armor "$gpg_key_name" > "$priv_key"

    rm -f "$key_dir/gen_rpm_keyring"
    cd "$key_dir"
    rm -rf openpgp-revocs.d private-keys-v1.d pubring.kbx* \
            trustdb.gpg* random_seed pubring.gpg* secring.gpg* gpg-agent.conf
    cd -
}

create_user_keys() {
    echo "Creating the user keys for UEFI Secure Boot"
    create_uefi_sb_user_keys

    echo "Creating the user keys for MOK Secure Boot"
    create_mok_sb_user_keys

    echo "Creating the user key for system"
    create_system_user_key

    echo "Creating the user key for system secondary trust"
    create_secondary_user_key

    echo "Creating the user key for modsign"
    create_modsign_user_key

    echo "Creating the user key for IMA appraisal"
    create_ima_user_key

    echo "Creating the user key for RPM"
    create_rpm_user_key
}

if [ -d "$KEYS_DIR" ] ; then
    echo "ERROR: $KEYS_DIR already exists, please remove it, to allow for the creation of new keys."
    exit 1
fi

if [ ! -z "$GPG_KEYNAME" ]; then
	gpg_key_name="$GPG_KEYNAME"
else
	echo -n "Enter GPG keyname [default: $gpg_key_name]: "
	read val
	if [ ! -z "$val" ] ; then
		gpg_key_name=$val
	fi
fi

if [ ! -z "$GPG_EMAIL" ]; then
	gpg_email=$GPG_EMAIL
else
	echo -n "Enter GPG e-mail address [default: $gpg_email]: "
	read val
	if [ ! -z "$val" ] ; then
		gpg_email=$val
	fi
fi

if [ ! -z "$GPG_COMMENT" ]; then
	gpg_email=$GPG_EMAIL
else
	echo -n "Enter GPG comment [default: $gpg_comment]: "
	read val
	if [ ! -z "$val" ] ; then
		gpg_email=$val
	fi
fi
if [ -z $GPG_PASS ]; then
	while [ 1 ] ; do
		echo -n "Enter RPM/OSTREE Passphrase: "
		read val
		if [ ! -z "$val" ] ; then
			GPG_PASS=$val
			break
		fi
	done
fi
if [ -z $IMA_PASS ]; then
	while [ 1 ] ; do
		echo -n "Enter IMA Passphrase: "
		read val
		if [ ! -z "$val" ] ; then
			IMA_PASS=$val
			break
		fi
	done
fi


create_user_keys

cat<<EOF
## The following variables need to be entered into your local.conf
## in order to use the new signing keys:

RPM_GPG_NAME = "$gpg_key_name"
RPM_GPG_PASSPHRASE = "$GPG_PASS"
RPM_FSK_PASSWORD = "$IMA_PASS"
OSTREE_GPGID = "$gpg_key_name"
OSTREE_GPG_PASSPHRASE = "$GPG_PASS"
WR_KEYS_DIR = "$KEYS_DIR"

## Please save the values above to your local.conf
EOF

