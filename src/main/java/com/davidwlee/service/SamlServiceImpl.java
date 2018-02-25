package com.davidwlee.service;

import com.davidwlee.model.SamlConfig;
import oasis.names.tc.saml._2_0.assertion.*;
import oasis.names.tc.saml._2_0.protocol.ResponseType;
import oasis.names.tc.saml._2_0.protocol.StatusCodeType;
import oasis.names.tc.saml._2_0.protocol.StatusType;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;

@Service
public class SamlServiceImpl implements SamlService {

    public SamlConfig getDefaultSamlConfig(){
        SamlConfig config = new SamlConfig();
        Properties prop = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("target/classes/samlConfigDefault.properties");
            prop.load(inputStream);
            config.setSamlVersion(prop.getProperty("samlVersion"));
            config.setIssuer(prop.getProperty("issuer"));
            config.setSubject(prop.getProperty("subject"));
            config.setAcsUrl(prop.getProperty("acsUrl"));
            config.setRecipient(prop.getProperty("recipient"));
            config.setAudience(prop.getProperty("audience"));
            config.setRelayState(prop.getProperty("relayState"));
            config.setSsoStartPage(prop.getProperty("ssoStartPage"));
        }
        catch(IOException ex){
            ex.printStackTrace();
        } finally{
            try {
                inputStream.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        return config;
    }

    public String getSamlResponse(SamlConfig config){
        XMLGregorianCalendar now = getXMLGregorianCalendarNow();
        XMLGregorianCalendar expirationDate =  getXMLGregorianCalendarExpirationDate();

        ResponseType response = new ResponseType();
        response.setDestination(config.getAcsUrl());
        response.setID(java.util.UUID.randomUUID().toString());
        response.setIssueInstant(now);
        response.setVersion(config.getSamlVersion());

        NameIDType nameIdType = new NameIDType();
        nameIdType.setValue(config.getIssuer());
        response.setIssuer(nameIdType);

        StatusType statusType = new StatusType();
        StatusCodeType statusCodeType = new StatusCodeType();
        statusCodeType.setValue("urn:oasis:names:tc:SAML:2.0:status:Success");
        statusType.setStatusCode(statusCodeType);
        response.setStatus(statusType);

        AssertionType assertionType = new AssertionType();
        assertionType.setID(java.util.UUID.randomUUID().toString());
        assertionType.setIssueInstant(now);
        assertionType.setIssuer(nameIdType);
        assertionType.setVersion(config.getSamlVersion());

        SubjectType subjectType = new SubjectType();
        assertionType.setSubject(subjectType);
        NameIDType nameIDType = new NameIDType();
        nameIDType.setFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
        nameIDType.setValue(config.getSubject());
        subjectType.getContent().add(new JAXBElement(new QName("saml:NameID"), NameIDType.class, nameIDType));

        SubjectConfirmationType subjectConfirmationType = new SubjectConfirmationType();
        subjectConfirmationType.setMethod("urn:oasis:names:tc:SAML:2.0:cm:bearer");
        SubjectConfirmationDataType subjectConfirmationDataType = new SubjectConfirmationDataType();

        subjectConfirmationDataType.setNotOnOrAfter(expirationDate);
        subjectConfirmationType.setSubjectConfirmationData(subjectConfirmationDataType);
        subjectConfirmationDataType.setRecipient(config.getRecipient());
        subjectType.getContent().add(new JAXBElement(new QName("saml:SubjectConfirmation"), SubjectConfirmationType.class, subjectConfirmationType));
        assertionType.setSubject(subjectType);

        ConditionsType conditionsType = new ConditionsType();
        conditionsType.setNotBefore(now);
        conditionsType.setNotOnOrAfter(expirationDate);

        AudienceRestrictionType audienceRestrictionType = new AudienceRestrictionType();
        audienceRestrictionType.getAudience().add(config.getAudience());
        conditionsType.getConditionOrAudienceRestrictionOrOneTimeUse().add(audienceRestrictionType);
        assertionType.setConditions(conditionsType);

        AuthnStatementType authnStatementType = new AuthnStatementType();
        authnStatementType.setAuthnInstant(now);
        AuthnContextType authnContextType = new AuthnContextType();
        authnContextType.getContent().add(new JAXBElement(new QName("saml:AuthnContextClassRef"), String.class, "urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified"));
        authnStatementType.setAuthnContext(authnContextType);
        assertionType.getStatementOrAuthnStatementOrAuthzDecisionStatement().add(authnStatementType);

        AttributeStatementType attributeStatementType = new AttributeStatementType();
        AttributeType attributeType = new AttributeType();
        attributeType.setName("ssoStartPage");
        attributeType.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified");
        attributeType.getAttributeValue().add(config.getSsoStartPage());
        attributeStatementType.getAttributeOrEncryptedAttribute().add(attributeType);

        AttributeType attributeType2 = new AttributeType();
        attributeType2.setName("logoutURL");
        attributeType2.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified");
        attributeType2.getAttributeValue().add("");
        attributeStatementType.getAttributeOrEncryptedAttribute().add(attributeType2);
        assertionType.getStatementOrAuthnStatementOrAuthzDecisionStatement().add(attributeStatementType);

        response.getAssertionOrEncryptedAssertion().add(assertionType);

        String res = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ResponseType.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            StringWriter sw = new StringWriter();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(response, sw);

            res = sw.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return res;
    }

    private XMLGregorianCalendar getXMLGregorianCalendarNow() {
        XMLGregorianCalendar now = null;
        try {
            GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
        }
        catch(DatatypeConfigurationException ex){
        }
        return now;
    }

    private XMLGregorianCalendar getXMLGregorianCalendarExpirationDate() {
        XMLGregorianCalendar expirationDate = null;
        try {
            GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            gregorianCalendar.add(Calendar.MINUTE, 2);
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            expirationDate = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
        }
        catch(DatatypeConfigurationException ex){
        }
        return expirationDate;
    }
}
