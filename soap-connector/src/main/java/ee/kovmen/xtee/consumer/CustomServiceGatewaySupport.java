package ee.kovmen.xtee.consumer;
/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.util.Assert;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.destination.DestinationProvider;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.transport.WebServiceMessageSender;

/**
 * Convenient super class for application classes that need Web service access.
 * <p/>
 * Requires a {@link WebServiceMessageFactory} or a {@link WebServiceTemplate} instance to be set. It will create its
 * own <code>WebServiceTemplate</code> if <code>WebServiceMessageFactory</code> is passed in.
 * <p/>
 * In addition to the message factory property, this gateway offers {@link Marshaller} and {@link Unmarshaller}
 * properties. Setting these is required when the {@link WebServiceTemplate#marshalSendAndReceive(Object) marshalling
 * methods} of the template are to be used.
 * <p/>
 * Note that when {@link #setWebServiceTemplate(WebServiceTemplate) injecting a <code>WebServiceTemplate</code>}
 * directly, the convenience setters ({@link #setMarshaller(Marshaller)}, {@link #setUnmarshaller(Unmarshaller)}, {@link
 * #setMessageSender(WebServiceMessageSender)}, {@link #setMessageSenders(WebServiceMessageSender[])}, and {@link
 * #setDefaultUri(String)}) should not be used on this class, but on the template directly.
 *
 * @author Arjen Poutsma
 * @see #setMessageFactory(WebServiceMessageFactory)
 * @see WebServiceTemplate
 * @see #setMarshaller(Marshaller)
 * @since 1.0.0
 */
public abstract class CustomServiceGatewaySupport implements InitializingBean {

    /** Logger available to subclasses. */
    //protected final Log logger = LogFactory.getLog(getClass());

    private CustomServiceTemplate webServiceTemplate;

    /**
     * Creates a new instance of the <code>WebServiceGatewaySupport</code> class, with a default
     * <code>WebServiceTemplate</code>.
     */
    protected CustomServiceGatewaySupport() {
        webServiceTemplate = new CustomServiceTemplate();
    }

    /**
     * Creates a new <code>WebServiceGatewaySupport</code> instance based on the given message factory.
     *
     * @param messageFactory the message factory to use
     */
    protected CustomServiceGatewaySupport(WebServiceMessageFactory messageFactory) {
        webServiceTemplate = new CustomServiceTemplate(messageFactory);
    }

    /** Returns the <code>WebServiceMessageFactory</code> used by the gateway. */
    public final WebServiceMessageFactory getMessageFactory() {
        return webServiceTemplate.getMessageFactory();
    }

    /** Set the <code>WebServiceMessageFactory</code> to be used by the gateway. */
    public final void setMessageFactory(WebServiceMessageFactory messageFactory) {
        webServiceTemplate.setMessageFactory(messageFactory);
    }

    /** Returns the default URI used by the gateway. */
    public final String getDefaultUri() {
        return webServiceTemplate.getDefaultUri();
    }

    /** Sets the default URI used by the gateway. */
    public final void setDefaultUri(String uri) {
        webServiceTemplate.setDefaultUri(uri);
    }

    /** Returns the destination provider used by the gateway. */
    public final DestinationProvider getDestinationProvider() {
        return webServiceTemplate.getDestinationProvider();
    }

    /** Set the destination provider URI used by the gateway. */
    public final void setDestinationProvider(DestinationProvider destinationProvider) {
        webServiceTemplate.setDestinationProvider(destinationProvider);
    }

    /** Sets a single <code>WebServiceMessageSender</code> to be used by the gateway. */
    public final void setMessageSender(WebServiceMessageSender messageSender) {
        webServiceTemplate.setMessageSender(messageSender);
    }

    /** Returns the <code>WebServiceMessageSender</code>s used by the gateway. */
    public final WebServiceMessageSender[] getMessageSenders() {
        return webServiceTemplate.getMessageSenders();
    }

    /** Sets multiple <code>WebServiceMessageSender</code> to be used by the gateway. */
    public final void setMessageSenders(WebServiceMessageSender[] messageSenders) {
        webServiceTemplate.setMessageSenders(messageSenders);
    }

    /** Returns the <code>WebServiceTemplate</code> for the gateway. */
    public final CustomServiceTemplate getWebServiceTemplate() {
        return webServiceTemplate;
    }

    /**
     * Sets the <code>WebServiceTemplate</code> to be used by the gateway.
     * <p/>
     * When using this property, the convenience setters ({@link #setMarshaller(Marshaller)}, {@link
     * #setUnmarshaller(Unmarshaller)}, {@link #setMessageSender(WebServiceMessageSender)}, {@link
     * #setMessageSenders(WebServiceMessageSender[])}, and {@link #setDefaultUri(String)}) should not be set on this
     * class, but on the template directly.
     */
    public final void setWebServiceTemplate(CustomServiceTemplate webServiceTemplate) {
        Assert.notNull(webServiceTemplate, "'webServiceTemplate' must not be null");
        this.webServiceTemplate = webServiceTemplate;
    }

    /** Returns the <code>Marshaller</code> used by the gateway. */
    public final Marshaller getMarshaller() {
        return webServiceTemplate.getMarshaller();
    }

    /**
     * Sets the <code>Marshaller</code> used by the gateway. Setting this property is only required if the marshalling
     * functionality of <code>WebServiceTemplate</code> is to be used.
     *
     * @see WebServiceTemplate#marshalSendAndReceive
     */
    public final void setMarshaller(Marshaller marshaller) {
        webServiceTemplate.setMarshaller(marshaller);
    }

    /** Returns the <code>Unmarshaller</code> used by the gateway. */
    public final Unmarshaller getUnmarshaller() {
        return webServiceTemplate.getUnmarshaller();
    }

    /**
     * Sets the <code>Unmarshaller</code> used by the gateway. Setting this property is only required if the marshalling
     * functionality of <code>WebServiceTemplate</code> is to be used.
     *
     * @see WebServiceTemplate#marshalSendAndReceive
     */
    public final void setUnmarshaller(Unmarshaller unmarshaller) {
        webServiceTemplate.setUnmarshaller(unmarshaller);
    }

    /** Returns the <code>ClientInterceptors</code> used by the template. */
    public final ClientInterceptor[] getInterceptors() {
        return webServiceTemplate.getInterceptors();
    }

    /** Sets the <code>ClientInterceptors</code> used by the gateway. */
    public final void setInterceptors(ClientInterceptor[] interceptors) {
        webServiceTemplate.setInterceptors(interceptors);
    }

    public final void afterPropertiesSet() throws Exception {
        webServiceTemplate.afterPropertiesSet();
        initGateway();
    }

    /**
     * Subclasses can override this for custom initialization behavior. Gets called after population of this instance's
     * bean properties.
     *
     * @throws java.lang.Exception if initialization fails
     */
    protected void initGateway() throws Exception {
    }

}
