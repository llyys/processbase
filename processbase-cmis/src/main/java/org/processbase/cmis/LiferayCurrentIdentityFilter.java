package org.processbase.cmis;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.web.AbstractFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.ConversationRegistry;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.services.security.StateKey;
import org.exoplatform.services.security.web.HttpSessionStateKey;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

public class LiferayCurrentIdentityFilter extends AbstractFilter {

	private boolean restoreIdentity;

	/**
	 * Logger.
	 */
	private static Log log = ExoLogger
			.getLogger("exo.core.component.security.core.SetCurrentIdentityFilter");

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void afterInit(FilterConfig config) throws ServletException {
		super.afterInit(config);
		restoreIdentity = Boolean.parseBoolean(config
				.getInitParameter("restoreIdentity"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		ExoContainer container = getContainer();

		try {
			ExoContainerContext.setCurrentContainer(container);
			ConversationState state = getCurrentState(container, httpRequest);
			ConversationState.setCurrent(state);
			chain.doFilter(request, response);
		} finally {
			try {
				ConversationState.setCurrent(null);
			} catch (Exception e) {
				log.warn("An error occured while cleaning the ThreadLocal", e);
			}
			try {
				ExoContainerContext.setCurrentContainer(null);
			} catch (Exception e) {
				log.warn("An error occured while cleaning the ThreadLocal", e);
			}
		}
	}

	/**
	 * Gives the current state
	 */
	private ConversationState getCurrentState(ExoContainer container,
			HttpServletRequest httpRequest) {
		ConversationRegistry conversationRegistry = (ConversationRegistry) container
				.getComponentInstanceOfType(ConversationRegistry.class);

		IdentityRegistry identityRegistry = (IdentityRegistry) container
				.getComponentInstanceOfType(IdentityRegistry.class);

		ConversationState state = null;
		try {
			User user = PortalUtil.getUser(httpRequest);

//			String userId = httpRequest.getRemoteUser();
			String userId = String.valueOf(user.getUserId());

			// only if user authenticated, otherwise there is no reason to do
			// anythings
			if (userId != null) {
				HttpSession httpSession = httpRequest.getSession();
				StateKey stateKey = new HttpSessionStateKey(httpSession);

				if (log.isDebugEnabled()) {
					log.debug("Looking for Conversation State "
							+ httpSession.getId());
				}

				state = conversationRegistry.getState(stateKey);

				if (state == null) {
					if (log.isDebugEnabled()) {
						log.debug("Conversation State not found, try create new one.");
					}

					Identity identity = identityRegistry.getIdentity(userId);
					if (identity == null) {
						int index = 0;
						if (userId != null
								&& (index = userId.lastIndexOf('#')) > -1) {
							userId = userId.substring(index + 1);
							identity = identityRegistry.getIdentity(userId);
						}
					}
					if (identity != null) {
						state = new ConversationState(identity);
						// Keep subject as attribute in ConversationState.
						// TODO remove this, do not need it any more.
						state.setAttribute(ConversationState.SUBJECT,
								identity.getSubject());
					} else {

						if (restoreIdentity) {
							if (log.isDebugEnabled()) {
								log.debug("Not found identity for " + userId
										+ " try to restore it. ");
							}

							Authenticator authenticator = (Authenticator) container
									.getComponentInstanceOfType(Authenticator.class);
							try {
								identity = authenticator.createIdentity(userId);
								identityRegistry.register(identity);
							} catch (Exception e) {
								log.error(
										"Unable restore identity. "
												+ e.getMessage(), e);
							}

							if (identity != null) {
								state = new ConversationState(identity);
							}
						} else {
							log.error("Not found identity in IdentityRegistry for user "
									+ userId + ", check Login Module.");
						}
					}

					if (state != null) {
						conversationRegistry.register(stateKey, state);
						if (log.isDebugEnabled()) {
							log.debug("Register Conversation state "
									+ httpSession.getId());
						}
						return state;
					}
				}
			}

			return state;
		} catch (PortalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		state = new ConversationState(new Identity("__anonim"));
		return state;
	}

}
