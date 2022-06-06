# Security & Certificates
-------------------------

[TOC]

### CA Certs and Certificate Transparency Logs

​

     * https://crt.sh/test-websites?trustedBy=Java
     * [Certificate Search - crt.sh](https://crt.sh/)

* [CT Logs](https://certificate.transparency.dev/logs/)

### OpenSSL & Keytool

----------------------

* Extract Certificate from Server

```bash
# Extract the server certificates.
$ echo -n | openssl s_client -showcerts -connect google.com:443 | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > globalsign.crt

# OR use java keytool
$ keytool -printcert -rfc -sslserver google.com:443 > google.pem

# Extract and show cert details
$ echo | openssl s_client -showcerts -connect google.com:443 2>/dev/null | openssl x509 -inform pem -noout -text

# Show certs expiry
$ echo | openssl s_client -servername google.com -connect google.com:443 2>/dev/null | openssl x509 -noout -dates

$ curl -vvI https://google.com 2>&1 | grep -i date
```



* Create `PKCS#12` trust-store from pem

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

# Change the keystore password
# Convert PKCS#12 to PEM (don't encrypt private keys)
$ openssl pkcs12 -in keystore.p12 -out keystore.pem -nodes

# Export to PKCS#12 with new password
$ openssl pkcs12 -export -in keystore.pem -nodes -out keystore.p12

# Verify if a Private Key Matches a Certificate
$ openssl x509 -noout -modulus -in cert.pem | openssl md5
$ openssl rsa  -noout -modulus -in cert.key | openssl md5
```



### Self Signed Certs

* [Add self-generated RootCA to OSes](https://www.bounca.org/tutorials/install_root_certificate.html)

* [Setup CA on CentsOs](https://www.digitalocean.com/community/tutorials/how-to-set-up-and-configure-a-certificate-authority-ca-on-centos-8)

```bash
# For OpenSSL ≥ 1.1.1
$ openssl req -x509 -newkey rsa:4096 -sha256 -days 3650 -nodes \
          -keyout example.key -out example.pem \
          -subj "/C=US/ST=CA/L=SanJose/O=Company Name/OU=Org/CN=www.example.com" \
          -addext "subjectAltName=DNS:example.com,DNS:www.example.net,IP:10.0.0.1"

# See certificate details
$ openssl x509 -in example.pem -text -noout

# Show public key of a cert
$ openssl x509 -in example.pem -pubkey -noout

# Get public key from a private key
$ openssl pkey -check -in example.key -pubout -outform PEM

# Check if the private key belongs to the cert
$ diff <(openssl x509 -pubkey -in example.pem -noout) <(openssl pkey -check -in example.key -pubout -outform PEM)

# OR create a test TLS server that will verify that a key and certificate match
$ openssl s_server -key example.key -cert example.pem


# Using OpenSSL ≤ 1.1.0
$ openssl req -x509 -newkey rsa:4096 -sha256 -days 3650 -nodes \
          -keyout example.key -out example.pem -subj "/CN=example.com" \
          -extensions san \
          -config <(echo '[req]'; echo 'distinguished_name=req';
          echo '[san]'; echo 'subjectAltName=DNS:example.com,DNS:www.example.net,IP:10.0.0.1')

```

### OpenJDK

* List all [JDK cacerts](https://seanjmullan.org/blog/2022/03/23/jdk18#pki)

```bash
# Since JDK 18, cacert migrated to Password-Less PKCS12(https://jdk.java.net/18/release-notes#JDK-8275252)
$ openssl pkcs12 -cacerts -chain -nokeys -nomacver -in "$JAVA_HOME/lib/security/cacerts" -passin pass: | grep -i "subject="
```



### Add Certs to IntelliJ Truststore

```bash
$ cacerts="$HOME/Library/Application Support/JetBrains/IntelliJIdea2022.1/ssl/cacerts"
$ keytool -list -keystore "$cacerts" -storetype pkcs12 -storepass changeit
$ keytool -importcert -trustcacerts -alias rootca -storetype PKCS12 -keystore $cacerts -storepass changeit -file "$HOME/Desktop/RootCA-SHA256.crt"
$ keytool -list -keystore "$cacerts" -storetype pkcs12 -storepass changeit
```



### Misc

* https://www.sslshopper.com/article-most-common-java-keytool-keystore-commands.html
* [Export Certificates and Private Key from a PKCS#12](https://www.ssl.com/how-to/export-certificates-private-key-from-pkcs12-file-with-openssl/)
* https://www.rapidsslonline.com/blog/simple-guide-java-keytool-keystore-commands/
