package org.caliburn.application.event;

import java.io.Serializable;


public interface Command<T> extends Serializable {
	T execute() throws Exception;
}