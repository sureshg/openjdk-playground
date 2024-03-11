# Security & Certificates

<!-- TOC -->
* [Security & Certificates](#security--certificates)
    * [CA Certs and Certificate Transparency Logs](#ca-certs-and-certificate-transparency-logs)
    * [OpenSSL & Keytool](#openssl--keytool)
    * [OpenJDK](#openjdk)
    * [Self Signed Certs](#self-signed-certs)
    * [cURL: Auth/Mutual TLS](#curl-authmutual-tls)
    * [Add CACerts](#add-cacerts)
    * [GPG/OpenPGP](#gpgopenpgp)
    * [Tools](#tools)
    * [TLS Debugging](#tls-debugging)
    * [Misc](#misc)
    * [TrustStore](#truststore)
    * [Cryptography](#cryptography)
<!-- TOC -->

### CA Certs and Certificate Transparency Logs

* [Certificate Search - crt.sh](https://crt.sh/)
* https://crt.sh/test-websites?trustedBy=Java
* [LintCert](https://crt.sh/lintcert)
* [Cert Lint Library](https://github.com/amazon-archives/certlint)
* [CT Logs](https://certificate.transparency.dev/logs/)

### OpenSSL & Keytool

* **Extract Certificate from Server**

```bash
# Extract the server certificates.
$ echo -n | openssl s_client -showcerts -connect google.com:443 | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > globalsign.crt

# OR use java keytool
$ keytool -printcert -rfc -sslserver google.com:443 > google.pem

# Show full cert chain (with subject and expiry)
$ echo | openssl s_client -showcerts -connect google.com:443 2>/dev/null | while openssl x509 -noout -subject -dates 2>/dev/null; do : ; done

# Extract and show cert details
$ echo | openssl s_client -showcerts -connect google.com:443 2>/dev/null | openssl x509 -inform pem -noout -text

# Extract TLS public keys in Cert pinning format
$ openssl s_client -connect 'dns.google.com:443' 2>&1 < /dev/null | sed -n '/-----BEGIN/,/-----END/p' | openssl x509 -noout -pubkey | openssl asn1parse -noout -inform pem -out /dev/stdout | openssl dgst -sha256 -binary | openssl base64

$ curl -vvI https://google.com 2>&1 | grep -i date
```

* **Create `PKCS#12` trust-store from pem**

```bash
# Create/Add trust store
$ keytool -importcert -trustcacerts -alias globalsign-rootca -storetype PKCS12 -keystore globalsign-rootca.p12 -storepass changeit -file globalsign.crt

# Add intermediate certs (Optional)
$ keytool -importcert -keystore globalsign-rootca.p12 -alias CA-intermediate -storepass changeit -file CA-intermediate.cer

# Show PKCS#12 info.
$ openssl pkcs12 -info -password pass:changeit -in globalsign-rootca.p12
$ keytool -list -keystore globalsign-rootca.p12 --storetype pkcs12 -storepass changeit -v


# OpenSSL: Create a new PKCS#12 store from certs
$ openssl pkcs12 -export -chain -out keystore.p12 \
                  -inkey private.key -password pass:test123 \
                  -in client.crt -certfile client.crt \
                  -CAfile cacert.crt -caname root-ca \
                  -name client-key

# Convert a PEM certificate file to PKCS#12 without private key.
$ openssl pkcs12 -export -nokeys -in certificate.pem -out keystore.p12

# Change the keystore password
# Convert PKCS#12 to PEM (don't encrypt private keys)
$ openssl pkcs12 -in keystore.p12 -out keystore.pem -nodes

# Export to PKCS#12 with new password
$ openssl pkcs12 -export -in keystore.pem -nodes -out keystore.p12

# Verify if a Private Key Matches a Certificate
$ openssl x509 -noout -modulus -in cert.pem | openssl md5
$ openssl rsa  -noout -modulus -in cert.key | openssl md5
```

* **Extract certs from `PKCS#12`**

```bash
# Client Certificate (Remove "-clcerts" to include intermediate/root certs)
$ openssl pkcs12 -in keystore.p12 -nodes -clcerts -nokeys -passin pass:<password>  -out client.crt

# Certificate Key (Remove "-nodes" for encrypted)
$ openssl pkcs12 -in keystore.p12 -nodes -nocerts -passin pass:<password> -out client.key

# Decrypt Key
$ openssl rsa -in client-encrypted.key -passin pass:<password> -out client.key

# CA cert
$ openssl pkcs12 -in keystore.p12 -nodes -nokeys -cacerts -passin pass:<password> -out cacert.crt

# Remove attribtes (Optional)
# openssl pkcs12 -in keystore.p12 -nodes  -nokeys -passin pass:<password> | openssl x509 -out client.crt
# openssl pkcs12 -in keystore.p12 -nodes -nocerts -passin pass:<password> | openssl rsa -out client.key
# openssl pkcs12 -in keystore.p12 -nodes -nokeys -cacerts -passin pass:<password> | openssl x509 -out cacert.crt

# Convert to PKCS8 private key (For java)
$ openssl pkcs8 -topk8 -inform PEM -outform DER -in cert.pem -out out.pem -nocrypt
```

* **Show all certs from System truststore**

```bash
# Using Openssl (CentOS/Ubuntu)
$ while openssl x509 -noout -subject -issuer -dates; do echo ........... ; done < $(find -L /etc/ssl/certs -regex ".*/ca-\(bundle\|certificates\).crt") 2>/dev/null | grep -i subject

# Using java keytool
$ keytool -printcert -file /etc/ssl/certs/ca-bundle.crt | grep -i issuer

# Using some awk trick
$ awk -v cmd='openssl x509 -noout -subject -dates ' '/BEGIN/{close(cmd)};{print | cmd}' < /etc/ssl/certs/ca-bundle.crt
```

### OpenJDK

* List all [JDK cacerts](https://seanjmullan.org/blog/2022/03/23/jdk18#pki) using `OpenSSL`

  ```bash
  # Since JDK 18, cacert migrated to Password-Less PKCS12(https://jdk.java.net/18/release-notes#JDK-8275252)
  $ openssl pkcs12 \
            -cacerts \
            -chain \
            -nokeys \
            -nomacver \
            -in "$(find -L $JAVA_HOME -name cacerts)" \
            -passin pass: | grep -i "subject="
  ```


* Show all JDK CA Certs using `Keytool`

  ```bash
  # JDK 17+
  $ keytool -list -rfc -cacerts

  # Or using cacerts location.
  $ JDK_CACERT="$(find -L "${JAVA_HOME:-$(dirname $(readlink -f "$(which java)"))/..}" -name cacerts -print -quit)"

  $ keytool -list \
            -rfc \
            -storetype pkcs12 \
            -storepass changeit \
            -keystore "${JDK_CACERT}"

  # Older JDKs
  $ keytool -list \
        -storetype jks \
        -storepass changeit \
        -keystore "${JDK_CACERT}"
  ```

    * [OpenJDK CACerts](https://github.com/openjdk/jdk/tree/master/src/java.base/share/data/cacerts)
    * [Android CACerts](https://android.googlesource.com/platform/system/ca-certificates/+/master/files)

### Self Signed Certs

* [Add self-generated RootCA to OSes](https://www.bounca.org/tutorials/install_root_certificate.html)

* [Setup CA on CentsOs](https://www.digitalocean.com/community/tutorials/how-to-set-up-and-configure-a-certificate-authority-ca-on-centos-8)

```bash
# For OpenSSL ≥ 1.1.1
$ openssl req -x509 -newkey rsa:4096 -sha256 -days 3650 -nodes \
          -keyout example.key -out example.pem \
          -subj "/C=US/ST=CA/L=SanJose/O=Company Name/OU=Org/CN=www.example.com" \
          -addext "subjectAltName=DNS:example.com,DNS:www.example.net,IP:10.0.0.1"
# For ECC
$ openssl req -x509 -newkey ec -pkeyopt ec_paramgen_curve:secp384r1 -days 3650 \
          -nodes -keyout example.key -out example.pem -subj "/CN=localhost" \
          -addext "subjectAltName=DNS:example.com,DNS:*.example.com,IP:127.0.0.1"

# See certificate details (in text format, no cert output)
$ openssl x509 -in example.pem -text -noout
# Instead of text form, just print subject/issuer/dates
$ openssl x509 -in example.pem -noout -subject -issuer -dates

# Show SAN entries
$ openssl x509 -text -in example.pem -noout | grep -A1 'Subject Alternative Name'
$ openssl x509 -text -noout -in example.pem -certopt no_subject,no_header,no_version,no_serial,no_signame,no_validity,no_issuer,no_pubkey,no_sigdump,no_aux

# Show public key of a cert
$ openssl x509 -in example.pem -pubkey -noout

# Get public key from a private key
$ openssl pkey -check -in example.key -pubout -outform PEM

# Check if the private key belongs to the cert
$ diff <(openssl x509 -pubkey -in example.pem -noout) <(openssl pkey -check -in example.key -pubout -outform PEM)

# OR create a test TLS server that will verify that a key and certificate match
$ openssl s_server -accept 443 -www -key example.key -cert example.pem


# Using OpenSSL ≤ 1.1.0
$ openssl req -x509 -newkey rsa:4096 -sha256 -days 3650 -nodes \
          -keyout example.key -out example.pem -subj "/CN=example.com" \
          -extensions san \
          -config <(echo '[req]'; echo 'distinguished_name=req';
          echo '[san]'; echo 'subjectAltName=DNS:example.com,DNS:www.example.net,IP:10.0.0.1')

```

### cURL: Auth/Mutual TLS

```bash
# mTLS Authentication using PKCS#12 bundle
$ curl -v \
       --cert-type P12 \
       --cert ~/keystore.p12:xxxxx \
       -X GET "https://my-server:443/api"


# Mutual TLS using PEM files
$ curl -v \
       --cacert ca.pem \
       --key client.key \
       --cert client.pem \
       --resolve "my-server:443:8.8.8.8" \
       -X GET "https://my-server:443/api"
```

### Add CACerts

- To JDK Truststore

  ```bash
  #!/usr/bin/env bash

  certs=("https://pki.service.com/cacert1.crt"
         "https://pki.service.com/cacert2.crt")

  JDK_CACERT="$(find -L "${JAVA_HOME:-$(dirname $(readlink -f "$(which java)"))/..}" -name cacerts -print -quit)"
  echo "Using JDK CACert: ${JDK_CACERT}"

  pushd /tmp >/dev/null || exit 1

  for cert in "${certs[@]}"; do
    echo "Downloading $cert ..."
    cert_file="$(basename "$cert")"
    curl -fsSL "$cert" -o "${cert_file}"

    echo "Installing ${cert_file} ..."
    keytool -importcert \
            -noprompt \
            -trustcacerts \
            -file "${cert_file}" \
            -alias "${cert_file}" \
            -keystore "${JDK_CACERT}" \
            -storepass changeit

    rm -f "${cert_file}"
  done

  popd >/dev/null || exit 1
  ```

- IntelliJ Truststore

  ```bash
  $ cacerts="$(find "$HOME/Library/Application Support/JetBrains/IntelliJIdea2022.3" -name cacerts)"
  $ keytool -list -keystore "$cacerts" -storetype pkcs12 -storepass changeit
  $ keytool -importcert \
            -trustcacerts \
            -alias rootca \
            -storetype PKCS12 \
            -keystore $cacerts \
            -storepass changeit \
            -file "$HOME/Desktop/RootCA-SHA256.crt"
  $ keytool -list -keystore "$cacerts" -storetype pkcs12 -storepass changeit
  ```

### GPG/OpenPGP

* [**Setup GPG on MacOS - IntelliJ
  Doc**](https://www.jetbrains.com/help/idea/2022.2/set-up-GPG-commit-signing.html#47b414b3)

* [GPG Key Prepare](https://github.com/s4u/sign-maven-plugin/blob/master/src/site/markdown/key-prepare.md)

* GPG commands

  ```bash
  # GPU key files
  $ ~/.gnupg

  # Show all keys
  $ gpg --list-keys --keyid-format=[long|short]

  # List secrets keys
  $ gpg --list-secret-keys --keyid-format=short

  # Update all keys from a keyserver
  $ gpg --refresh-keys [--keyserver pgp.mit.edu]

  $ gpg --recv-key XXXXXX
  $ gpg --delete-key  "xxx@gmail.com"
  ```


* [Renew GPG Key](https://gist.github.com/krisleech/760213ed287ea9da85521c7c9aac1df0)

  ```bash
  # Note: The private key can never expire

  # Get the key id
  $ gpg --list-keys --keyid-format SHORT

  # Comment out 'no-tty' in ~/.gnupg/gpg.conf
  $ gpg --edit-key C8B53CA1
  $ gpg> list
         expire // type 1y
         key 1  // renew encryption subkey
         expire // type 1y
         trust
         save
         quit

  # Export for backup
  $ gpg -a --export C8B53CA1 > sureshg.gpg.public
  $ gpg -a --export-secret-keys C8B53CA1 > sureshg.gpg.private

  # Send it to key servers
  $ gpg --keyserver keyserver.ubuntu.com --send-keys C8B53CA1
  $ gpg --keyserver pgp.mit.edu --send-keys C8B53CA1

  # Update GPG key in Github
  $ cat sureshg.gpg.public| pbcopy
  $ https://github.com/settings/gpg/new
  ```

### Tools

* [BadSSL](https://badssl.com/)

* [Mastering two way TLS in Java](https://github.com/Hakky54/mutual-tls-ssl)

* [sslcontext-kickstart](https://github.com/Hakky54/sslcontext-kickstart)

* [Cert Ripper](https://github.com/Hakky54/certificate-ripper)

* [Extract TLS Secrets for Wireshark](https://github.com/neykov/extract-tls-secrets)

* [mkcert - Locally Trusted Self-Signed Certs](https://github.com/FiloSottile/mkcert)

* [Keystore Explorer](https://keystore-explorer.org/)

* https://github.com/scop/portecle

* https://github.com/mitmproxy/mitmproxy

### TLS Debugging

* [Debugging TLS Connection in Java](https://docs.oracle.com/en/java/javase/20/security/java-secure-socket-extension-jsse-reference-guide.html#GUID-31B7E142-B874-46E9-8DD0-4E18EC0EB2CF)

* [Java Security Guide](https://docs.oracle.com/en/java/javase/20/security/java-security-overview1.html)

```bash
$ java -Djavax.net.debug=help MyApp
# java -Djavax.net.debug=all MyApp
# java -Djavax.net.debug=ssl,handshake,data,trustmanager MyApp

help           print the help messages
expand         expand debugging information

all            turn on all debugging
ssl            turn on ssl debugging

The following can be used with ssl:
  record       enable per-record tracing
  handshake    print each handshake message
  keygen       print key generation data
  session      print session activity
  defaultctx   print default SSL initialization
  sslctx       print SSLContext tracing
  sessioncache print session cache tracing
  keymanager   print key manager tracing
  trustmanager print trust manager tracing
  pluggability print pluggability tracing

  handshake debugging can be widened with:
  data         hex dump of each handshake message
  verbose      verbose handshake message printing

  record debugging can be widened with:
  plaintext    hex dump of record plaintext
  packet       print raw SSL/TLS packets
```

### Misc

* https://www.sslshopper.com/article-most-common-java-keytool-keystore-commands.html
* [Export Certificates and Private Key from a PKCS#12](https://www.ssl.com/how-to/export-certificates-private-key-from-pkcs12-file-with-openssl/)
* https://www.rapidsslonline.com/blog/simple-guide-java-keytool-keystore-commands/

### TrustStore

* [Keystore vs. Truststore](https://www.educative.io/answers/keystore-vs-truststore)
* https://www.baeldung.com/java-keystore-truststore-difference

### Cryptography

* https://www.udacity.com/course/applied-cryptography--cs387
* https://www.coursera.org/learn/crypto#syllabus
