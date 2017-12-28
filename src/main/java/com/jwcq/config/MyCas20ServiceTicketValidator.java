package com.jwcq.config;

/**
 * Created by luotuo on 17-6-19.
 */
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.authentication.AttributePrincipalImpl;
import org.jasig.cas.client.proxy.Cas20ProxyRetriever;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.proxy.ProxyRetriever;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.XmlUtils;
import org.jasig.cas.client.validation.AbstractCasProtocolUrlBasedTicketValidator;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.AssertionImpl;
import org.jasig.cas.client.validation.TicketValidationException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class MyCas20ServiceTicketValidator extends AbstractCasProtocolUrlBasedTicketValidator {
    private String proxyCallbackUrl;
    private ProxyGrantingTicketStorage proxyGrantingTicketStorage;
    private ProxyRetriever proxyRetriever;

    public MyCas20ServiceTicketValidator(String casServerUrlPrefix) {
        super(casServerUrlPrefix);
        this.proxyRetriever = new Cas20ProxyRetriever(casServerUrlPrefix, this.getEncoding(), this.getURLConnectionFactory());
    }

    protected final void populateUrlAttributeMap(Map<String, String> urlParameters) {
        urlParameters.put("pgtUrl", this.proxyCallbackUrl);
    }

    protected String getUrlSuffix() {
        return "serviceValidate";
    }

    protected final Assertion parseResponseFromServer(String response) throws TicketValidationException {
        String error = XmlUtils.getTextForElement(response, "authenticationFailure");
        if(CommonUtils.isNotBlank(error)) {
            throw new TicketValidationException(error);
        } else {
            String principal = XmlUtils.getTextForElement(response, "user");
            String proxyGrantingTicketIou = XmlUtils.getTextForElement(response, "proxyGrantingTicket");
            String proxyGrantingTicket;
            if(!CommonUtils.isBlank(proxyGrantingTicketIou) && this.proxyGrantingTicketStorage != null) {
                proxyGrantingTicket = this.proxyGrantingTicketStorage.retrieve(proxyGrantingTicketIou);
            } else {
                proxyGrantingTicket = null;
            }

            if(CommonUtils.isEmpty(principal)) {
                throw new TicketValidationException("No principal was found in the response from the CAS server.");
            } else {
                Map<String, Object> attributes = this.extractCustomAttributes(response);
                AssertionImpl assertion;
                if(CommonUtils.isNotBlank(proxyGrantingTicket)) {
                    AttributePrincipal attributePrincipal = new AttributePrincipalImpl(principal, attributes, proxyGrantingTicket, this.proxyRetriever);
                    assertion = new AssertionImpl(attributePrincipal);
                } else {
                    assertion = new AssertionImpl(new AttributePrincipalImpl(principal, attributes));
                }

                this.customParseResponse(response, assertion);
                return assertion;
            }
        }
    }

    protected Map<String, Object> extractCustomAttributes(String xml) {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(false);

        try {
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            MyCas20ServiceTicketValidator.CustomAttributeHandler handler = new MyCas20ServiceTicketValidator.CustomAttributeHandler();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(new StringReader(xml)));
            return handler.getAttributes();
        } catch (Exception var6) {
            this.logger.error(var6.getMessage(), var6);
            return Collections.emptyMap();
        }
    }

    protected void customParseResponse(String response, Assertion assertion) throws TicketValidationException {
    }

    public final void setProxyCallbackUrl(String proxyCallbackUrl) {
        this.proxyCallbackUrl = proxyCallbackUrl;
    }

    public final void setProxyGrantingTicketStorage(ProxyGrantingTicketStorage proxyGrantingTicketStorage) {
        this.proxyGrantingTicketStorage = proxyGrantingTicketStorage;
    }

    public final void setProxyRetriever(ProxyRetriever proxyRetriever) {
        this.proxyRetriever = proxyRetriever;
    }

    protected final String getProxyCallbackUrl() {
        return this.proxyCallbackUrl;
    }

    protected final ProxyGrantingTicketStorage getProxyGrantingTicketStorage() {
        return this.proxyGrantingTicketStorage;
    }

    protected final ProxyRetriever getProxyRetriever() {
        return this.proxyRetriever;
    }

    private class CustomAttributeHandler extends DefaultHandler {
        private Map<String, Object> attributes;
        private boolean foundAttributes;
        private String currentAttribute;
        private StringBuilder value;

        private CustomAttributeHandler() {
        }

        public void startDocument() throws SAXException {
            this.attributes = new HashMap();
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) throws SAXException {
            if("attributes".equals(localName)) {
                this.foundAttributes = true;
            } else if(this.foundAttributes) {
                this.value = new StringBuilder();
                this.currentAttribute = localName;
            }

        }

        public void characters(char[] chars, int start, int length) throws SAXException {
            if(this.currentAttribute != null) {
                this.value.append(chars, start, length);
            }

        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            if("attributes".equals(localName)) {
                this.foundAttributes = false;
                this.currentAttribute = null;
            } else if(this.foundAttributes) {
                Object o = this.attributes.get(this.currentAttribute);
                if(o == null) {
                    this.attributes.put(this.currentAttribute, this.value.toString());
                } else {
                    Object items;
                    if(o instanceof List) {
                        items = (List)o;
                    } else {
                        items = new LinkedList();
                        ((List)items).add(o);
                        this.attributes.put(this.currentAttribute, items);
                    }

                    ((List)items).add(this.value.toString());
                }
            }

        }

        public Map<String, Object> getAttributes() {
            return this.attributes;
        }
    }
}
