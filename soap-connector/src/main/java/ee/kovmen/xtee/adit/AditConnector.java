package ee.kovmen.xtee.adit;

import java.util.List;

import org.ow2.bonita.connector.core.ConnectorError;
import org.ow2.bonita.connector.core.ProcessConnector;
import org.springframework.oxm.xmlbeans.XmlBeansMarshaller;

import ee.finestmedia.xtee.client.adit.AmetlikudDokumendidXTeeServiceImpl;
import ee.kovmen.SoapConnector;
import ee.kovmen.xtee.consumer.CustomServiceTemplate;
import ee.kovmen.xtee.consumer.CustomXTeeConsumer;
import ee.webmedia.xtee.client.service.configuration.XTeeServiceConfiguration;
import ee.webmedia.xtee.client.service.configuration.provider.XTeeServiceConfigurationProvider;

public abstract class AditConnector extends ProcessConnector{
	private static AmetlikudDokumendidXTeeServiceImpl service;

	
	
	public AmetlikudDokumendidXTeeServiceImpl getService() throws Exception {
		if (service != null)
			return service;
		
		service = new AmetlikudDokumendidXTeeServiceImpl();
		
		CustomXTeeConsumer	xteeConsumer = new CustomXTeeConsumer();
		
		CustomServiceTemplate webServiceTemplate = new CustomServiceTemplate();

		XmlBeansMarshaller xmlBeansMarshaller = new org.springframework.oxm.xmlbeans.XmlBeansMarshaller();
		webServiceTemplate.setMarshaller(xmlBeansMarshaller);
		webServiceTemplate.setUnmarshaller(xmlBeansMarshaller);
		
		xteeConsumer.setWebServiceTemplate(webServiceTemplate);
		xteeConsumer.afterPropertiesSet();
		
		service.setConsumer(xteeConsumer);
		service.setSecurityServer(SoapConnector.getConfigValue("turvaserver_dvk", "http://10.100.113.109:80/cgi-bin/consumer_proxy"));
		service.setInstitution(SoapConnector.getConfigValue("asutus", "10648908"));
		service.setSystem("Kovmen");
		
		return service;
	}

}
