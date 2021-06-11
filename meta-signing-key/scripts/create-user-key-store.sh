#!/bin/bash

_S="${BASH_SOURCE[0]}"
_D=`dirname "$_S"`
ROOT_DIR="`cd "$_D" && pwd`"

KEYS_DIR="$ROOT_DIR/user-keys"
OPENSSL_DAYS="3650"
GPG_KEYNAME=
GPG_EMAIL=
GPG_COMMENT=
BOOT_GPG_KEYNAME=
BOOT_GPG_EMAIL=
BOOT_GPG_COMMENT=
EMPTY_PW=0
GPG_PASS=
GPG_BIN=${GPG_BIN=gpg}
IMA_PASS=
gpg_key_name="PKG-SecureCore"
gpg_email="SecureCore@foo.com"
gpg_comment="Signing Key"
boot_gpg_key_name="BOOT-SecureCore"
boot_gpg_email="SecureCore@foo.com"
boot_gpg_comment="Bootloader Signing Key"

function show_help()
{
    cat <<EOF
$1 - creation tool for user key store

(C)Copyright 2017, Jia Zhang <lans.zhang2008@gmail.com>
(C)Copyright 2019, Jason Wessel <jason.wessel@windriver.com> Wind River Systems, Inc.

Usage: $1 options...

Options:
 -d <dir>
    Set the path to save the generated user keys.
    Default: `pwd`/user-keys
 -c <gpg key comment>
    Set the RPM/OStree gpg's key name
    Default: $gpg_comment
 -n <gpg key name>
    Set the RPM/OStree gpg's key name
    Default: $gpg_key_name
 -m <gpg key owner's email address>
    Set the RPM/OStree owner's email address of the gpg key
    Default: $gpg_email
 -rp <OSTree/RPM GPG passphrase>
 -bgp <Boot Loader GPG passphrase>
 -bp <Boot loader config password>
 -ip <IMA passphrase>
 --days          Specify the number of days to make a certificate valid for
                 Default: $OPENSSL_DAYS
 -h|--help       Show this help information.
Overides:
 -bc <gpg key comment>
    Force set the gpg key comment for the boot loader
 -bn <gpg key name>
    Force set the gpg key name for the boot loader
 -bm <gpg key owner's email address>
    Force set the gpg email address for the boot loader

EOF
}

print_fatal() {
    printf "\033[1;35m"
    echo "$@"
    printf "\033[0m"
    exit 1
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
        -bc)
            shift && BOOT_GPG_COMMENT="$1"
            ;;
        -n)
            shift && GPG_KEYNAME="$1"
            ;;
        -bn)
            shift && BOOT_GPG_KEYNAME="$1"
            ;;
        -m)
            shift && GPG_EMAIL="$1"
            ;;
        -bm)
            shift && BOOT_GPG_EMAIL="$1"
            ;;
        -rp)
            shift && GPG_PASS="$1"
            ;;
        -bgp)
            shift && BOOT_GPG_PASS="$1"
            ;;
        -bp)
            shift && BOOT_PASS="$1"
            ;;
        -ip)
            shift && IMA_PASS="$1"
            ;;
        --days)
            shift && OPENSSL_DAYS="$1"
            ;;
        -h|--help)
            show_help `basename $0`
            exit 0
            ;;
        *)
            print_fatal "Unsupported option $opt"
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
BOOT_KEYS_DIR="$KEYS_DIR/boot_keys"
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
            -sha256 -nodes -days $OPENSSL_DAYS \
            -subj "$subject" \
            -keyout "$key_dir/$key_name.key" \
            -out "$key_dir/$key_name.crt" \
                || print_fatal "openssl failure"
    else
        if [ -z "$encrypted" ]; then
            openssl req -new -newkey rsa:2048 \
                -sha256 -nodes \
                -subj "$subject" \
                -keyout "$key_dir/$key_name.key" \
                -out "$key_dir/$key_name.csr" \
                    || print_fatal "openssl failure"
        else
            # Prompt user to type the password
            if [ "$IMA_PASS" = "" ] ; then
                openssl genrsa -aes256 -out "$key_dir/$key_name.key" 2048 \
                        || print_fatal "openssl failure"

                openssl req -new -sha256 \
                    -subj "$subject" \
                    -key "$key_dir/$key_name.key" \
                    -out "$key_dir/$key_name.csr" \
                        || print_fatal "openssl failure"
            else
                openssl genrsa -aes256 -passout "pass:$IMA_PASS" \
                    -out "$key_dir/$key_name.key" 2048 \
                        || print_fatal "openssl failure"

                openssl req -new -sha256 -passin "pass:$IMA_PASS" \
                    -subj "$subject" \
                    -key "$key_dir/$key_name.key" \
                    -out "$key_dir/$key_name.csr" \
                        || print_fatal "openssl failure"
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
            -set_serial 1 -days $OPENSSL_DAYS \
            -extfile "$ROOT_DIR/openssl.cnf" -extensions v3_req \
            -out "$key_dir/$key_name.crt" \
                || print_fatal "openssl failure"

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

create_boot_pw_key() {
        local bootprog=`which grub-mkpasswd-pbkdf2`
        if [ "$bootprog" = "" ] ; then
            # Locate grub2-mkpasswd-pbkdf2 on RHEL/CentOS/Fedora
            bootprog=`which grub2-mkpasswd-pbkdf2`
            if [ "$bootprog" = "" ] ; then
                print_fatal "ERROR could not locate \"grub-mkpasswd-pbkdf2\" or \"grub2-mkpasswd-pbkdf2\" please install it or set the path to the host native sysroot"
            fi
        fi
        (echo "$BOOT_PASS"; echo "$BOOT_PASS") | $bootprog > $BOOT_KEYS_DIR/boot_cfg_pw.tmp
        if [ $? != 0 ] ; then
            print_fatal "ERROR failed to run grub-mkpasswd-mpkdf2 to generate password"
        fi
        cat $BOOT_KEYS_DIR/boot_cfg_pw.tmp |grep grub.pbkdf2 |sed -e 's/.*grub.pbkdf2/grub.pbkdf2/' > $BOOT_KEYS_DIR/boot_cfg_pw
        rm -f $BOOT_KEYS_DIR/boot_cfg_pw.tmp

}

create_gpg_user_key() {
    local gpg_ver=`$GPG_BIN --version | head -1 | awk '{ print $3 }' | awk -F. '{ print $1 }'`
    local key_dir="$1"

    [ ! -d "$key_dir" ] && mkdir -m 0700 -p "$key_dir"

    local priv_key="$key_dir/$2-GPG-PRIVKEY-$3"
    local pub_key="$key_dir/$2-GPG-KEY-$3"
    local name_real="$3"
    local USE_PW="Passphrase: $4"
    local pw="$4"
    local comment="$5"
    local email="$6"

    if [ "$gpg_ver" != "1" -a "$gpg_ver" != "2" ]; then
        print_fatal "ERROR: GPG Version 1 or 2 are required for key generation and signing"
    fi
    cat >"$key_dir/gen_keyring" <<EOF
Key-Type: RSA
Key-Length: 4096
Name-Real: $name_real
Name-Comment: $comment
Name-Email: $email
Expire-Date: 0
$USE_PW
%commit
%echo keyring $name_real created
EOF

    pinentry=""
    if [ "$gpg_ver" = "2" ] ; then
            gpg_ver_whole=`gpg --version | head -1 | awk '{ print $3 }'`
            if [ "$gpg_ver_whole" != "2.0.22" ] ; then
                pinentry="--pinentry-mode=loopback"
                echo "allow-loopback-pinentry" > $key_dir/gpg-agent.conf
            fi
            gpg-connect-agent --homedir "$key_dir" reloadagent /bye
            if [ $? != 0 ] ; then
                gpg-agent --homedir "$key_dir" --daemon
            fi
    fi
    $GPG_BIN --homedir "$key_dir" --batch --yes --gen-key "$key_dir/gen_keyring"
    if [ $? != 0 ] ; then
            print_fatal "Error with keyring generation"
    fi

    $GPG_BIN --homedir "$key_dir" -k

    $GPG_BIN --homedir "$key_dir" --export --armor "$name_real" > "$pub_key" || print_fatal "gpg export failed"
    if [ "$2" = "BOOT" ] ; then
            $GPG_BIN --homedir "$key_dir" --export "$name_real" > "$key_dir/boot_pub_key" || print_fatal "gpg export failed"
    fi

    $GPG_BIN --homedir "$key_dir" --export-secret-keys $pinentry --passphrase "$pw" --armor "$3" > "$priv_key" || print_fatal "gpg export failed"

    rm -f "$key_dir/gen_keyring"
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

    echo "Creating the gpg key for RPM/OSTree"
    create_gpg_user_key "$RPM_KEYS_DIR" RPM "$gpg_key_name" "$GPG_PASS" "$gpg_comment" "$gpg_email"

    echo "Creating the gpg key for boot loader"
    create_gpg_user_key "$BOOT_KEYS_DIR" BOOT "$boot_gpg_key_name" "$BOOT_GPG_PASS" "$boot_gpg_comment" "$boot_gpg_email"

    echo "Creating the password salt for boot"
    create_boot_pw_key
}

if [ -d "$KEYS_DIR" ] ; then
    print_fatal "ERROR: $KEYS_DIR already exists, please remove it, to allow for the creation of new keys."
fi

if [ ! -z "$GPG_KEYNAME" ]; then
    gpg_key_name="$GPG_KEYNAME"
else
    echo -n "Enter RPM/OSTree GPG keyname (use dashes instead of spaces) [default: $gpg_key_name]: "
    read val
    if [ ! -z "$val" ] ; then
        gpg_key_name=$val
    fi
fi

if [ ! -z "$GPG_EMAIL" ]; then
    gpg_email=$GPG_EMAIL
else
    echo -n "Enter RPM/OSTree GPG e-mail address [default: $gpg_email]: "
    read val
    if [ ! -z "$val" ] ; then
        gpg_email=$val
    fi
fi

if [ ! -z "$GPG_COMMENT" ]; then
    gpg_comment=$GPG_COMMENT
else
    echo -n "Enter RPM/OSTREE GPG comment [default: $gpg_comment]: "
    read val
    if [ ! -z "$val" ] ; then
        gpg_comment=$val
    fi
fi

boot_gpg_key_name="BOOT-${gpg_key_name#PKG-}"
boot_gpg_email="$gpg_email"
boot_gpg_comment="$gpg_comment"
if [ ! -z "$BOOT_GPG_KEYNAME" ]; then
    boot_gpg_key_name="$BOOT_GPG_KEYNAME"
fi
if [ ! -z "$BOOT_GPG_EMAIL" ]; then
    boot_gpg_email=$BOOT_GPG_EMAIL
fi
if [ ! -z "$BOOT_GPG_COMMENT" ]; then
    boot_gpg_comment=$BOOT_GPG_COMMENT
fi

echo "  Using boot loader gpg name: $boot_gpg_key_name"
echo "  Using boot loader gpg email: $boot_gpg_email"
echo "  Using boot loader gpg comment: $boot_gpg_comment"
echo "    Press control-c now if and use -bn -bm -bc arguments if you want"
echo "    different values other than listed above"

# Sanity checks on values so far

if [ "$boot_gpg_key_name" = "$gpg_key_name" ] ; then
    echo "==================================="
    echo " RPM_GPG_NAME = \"$gpg_key_name\""
    echo " BOOT_GPG_NAME = \"$boot_gpg_key_name\""
    print_fatal "ERROR: The gpg key names must be unique"
fi
if [ "$boot_gpg_key_name" != "${boot_gpg_key_name/$gpg_key_name/}" ] ; then
    echo "==================================="
    echo " RPM_GPG_NAME = \"$gpg_key_name\""
    echo " BOOT_GPG_NAME = \"$boot_gpg_key_name\""
    print_fatal "ERROR: The RPM/OSTree gpg key name cannot be a subset of the boot loader gpg key name"
fi
if [ "$gpg_key_name" != "${gpg_key_name/$boot_gpg_key_name/}" ] ; then
    echo "==================================="
    echo " RPM_GPG_NAME = \"$gpg_key_name\""
    echo " BOOT_GPG_NAME = \"$boot_gpg_key_name\""
    print_fatal "ERROR: The boot loader gpg key name cannot be a subset of the RPM/OSTREE gpg key name"
fi

# Passwor section next
if [ -z $GPG_PASS ]; then
    while [ 1 ] ; do
        echo -n "Enter RPM/OSTREE passphrase: "
        read val
        if [ ! -z "$val" ] ; then
            GPG_PASS=$val
            break
        fi
    done
fi
if [ -z $IMA_PASS ]; then
    while [ 1 ] ; do
        echo -n "Enter IMA passphrase: "
        read val
        if [ ! -z "$val" ] ; then
            IMA_PASS=$val
            break
        fi
    done
fi
if [ -z $BOOT_GPG_PASS ]; then
    while [ 1 ] ; do
        echo -n "Enter boot loader GPG passphrase: "
        read val
        if [ ! -z "$val" ] ; then
            BOOT_GPG_PASS=$val
            break
        fi
    done
fi
if [ -z $BOOT_PASS ]; then
    while [ 1 ] ; do
        echo -n "Enter boot loader locked configuration password(e.g. grub pw): "
        read val
        if [ ! -z "$val" ] ; then
            BOOT_PASS=$val
            break
        fi
    done
fi

create_user_keys

cat <<EOF>$KEYS_DIR/keys.conf
MASTER_KEYS_DIR = "$(readlink -f $KEYS_DIR)"

IMA_KEYS_DIR = "\${MASTER_KEYS_DIR}/ima_keys"
IMA_EVM_KEY_DIR = "\${MASTER_KEYS_DIR}/ima_keys"
RPM_KEYS_DIR = "\${MASTER_KEYS_DIR}/rpm_keys"
BOOT_KEYS_DIR = "\${MASTER_KEYS_DIR}/boot_keys"
MOK_SB_KEYS_DIR = "\${MASTER_KEYS_DIR}/mok_sb_keys"
SYSTEM_TRUSTED_KEYS_DIR = "\${MASTER_KEYS_DIR}/system_trusted_keys"
SECONDARY_TRUSTED_KEYS_DIR = "\${MASTER_KEYS_DIR}/secondary_trusted_keys"
MODSIGN_KEYS_DIR = "\${MASTER_KEYS_DIR}/modsign_keys"
UEFI_SB_KEYS_DIR = "\${MASTER_KEYS_DIR}/uefi_sb_keys"
GRUB_PUB_KEY = "\${MASTER_KEYS_DIR}/boot_keys/boot_pub_key"
GRUB_PW_FILE = "\${MASTER_KEYS_DIR}/boot_keys/boot_cfg_pw"
OSTREE_GPGDIR = "\${MASTER_KEYS_DIR}/rpm_keys"

RPM_GPG_NAME = "$gpg_key_name"
RPM_GPG_PASSPHRASE = "$GPG_PASS"
RPM_FSK_PASSWORD = "$IMA_PASS"
BOOT_GPG_NAME = "$boot_gpg_key_name"
BOOT_GPG_PASSPHRASE = "$BOOT_GPG_PASS"
OSTREE_GPGID = "$gpg_key_name"
OSTREE_GPG_PASSPHRASE = "$GPG_PASS"
OSTREE_GRUB_PW_FILE = "\${GRUB_PW_FILE}"
EOF

cat<<EOF
## The following variables need to be entered into your local.conf
## in order to use the new signing keys:

$(cat $KEYS_DIR/keys.conf)

## Please save the values above to your local.conf
## Or copy and uncomment the following line:
# require $(readlink -f $KEYS_DIR/keys.conf)
EOF

