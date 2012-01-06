package ee.kovmen.xtee.adit;

import java.util.ArrayList;
import java.util.List;

import ee.finestmedia.xtee.client.adit.AditRequestInfo;
import ee.finestmedia.xtee.client.adit.AditResponse;
import ee.finestmedia.xtee.client.adit.AmetlikudDokumendidXTeeServiceException;
import ee.finestmedia.xtee.client.adit.domain.UserType;
import org.ow2.bonita.connector.core.ConnectorError;
public class AditJoin extends AditConnector{

	private java.lang.String kasutajaNimi;
	private java.lang.String isikukood;
	private boolean success;
	
	@Override
	protected void executeConnector() throws Exception {
		success=Join(isikukood, kasutajaNimi);
	}
	
	@Override
	public List<ConnectorError> validateValues() {
		List<ConnectorError> errors = new ArrayList<ConnectorError>();
		if(this.isikukood==null){
			errors.add(new ConnectorError("Isikukood", new Exception("Isikukood puudub")));
		}
		return errors.size()==0?null:errors;
	}
	
	public boolean Join(String isikukood, String kasutajaNimi) throws AmetlikudDokumendidXTeeServiceException, Exception{
		AditRequestInfo info = new AditRequestInfo(isikukood);
		//info.setInstitution(getXteeConfig().getInstitution());
				
		if(!isikukood.startsWith("EE"))
			info.setIdCode("EE"+isikukood);
		else
			info.setIdCode(isikukood);
		
		UserType type=new UserType();
		type.setName("person");
		AditResponse<Object> result = getService().join(info, type, kasutajaNimi);
		
		return result.getSuccess();
	}

	public void setKasutajaNimi(java.lang.String kasutajaNimi) {
		this.kasutajaNimi = kasutajaNimi;
	}

	public java.lang.String getKasutajaNimi() {
		return kasutajaNimi;
	}

	public void setIsikukood(java.lang.String isikukood) {
		this.isikukood = isikukood;
	}

	public java.lang.String getIsikukood() {
		return isikukood;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}
	

}
