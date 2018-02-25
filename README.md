# saml-idp-initiated-sso

### Objective
This project demonstrates how SAML2.0 works via a sample integration with Salesforce SSO. Watch how each field is populated into SAML response and figure out that they mean.

### How to set up the environment.
#### Service Provider side (Salesforce)
1. Set up SSO in Salesforce side.
   1. Sign up for Salesforce developer portal(developer.salesforce.com).
2. Log in and search for "Single Sign-On Settings".
3. Click "SAML Enabled" checkbox to enable SSO.
4. Add a new SAML Single Sign-On Settings.
5. Enter all information
   1. For "Name" and "API Name", decide and enter this setting name and API name.
   2. For "Issuer", enter the IdP EntityID. It can be URL of your website or any unique name
   3. For "Entity ID", enter the SP EntityID, which is "https://saml.salesforce.com"
   4. For "Identity Provider Certificate", upload "certificate.cer" file included in the project.
   5. For "Assertion Decryption Certificat", select "RSA-SHA256".
   6. For "SAML Identity Type, select "Assertion contains the User's Salesforce username"
   7. For "SAML Identity Location", select "Identity is in the nameIdentifier element of the Subject statement"
   8. For "Service Provider Initiated Request Binding", select "HTTP POST".

#### Identity Provider side (Our project in local environment)
1. Download Eclipse or Intellij.
2. Change samleConfigDefault.properties file if you want.
3. Run the main method in App class.
4. Go to localhost and play with it!

### Implementation Details
#### Command for generating xsd file
* If you want to change the package name, you need to download the import files and change the references accordingly.
```
xjc -d . https://docs.oasis-open.org/security/saml/v2.0/saml-schema-protocol-2.0.xsd
xjc -d . https://docs.oasis-open.org/security/saml/v2.0/saml-schema-assertion-2.0.xsd
xjc -d . https://docs.oasis-open.org/security/saml/v2.0/saml-schema-metadata-2.0.xsd
```

#### In generated files, you need to add code in 3 places.
1. In ResponseType.java,
   1. Add @XmlRootElement(name = "Response") on top of public class ResponseType
   2. Import class for XmlRootElement.
2. In assertion/package-info.java, protocol/package-info.java,
   1. Fix the annotation to the following.
        @javax.xml.bind.annotation.XmlSchema(namespace = "urn:oasis:names:tc:SAML:2.0:assertion",
        elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
        xmlns = {@XmlNs(prefix="saml", namespaceURI="urn:oasis:names:tc:SAML:2.0:assertion")})
   2. Import class for XmlNs.

#### How to generate selfsigned certificate and private key and store it in keystore.
* Here, alias is selfsigned, keystore name is keystore.jks RSA, password is password
* For details, refer to https://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html
```
keytool -genkeypair -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass password -validity 360 -keysize 2048
```

#### Convert keystore.jks to pkcs12
* Alternatively, you can add "-storetype PKCS12" in the previous command to create pkcs12 directly.
```
keytool -importkeystore -srckeystore keystore.jks -destkeystore keystore.jks -deststoretype pkcs12
```

#### Export certificate out of keystore
```
keytool -export -storepass password -alias selfsigned -keystore keystore.jks -file certificate.cer
```