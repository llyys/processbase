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
import ee.webmedia.xtee.client.dhl.DhlXTeeService.SendDocumentsDokumentCallback;
import ee.webmedia.xtee.client.dhl.DhlXTeeService.SendDocumentsRequestCallback;
import ee.webmedia.xtee.client.dhl.DhlXTeeServiceImpl;
import ee.webmedia.xtee.client.dhl.types.ee.riik.schemas.dhl.AadressType;
import ee.webmedia.xtee.client.dhl.types.ee.riik.schemas.dhl.DokumentDocument;
import ee.webmedia.xtee.client.dhl.types.ee.riik.schemas.dhl.MetaxmlDocument.Metaxml;
import ee.webmedia.xtee.client.dhl.types.ee.riik.schemas.dhl.rkelLetter.Letter;
import ee.webmedia.xtee.client.dhl.types.ee.riik.schemas.dhl.rkelLetter.LetterType;
import ee.webmedia.xtee.client.dhl.types.ee.riik.schemas.dhl.rkelLetter.OrganisationType;
import ee.webmedia.xtee.client.dhl.types.ee.riik.schemas.dhl.rkelLetter.PartyType;
import ee.webmedia.xtee.client.dhl.types.ee.riik.xtee.dhl.producers.producer.dhl.SendDocumentsV2RequestType;
import ee.webmedia.xtee.client.service.configuration.XTeeServiceConfiguration;
import ee.webmedia.xtee.client.service.configuration.provider.XTeeServiceConfigurationProvider;

public class DvkConnector extends ProcessConnector{
	
	private org.ow2.bonita.facade.runtime.AttachmentInstance file;
	
	
	private String saatjaIsikukood;
	private String saatjaEmail;
	private String saatjaNimi;
	private String recipentCompanyName;
	private String recipentCompanyRegNo;
	private String senderCompanyName;
	private String senderCompanyRegNo;
	
	private String title;

	@Override
	protected void executeConnector() throws Exception {
		List<ContentToSend> content = new ArrayList<ContentToSend>();
		content.add(getContentToSend(file));
		sendDocument(content);
	}

	@Override
	protected List<ConnectorError> validateValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void sendDocument(Collection<ContentToSend> contentsToSend) throws Exception{
		
		//initialization
		DhlXTeeServiceImpl service = new DhlXTeeServiceImpl();

		CustomXTeeConsumer xteeConsumer = new CustomXTeeConsumer();

		CustomServiceTemplate webServiceTemplate = new CustomServiceTemplate();

		XmlBeansMarshaller xmlBeansMarshaller = new org.springframework.oxm.xmlbeans.XmlBeansMarshaller();
		webServiceTemplate.setMarshaller(xmlBeansMarshaller);
		webServiceTemplate.setUnmarshaller(xmlBeansMarshaller);

		xteeConsumer.setWebServiceTemplate(webServiceTemplate);
		xteeConsumer.afterPropertiesSet();

		service.setxTeeConsumer(xteeConsumer);
		service.setDatabase("dhl");

		service.setxTeeServiceConfigurationProvider(new XTeeServiceConfigurationProvider() {
			public XTeeServiceConfiguration createConfiguration(
					String database, String wsdldatabase, final String method,
					final String version) {
				return new XTeeServiceConfiguration() {

					public boolean useDeprecatedApi() {
						return false;
					}

					public String getWsdlDatabase() {
						return "dhl";
					}

					public String getVersion() {
						return version == null ? "v1" : version;
					}

					public String getSecurityServer() {
						return SoapConnector
								.getConfigValue("turvaserver_dvk", "http://10.100.113.109:80/cgi-bin/consumer_proxy");
					}

					public String getMethod() {
						return method;
					}

					public String getInstitution() {
						return SoapConnector.getConfigValue("asutus", "10648908");
					}

					public String getIdCode() {
						return "EE" + getSaatjaIsikukood();
					}

					public boolean getForceDatabaseNamespace() {
						return false;
					}

					public String getFile() {
						return null;
					}

					public String getDatabase() {
						return "dhl";
					}

					public void forceDatabaseNamespace() {
					}
				};
			}
		});

		// set up sender info
		final AadressType sender = AadressType.Factory.newInstance();
		if (getSenderCompanyName() == null) {
			sender.setAsutuseNimi(SoapConnector.getConfigValue("asutus_nimi",
					"Smartlink OÜ"));
		} else {
			sender.setAsutuseNimi(getSenderCompanyName());
		}

		if (getSenderCompanyRegNo() == null) {
			sender.setRegnr(SoapConnector.getConfigValue("asutus", "10648908"));
		} else {
			sender.setRegnr(getSenderCompanyRegNo());
		}

		sender.setIsikukood(getSaatjaIsikukood());
		sender.setEpost(getSaatjaEmail());
		sender.setNimi(getSaatjaNimi());

		AadressType recipent = AadressType.Factory.newInstance();
		recipent.setAsutuseNimi(getRecipentCompanyName());
		recipent.setRegnr(getRecipentCompanyRegNo());
		AadressType[] recipients = { recipent };
		
		//Set<String> sendDocuments = service.sendDocuments(contentsToSend, recipients, sender);
		
		Set<String> sendDocuments = service.sendDocuments(contentsToSend, recipients, sender, new SendDocumentsDokumentCallback() {
			
			public void doWithDocument(DokumentDocument dokumentDocument) {
				
				Metaxml metaxml = dokumentDocument.getDokument().getMetaxml();

				Letter letter = Letter.Factory.newInstance();
				
				LetterType letterType = LetterType.Factory.newInstance();
				letterType.setSenderIdentifier(sender.getAsutuseNimi());
				letterType.setTitle(title);
				
				letter.setLetterMetaData(letterType);
				
				OrganisationType organisationType = OrganisationType.Factory.newInstance();
				organisationType.setOrganisationName(sender.getAsutuseNimi());
				
				PartyType partyType = PartyType.Factory.newInstance();
				partyType.setOrganisation(organisationType);
				
				letter.setAuthor(partyType);
				
				metaxml.set(letter);
				
				System.out.println("doWithDocument dokumentDocument=" + dokumentDocument);
			}
		}, new SendDocumentsRequestCallback() {
			
			public void doWithRequest(SendDocumentsV2RequestType request) {
				System.out.println("doWithRequest request=" + request);
			}
		});
		
		
		System.out.println("result:" + sendDocuments);
		
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

	public void setFile(org.ow2.bonita.facade.runtime.AttachmentInstance file) {
		this.file = file;
	}

	public org.ow2.bonita.facade.runtime.AttachmentInstance getFile() {
		return file;
	}

	public void setSenderCompanyName(String senderCompanyName) {
		this.senderCompanyName = senderCompanyName;
	}

	public String getSenderCompanyName() {
		return senderCompanyName;
	}

	public void setSenderCompanyRegNo(String senderCompanyRegNo) {
		this.senderCompanyRegNo = senderCompanyRegNo;
	}

	public String getSenderCompanyRegNo() {
		return senderCompanyRegNo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
