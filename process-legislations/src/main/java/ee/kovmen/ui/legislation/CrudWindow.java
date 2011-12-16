package ee.kovmen.ui.legislation;

import java.lang.reflect.ParameterizedType;

import org.caliburn.viewmodels.ViewModelBinder;
import org.processbase.ui.core.template.PbWindow;

import ee.kovmen.entities.KovLegislation;

public class CrudWindow<T> extends PbWindow{
	private ViewModelBinder<T> binder;
	private T data;
	public CrudWindow(T data){
		this.data = data;		
	}
	
	private T getDataInstance(){
		if(data==null)
		{
			Class entityBeanType = ((Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
			try {
				return (T)entityBeanType.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		return data;
	}
	
	public void initUI(){
		
	}
}
