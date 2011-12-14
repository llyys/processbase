package org.caliburn.viewmodels;

import org.caliburn.application.event.imp.DefaultEventAggregator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.MethodPropertyDescriptor;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.VaadinPropertyDescriptor;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

public class ViewModelBinder<BT> {

	private final ComponentContainer container;
	private final BeanItem<BT> model;
	/**
     * Mapping from propertyName to corresponding field.
     */
    private final HashMap<Object, Field> fields = new HashMap<Object, Field>();

	public ViewModelBinder(ComponentContainer container, BT model){
		this.container = container;
		this.model = new BeanItem<BT>(model);
		
	}
	
	
	static Logger Log = Logger.getLogger(ViewModelBinder.class.getName());
	private LinkedHashMap<String, Component> viewComponents;
	
	
	/**
	 * This method will register compoent to container and also will bind it's property value
	 * @param component
	 * @param property
	 */
	public void addComponent(Field component, String propertyName) {
		
		//viewComponents.put(propertyName, component);
		container.addComponent(component);
		fields.put(propertyName, component);
		
		Property property=this.model.getItemProperty(propertyName);
		if(property!=null)
			component.setPropertyDataSource(property);
	}
	
	 
	
	public BT getBean(){
		for (Entry<Object, Field> entry : fields.entrySet()) {
			entry.getValue().commit();
		}
		return model.getBean();
	}
}
