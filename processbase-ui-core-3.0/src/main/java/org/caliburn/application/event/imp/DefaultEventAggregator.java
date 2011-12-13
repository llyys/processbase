package org.caliburn.application.event.imp;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.caliburn.application.event.Command;
import org.caliburn.application.event.IEventAggregator;
import org.caliburn.application.event.IHandle;
/**
 * Enables loosely-coupled publication of and subscription to events.
 *
 */
public class DefaultEventAggregator implements IEventAggregator{
	static Logger Log = Logger.getLogger(DefaultEventAggregator.class.getName());
	List<Handler> handlers = new ArrayList<Handler>();

	@Override 
	public void Subscribe(Object instance) {
		synchronized (handlers) {			
            for (Handler handler : handlers) {            
				if(handler.Matches(instance))
					return;
            }
            
            Log.info("Subscribing {0}."+ instance);
            handlers.add(new Handler(instance));
        }		
	}

	/**
	 *  Unsubscribes the instance from all events.
	 */
	@Override
	public void Unsubscribe(Object instance) {
		synchronized (handlers) {
            List<Integer> found=new ArrayList<Integer>();
            int i=0;
            for (Handler handler : handlers) {
            	if(handler.Matches(instance))
            	found.add(i);
            	i++;
			}
            Iterator<Integer> iterator = found.iterator();
            for (Integer integer : found) {
            	handlers.remove(iterator.next());
			}            
        }		
	}
/**
 *  Publishes a message.
 * @param message - The message instance.
 * @param marshall - Allows the publisher to provide a custom thread marshaller for the message publication. 
 * The default uses the UI thread marshaller.
 */
	@Override
	public void Publish(Object message/*, Command<Object> marshall*/) {
		Handler[] toNotify=null;
		synchronized (handlers) {
			toNotify = new Handler[handlers.size()];
			toNotify= handlers.toArray(toNotify);
		}
		List<Handler> dead=new ArrayList<Handler>();
		Class<? extends Object> messageType = message.getClass();
		for (Handler handler : toNotify) {
         	if(!handler.Handle(messageType, message)){
         		dead.add(handler);
         	}
		}
		
		synchronized (handlers) {
			for (Handler handler : dead) {
				handlers.remove(handler);
			}
		}
		
	}

}
