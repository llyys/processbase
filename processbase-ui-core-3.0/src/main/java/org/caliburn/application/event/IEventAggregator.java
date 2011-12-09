package org.caliburn.application.event;
/**
 * Enables loosely-coupled publication of and subscription to events.
 *
 */
public interface IEventAggregator {

	/**
	 * Subscribes an instance to all events declared through implementations of IHandle
	 * @param instance
	 */
	void Subscribe(Object instance);
	/**
	 * Unsubscribes the instance from all events.
	 * @param instance
	 */
	void Unsubscribe(Object instance);
	/**
	 * Publishes a message.
	 * @param message - The message instance.
	 * @param marshall - Allows the publisher to provide a custom thread marshaller for the message publication. The default uses the UI thread marshaller.
	 */
	void Publish(Object message/*, Action<Action> marshall*/);

}
