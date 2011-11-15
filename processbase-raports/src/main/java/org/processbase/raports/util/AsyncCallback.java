package org.processbase.raports.util;

public interface AsyncCallback<T> {
	void onSuccess(T result);
	void onFailure(Throwable caught);
}
