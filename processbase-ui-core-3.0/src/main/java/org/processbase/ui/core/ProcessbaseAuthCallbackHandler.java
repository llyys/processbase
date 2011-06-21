package org.processbase.ui.core;

import java.io.IOException;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class ProcessbaseAuthCallbackHandler implements CallbackHandler {
	 private final String name;
	  private final String password;

	  public ProcessbaseAuthCallbackHandler(final String name, String password) {
	    this.name = name;
	    this.password = password;
	  }
	  
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
	    for (Callback callback : callbacks) {
	      if (callback instanceof NameCallback) {
	        final NameCallback nc = (NameCallback) callback;
	        nc.setName(name);
	      } else if (callback instanceof PasswordCallback) {
	        final PasswordCallback pc = (PasswordCallback) callback;
	        pc.setPassword(password.toCharArray());
	      }
	    }
	  }

}
