package com.davidwlee.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SignatureServiceImpl implements SignatureService {

    public String sign(String s) throws Exception{
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        Reference ref = fac.newReference("",
                fac.newDigestMethod(DigestMethod.SHA1, null),
                Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                null,
                null);

        SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                        (C14NMethodParameterSpec) null),
                        fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                        Collections.singletonList(ref));

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("target/classes/keystore.jks"), "password".toCharArray());
        KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry("selfsigned", new KeyStore.PasswordProtection("password".toCharArray()));
        X509Certificate cert = (X509Certificate) keyEntry.getCertificate();

        KeyInfoFactory kif = fac.getKeyInfoFactory();
        List x509Content = new ArrayList();
        x509Content.add(cert.getSubjectX500Principal().getName());
        x509Content.add(cert);
        X509Data xd = kif.newX509Data(x509Content);
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(s.getBytes()));

        DOMSignContext dsc = new DOMSignContext(keyEntry.getPrivateKey(), doc.getDocumentElement());

        XMLSignature signature = fac.newXMLSignature(si, ki);
        signature.sign(dsc);

        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);

        return writer.toString();
    }
}
