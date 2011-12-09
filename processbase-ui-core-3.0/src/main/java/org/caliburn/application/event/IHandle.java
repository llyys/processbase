package org.caliburn.application.event;

/**
 * 
 * Denotes a class which can handle a particular type of message.
 * @param <TMessage>
 */
public interface IHandle<TMessage>
{
	/**
	 * Handles the message.
	 * @param message - The message.
	 */
    void Handle(TMessage message);
}