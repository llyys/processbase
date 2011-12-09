package org.caliburn.application.event.imp;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.reflect.*;

import org.caliburn.application.event.IHandle;

public class Handler {
	WeakReference<Object> reference;
	Map<Class<? extends Object>, Method> supportedHandlers = new HashMap<Class<? extends Object>, Method>();

    public Handler(Object handler) {
        reference = new WeakReference<Object>(handler);
        Class ihandlerType=null;
		try {
			ihandlerType = Class.forName("org.caliburn.application.event.IHandle");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        Class<? extends Object> handlerType=handler.getClass();
        for (Type  ifc : handlerType.getGenericInterfaces()) {
        	if(ifc instanceof ParameterizedType )
			{
        		ParameterizedType ptype=(ParameterizedType)ifc;        		
				Class type = (Class)ptype.getActualTypeArguments()[0];
				Method method;
				try {
					method = handlerType.getMethod("Handle", type);
					supportedHandlers.put((Class)type, method);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
				
			}
		}
    }
    
    public boolean Matches(Object instance) {
        return reference.getClass().equals(instance.getClass());
    }
    
    public boolean Handle(Class messageType, Object message) {
        Class<? extends WeakReference> target = reference.getClass();
        if(target == null)
            return false;
        for (Entry<Class<? extends Object>, Method> pair : supportedHandlers.entrySet()) {
        	if(pair.getKey().isAssignableFrom(messageType)) {
                try {
					pair.getValue().invoke(reference.get(), new Object[] { message });
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				}
                return true;
            }
		}
        
        return true;
    }


}
