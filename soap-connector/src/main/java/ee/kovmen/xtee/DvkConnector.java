package ee.kovmen.xtee;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.ow2.bonita.connector.core.ConnectorError;
import org.ow2.bonita.connector.core.ProcessConnector;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.exception.DocumentNotFoundException;
import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.util.AccessorUtil;
import org.springframework.oxm.xmlbeans.XmlBeansMarshaller;

import ee.kovmen.SoapConnector;
import ee.kovmen.xtee.consumer.CustomServiceTemplate;
import ee.kovmen.xtee.consumer.CustomXTeeConsumer;
import ee.webmedia.xtee.client.dhl.DhlXTeeService.ContentToSend;
import ee.webmedia.xtee.client.dhl.DhlXTeeServiceImpl;
import ee.webmedia.xtee.client.dhl.types.ee.riik.schemas.dhl.AadressType;
import ee.webmedia.xtee.client.service.configuration.XTeeServiceConfiguration;
import ee.webmedia.xtee.client.service.configuration.provider.XTeeServiceConfigurationProvider;

public class DvkConnector extends ProcessConnector{

	private String saatjaIsikukood;
	private String saatjaEmail;
	private String saatjaNimi;
	private String recipentCompanyName;
	private String recipentCompanyRegNo;



	@Override
	protected void executeConnector() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected List<ConnectorError> validateValues() {
		// TODO Auto-generated method stub
		return null;
	}
	

	
	public void SendDocument(Collection<ContentToSend> contentsToSend) throws Exception{
		
		//initialization
		DhlXTeeServiceImpl service=new DhlXTeeServiceImpl();
		
		CustomXTeeConsumer	xteeConsumer = new CustomXTeeConsumer();
		
		CustomServiceTemplate webServiceTemplate = new CustomServiceTemplate();

		XmlBeansMarshaller xmlBeansMarshaller = new org.springframework.oxm.xmlbeans.XmlBeansMarshaller();
		webServiceTemplate.setMarshaller(xmlBeansMarshaller);
		webServiceTemplate.setUnmarshaller(xmlBeansMarshaller);
		
		xteeConsumer.setWebServiceTemplate(webServiceTemplate);
		xteeConsumer.afterPropertiesSet();
		
		service.setxTeeConsumer(xteeConsumer);
		service.setDatabase("dhl");

		service.setxTeeServiceConfigurationProvider(new XTeeServiceConfigurationProvider() {			
			public XTeeServiceConfiguration createConfiguration(String database, String wsdldatabase, final String method, final String version) {
				return new XTeeServiceConfiguration() {
					
					public boolean useDeprecatedApi() {return false;}					
					public String getWsdlDatabase() {return "dhl";}
					
					public String getVersion() {
						return version == null ? "v1" : version;
					}
					
					public String getSecurityServer() {
						return SoapConnector.getConfigValue("turvaserver_dvk", "http://10.100.113.109:80/cgi-bin/consumer_proxy");
					}
					
					public String getMethod() {
						return method;
					}
					
					public String getInstitution() {
						return SoapConnector.getConfigValue("asutus", "10648908");
					}
					
					public String getIdCode() {
						return "EE"+getSaatjaIsikukood();
					}
					
					public boolean getForceDatabaseNamespace() {return false;}					
					public String getFile() {return null;}					
					public String getDatabase() {return "dhl";}					
					public void forceDatabaseNamespace() {}
				};
			}
		});

		// set up sender info
		AadressType sender=AadressType.Factory.newInstance();
		sender.setAsutuseNimi(SoapConnector.getConfigValue("asutus_nimi", "Smartlink OÜ"));
		sender.setRegnr(SoapConnector.getConfigValue("asutus", "10648908"));
		
		sender.setIsikukood(getSaatjaIsikukood());
		sender.setEpost(getSaatjaEmail());
		sender.setNimi(getSaatjaNimi());	
		
		AadressType recipent=AadressType.Factory.newInstance();
		recipent.setAsutuseNimi(getRecipentCompanyName());
		recipent.setRegnr(getRecipentCompanyRegNo());
		AadressType[] recipients =  {recipent};
		
		// TODO Auto-generated method stub
		Set<String> sendDocuments = service.sendDocuments(contentsToSend, recipients, sender);
	}
	
	public ContentToSend getContentToSend(AttachmentInstance document) throws DocumentNotFoundException{
		
		ContentToSend contentToSend = new ContentToSend();
		contentToSend.setFileName(document.getFileName());
		contentToSend.setInputStream(getAttachmentInputStream(document));
		QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
		
		org.ow2.bonita.facade.runtime.Document bdoc = queryRuntimeAPI.getDocument(document.getUUID());
		
		contentToSend.setMimeType(bdoc.getContentMimeType());
		return contentToSend;
	}
	
	public InputStream getAttachmentInputStream(AttachmentInstance attachment) throws DocumentNotFoundException {
		QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
		
		byte[] content=queryRuntimeAPI.getDocumentContent(attachment.getUUID());
		ByteArrayInputStream bais=new ByteArrayInputStream(content);		  
		return bais;
		
	}

	public void setSaatjaIsikukood(String saatjaIsikukood) {
		this.saatjaIsikukood = saatjaIsikukood;
	}

	public String getSaatjaIsikukood() {
		return saatjaIsikukood;
	}

	public void setSaatjaEmail(String saatjaEmail) {
		this.saatjaEmail = saatjaEmail;
	}

	public String getSaatjaEmail() {
		return saatjaEmail;
	}

	public void setSaatjaNimi(String saatjaNimi) {
		this.saatjaNimi = saatjaNimi;
	}

	public String getSaatjaNimi() {
		return saatjaNimi;
	}

	public void setRecipentCompanyName(String recipentCompanyName) {
		this.recipentCompanyName = recipentCompanyName;
	}

	public String getRecipentCompanyName() {
		return recipentCompanyName;
	}

	public void setRecipentCompanyRegNo(String recipentCompanyRegNo) {
		this.recipentCompanyRegNo = recipentCompanyRegNo;
	}

	public String getRecipentCompanyRegNo() {
		return recipentCompanyRegNo;
	}


}
