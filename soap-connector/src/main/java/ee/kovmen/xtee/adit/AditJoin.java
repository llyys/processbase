package ee.kovmen.xtee.adit;

import java.util.ArrayList;
import java.util.List;

import org.ow2.bonita.connector.core.ConnectorError;

import ee.finestmedia.xtee.client.adit.AditRequestInfo;
import ee.finestmedia.xtee.client.adit.AditResponse;
import ee.finestmedia.xtee.client.adit.AmetlikudDokumendidXTeeServiceException;
import ee.finestmedia.xtee.client.adit.domain.UserType;

public class AditJoin extends AbstractAditConnector {

	private String isikukood;
	private String kasutajaNimi;
	
	private Boolean success;

	@Override
	protected void executeConnector() throws Exception {
		success = join(isikukood, kasutajaNimi);
	}

	@Override
	public List<ConnectorError> validateValues() {
		List<ConnectorError> errors = new ArrayList<ConnectorError>();
		if (this.isikukood == null) {
			errors.add(new ConnectorError("Isikukood", new Exception(
					"Isikukood puudub")));
		}
		return errors.size() == 0 ? null : errors;
	}

	public boolean join(String isikukood, String kasutajaNimi)
			throws AmetlikudDokumendidXTeeServiceException, Exception {
		
		AditRequestInfo info = new AditRequestInfo(isikukood);
		// info.setInstitution(getXteeConfig().getInstitution());

		if (!isikukood.startsWith("EE"))
			info.setIdCode("EE" + isikukood);
		else
			info.setIdCode(isikukood);

		UserType type = new UserType();
		type.setName("person");
		AditResponse<Object> result = getService().join(info, type,
				kasutajaNimi);

		return result.getSuccess();
	}

	public void setKasutajaNimi(String kasutajaNimi) {
		this.kasutajaNimi = kasutajaNimi;
	}

	public String getKasutajaNimi() {
		return kasutajaNimi;
	}

	public void setIsikukood(String isikukood) {
		this.isikukood = isikukood;
	}

	public String getIsikukood() {
		return isikukood;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

}
