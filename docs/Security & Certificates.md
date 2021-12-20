# Security & Certificates

------

[TOC]

### OpenSSL & Keytool

------

To create a RootCA `PKCS#12` trust-store of the given URL

```bash
# Extract the server certificates.
$ echo -n | openssl s_client -showcerts -connect google.com:443 | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > globalsign.crt

# Create/Add trust store
$ keytool -importcert -trustcacerts -alias globalsign-rootca -storetype PKCS12 -keystore globalsign-rootca.p12 -storepass changeit -file globalsign.crt

# Add intermediate certs (Optional)
$ keytool -importcert -keystore globalsign-rootca.p12 -alias CA-intermediate -storepass changeit -file CA-intermediate.cer

# Show PKCS#12 info.
$ openssl pkcs12 -info -password pass:changeit -in globalsign-rootca.p12
$ keytool -list -keystore globalsign-rootca.p12 --storetype pkcs12 -storepass changeit -v

# Show certs expiry
$ echo | openssl s_client -servername google.com -connect google.com:443 2>/dev/null | openssl x509 -noout -dates
$ curl -vvI https://google.com 2>&1 | grep -i date

# Create a new PKCS#12 store from certs
$ openssl pkcs12 -export -chain -out keystore.p12 -inkey private.key -password pass:test123 \
                  -in client.crt -certfile client.crt -CAfile cacert.crt -name client-key \
                  -caname root-ca

# Convert PKCS#12 to PEM (don't encrypt private keys)
$ openssl pkcs12 -in keystore.p12 -out keystore.pem -nodes

# Export to PKCS#12 with new password
$ openssl pkcs12 -export -in keystore.pem -nodes -out keystore.p12

# Verify if a Private Key Matches a Certificate
$ openssl x509 -noout -modulus -in cert.pem | openssl md5
$ openssl rsa  -noout -modulus -in cert.key | openssl md5
```

### Add Certs to IntelliJ Truststore

------

```bash
$ cacerts="$HOME/Library/Application Support/JetBrains/GoLand2020.2/ssl/cacerts"
$ keytool -list -keystore "$cacerts" -storetype pkcs12 -storepass changeit
$ keytool -importcert -trustcacerts -alias rootca -storetype PKCS12 -keystore $cacerts -storepass changeit -file "$HOME/Desktop/RootCA-SHA256.crt"
$ keytool -list -keystore "$cacerts" -storetype pkcs12 -storepass changeit

$ cacerts="$HOME/Library/Application Support/JetBrains/IntelliJIdea2020.2/ssl/cacerts"
$ keytool -list -keystore "$cacerts" -storetype pkcs12 -storepass changeit
$ keytool -importcert -trustcacerts -alias rootca -storetype PKCS12 -keystore $cacerts -storepass changeit -file "$HOME/Desktop/RootCA-SHA256.crt"
$ keytool -list -keystore "$cacerts" -storetype pkcs12 -storepass changeit
```
