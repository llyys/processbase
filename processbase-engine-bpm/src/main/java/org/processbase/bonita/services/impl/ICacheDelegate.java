package org.processbase.bonita.services.impl;

public interface ICacheDelegate<T> {
	 T execute() throws Exception;
}