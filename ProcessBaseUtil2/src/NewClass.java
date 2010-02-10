
import com.liferay.client.soap.portal.model.CompanySoap;
import com.liferay.client.soap.portal.model.RoleSoap;
import com.liferay.client.soap.portal.service.http.CompanyServiceSoap;
import com.liferay.client.soap.portal.service.http.CompanyServiceSoapServiceLocator;
import com.liferay.client.soap.portal.service.http.RoleServiceSoap;
import com.liferay.client.soap.portal.service.http.RoleServiceSoapServiceLocator;
import com.liferay.client.soap.portal.service.http.UserServiceSoap;
import com.liferay.client.soap.portal.service.http.UserServiceSoapServiceLocator;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;
import org.processbase.util.Constants;

/**
 *
 * @author mgubaidullin
 */
public class NewClass {

    public static void main(String[] args) throws ServiceException, MalformedURLException, RemoteException {
        Constants.loadConstants();
        CompanyServiceSoapServiceLocator companyServiceSoapServiceLocator = new CompanyServiceSoapServiceLocator();
        CompanyServiceSoap companyServiceSoap = 
                companyServiceSoapServiceLocator.getPortal_CompanyService(new URL("http://" + Constants.LIFERAY_HOST + ":" + Constants.LIFERAY_PORT + "/tunnel-web/axis/Portal_CompanyService"));
        CompanySoap company = companyServiceSoap.getCompanyByMx(Constants.LIFERAY_COMPANY_MX);

        RoleServiceSoapServiceLocator roleServiceSoapServiceLocator = new RoleServiceSoapServiceLocator();
        RoleServiceSoap roleServiceSoap =
                roleServiceSoapServiceLocator.getPortal_RoleService(new URL("http://" + Constants.LIFERAY_HOST + ":" + Constants.LIFERAY_PORT + "/tunnel-web/axis/Portal_RoleService"));
        RoleSoap role = roleServiceSoap.getRole(company.getCompanyId(),
                "Юрист");

        UserServiceSoapServiceLocator g = new UserServiceSoapServiceLocator();
        UserServiceSoap x =
                g.getPortal_UserService(new URL("http://" + Constants.LIFERAY_HOST + ":" + Constants.LIFERAY_PORT + "/tunnel-web/axis/Portal_UserService"));
        for (long z : x.getRoleUserIds(role.getRoleId())) {
            System.out.println(x.getUserById(z).getScreenName());
        }

    }
}
