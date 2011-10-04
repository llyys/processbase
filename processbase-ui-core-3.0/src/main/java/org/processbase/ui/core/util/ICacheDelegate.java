package org.processbase.ui.core.util;

public interface ICacheDelegate<T> {
	 T execute() throws Exception;
}
