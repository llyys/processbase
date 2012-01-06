package ee.kovmen.xtee;

import org.ow2.bonita.util.BonitaConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

import ee.kovmen.xtee.adit.AditGetUserInfo;
import ee.kovmen.xtee.result.AditUserInfo;

public class AditConnectorTest {
  @Test
  public void CanSendDocumentToAditTestEnvironment() {
	  
	  System.setProperty(BonitaConstants.HOME, "c:/dev_env/bonita");
	  
	  AditGetUserInfo userInfo=new AditGetUserInfo();
	  try {
		AditUserInfo info = userInfo.getUserInfo("37903236510");
		Assert.assertNotNull(info, "adnmeid ei leitud");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		Assert.fail("getUserInfo", e);
	}
  }
}
