package ee.kovmen.xtee.adit;

import org.ow2.bonita.connector.core.ProcessConnector;
import org.springframework.oxm.xmlbeans.XmlBeansMarshaller;

import ee.kovmen.SoapConnector;
import ee.kovmen.xtee.adit.AmetlikudDokumendidXTeeServiceImpl;
import ee.kovmen.xtee.consumer.CustomServiceTemplate;
import ee.kovmen.xtee.consumer.CustomXTeeConsumer;

public abstract class AbstractAditConnector extends ProcessConnector {


	private static AmetlikudDokumendidXTeeServiceImpl service;

	public AmetlikudDokumendidXTeeServiceImpl getService() throws Exception {
		if (service != null)
			return service;

		service = new AmetlikudDokumendidXTeeServiceImpl();

		CustomXTeeConsumer xteeConsumer = new CustomXTeeConsumer();

		CustomServiceTemplate webServiceTemplate = new CustomServiceTemplate();

		XmlBeansMarshaller xmlBeansMarshaller = new org.springframework.oxm.xmlbeans.XmlBeansMarshaller();
		webServiceTemplate.setMarshaller(xmlBeansMarshaller);
		webServiceTemplate.setUnmarshaller(xmlBeansMarshaller);

		xteeConsumer.setWebServiceTemplate(webServiceTemplate);
		xteeConsumer.afterPropertiesSet();

		service.setConsumer(xteeConsumer);
		service.setSecurityServer(SoapConnector.getConfigValue(
				"turvaserver_adit",
				"http://10.100.112.109:80/cgi-bin/consumer_proxy"));
		service.setInstitution(SoapConnector.getConfigValue("asutus_adit",
				"10648908"));
		service.setSystem("KOVMEN");

		return service;
	}

}
