package ee.kovmen.xtee;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.ow2.bonita.util.BonitaConstants;
import org.testng.annotations.Test;

import ee.webmedia.xtee.client.dhl.DhlXTeeService.ContentToSend;

public class DvkConnectorTest {
  @Test
  public void CanSendDocumentToTestDVKserver() {
	  
	  System.setProperty(BonitaConstants.HOME, "c:/dev_env/bonita");
	  
	  DvkConnector dvkConnector = new DvkConnector();
	  
	  InputStream fileStream = getClass().getResourceAsStream("/KoolitoiduToetuseTaotlus.rtf");
	  
	  Collection<ContentToSend> contentsToSend = new ArrayList<ContentToSend>();	  
	  ContentToSend doc=new ContentToSend();
	  doc.setFileName("KoolitoiduToetuseTaotlus.rtf");
	  doc.setMimeType("application/rtf");
	  doc.setInputStream(fileStream);
	  contentsToSend.add(doc);
	  
	  dvkConnector.setRecipentCompanyName("INTERINX OÜ");
	  dvkConnector.setRecipentCompanyRegNo("10425769");
	  
	  try {
		dvkConnector.SendDocument(contentsToSend);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
