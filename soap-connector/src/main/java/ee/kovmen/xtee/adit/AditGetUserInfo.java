package ee.kovmen.xtee.adit;

import java.util.ArrayList;
import java.util.List;

import org.ow2.bonita.connector.core.ConnectorError;

import ee.finestmedia.xtee.client.adit.AditRequestInfo;
import ee.finestmedia.xtee.client.adit.AditResponse;
import ee.finestmedia.xtee.client.adit.types.noNamespace.GetUserInfoResponseAttachmentV1UserType;
import ee.kovmen.SoapConnector;
import ee.kovmen.xtee.result.AditUserInfo;

public class AditGetUserInfo extends AbstractAditConnector {

	private String isikukood;
	private AditUserInfo userInfo;

	public AditUserInfo getUserInfo(String isikukood) throws Exception {
		AditRequestInfo info = new AditRequestInfo(isikukood);
		info.setInstitution(SoapConnector.getConfigValue("asutus_smartlink",
				"10648908"));

		if (!isikukood.startsWith("EE"))
			info.setIdCode("EE" + isikukood);
		else
			info.setIdCode(isikukood);

		ArrayList<String> userCodes = new ArrayList<String>();
		userCodes.add(isikukood);

		AditResponse<List<GetUserInfoResponseAttachmentV1UserType>> result = getService()
				.getUserInfo(info, userCodes);
		userCodes.add(isikukood);

		GetUserInfoResponseAttachmentV1UserType userType = result.getExtra()
				.get(0);

		AditUserInfo ui = new AditUserInfo();
		ui.setCanRead(userType.getCanRead());
		ui.setCanWrite(userType.getCanWrite());
		ui.setUsesDVK(userType.getUsesDvk());
		ui.setHasJoined(userType.getHasJoined());
		ui.setFreeSpaceBytes(userType.getFreeSpaceBytes());
		ui.setUsedSpaceBytes(userType.getUsedSpaceBytes());
	
		
		return ui;

	}

	@Override
	protected void executeConnector() throws Exception {
		userInfo = getUserInfo(isikukood);

	}

	@Override
	protected List<ConnectorError> validateValues() {
		List<ConnectorError> errors = new ArrayList<ConnectorError>();
		if (this.isikukood == null) {
			errors.add(new ConnectorError("Isikukood", new Exception(
					"Isikukood puudub")));
		}
		return errors.size() == 0 ? null : errors;
	}

	public void setIsikukood(String isikukood) {
		this.isikukood = isikukood;
	}

	public String getIsikukood() {
		return isikukood;
	}

	public void setUserInfo(AditUserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public AditUserInfo getUserInfo() {
		return userInfo;
	}

}
