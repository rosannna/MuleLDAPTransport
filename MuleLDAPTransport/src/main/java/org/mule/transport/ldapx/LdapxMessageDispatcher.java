/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.ldapx;

import com.novell.ldap.LDAPMessage;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.transport.AbstractMessageDispatcher;
import org.mule.transport.ConnectException;

/**
 * <code>LdapxMessageDispatcher</code> TODO document
 */
public class LdapxMessageDispatcher extends AbstractMessageDispatcher {

	private LdapxConnector ldapxConnector = null;

	/* For general guidelines on writing transports see
	http://www.mulesoft.org/documentation/display/MULE3USER/Creating+Transports */
	public LdapxMessageDispatcher(OutboundEndpoint endpoint) {
		super(endpoint);
		setLdapxConnector((LdapxConnector)endpoint.getConnector());
	}

	final LdapxConnector getLdapxConnector() {
		return this.ldapxConnector;
	}

	final void setLdapxConnector(LdapxConnector ldapxConnector) {
		this.ldapxConnector = ldapxConnector;
	}

	@Override
	public void doConnect() throws ConnectException {
		getLdapxConnector().testLdapConnection();
	}

	@Override
	public void doDispatch(MuleEvent event) throws Exception {
		
		/* IMPLEMENTATION NOTE: This is invoked when the endpoint is
		asynchronous.  It should invoke the transport but not return any
		result.  If a result is returned it should be ignorred, but if the
		underlying transport does have a notion of asynchronous processing,
		that should be invoked.  This method is executed in a different
		thread to the request thread. */


		/* IMPLEMENTATION NOTE: The event message needs to be transformed for the outbound transformers to take effect. This
		isn't done automatically in case the dispatcher needs to modify the message before apllying transformers.  To
		get the transformed outbound message call -
		event.transformMessage(); */

		// TODO Write the client code here to dispatch the event over this transport

		//throw new UnsupportedOperationException("doDispatch");
		logger.debug("doDispatch(MuleEvent event)  " + event.getMessageAsString());

		doConnect();

		Object payload = event.getMessage().getPayload();
		if (!(payload instanceof LDAPMessage))
			return;

		LDAPMessage ldapMessage = (LDAPMessage) payload;
		if (null != event.getMessage().getCorrelationId()) {
			ldapMessage.setTag(event.getMessage().getCorrelationId());
		}

		getLdapxConnector().doLDAPRequest(ldapMessage, true);
	}

	@Override
	public MuleMessage doSend(MuleEvent event) throws Exception {
		/* IMPLEMENTATION NOTE: Should send the event payload over the
		transport. If there is a response from the transport it shuold be
		returned from this method. The sendEvent method is called when the
		endpoint is running synchronously and any response returned will
		ultimately be passed back to the callee. This method is executed in
		the same thread as the request thread. */

		/* IMPLEMENTATION NOTE: The event message needs to be transformed for the outbound transformers to take effect. This
		isn't done automatically in case the dispatcher needs to modify the message before apllying transformers.  To
		get the transformed outbound message call -
		event.transformMessage(); */

		// TODO Write the client code here to send the event over this
		// transport (or to dispatch the event to a store or repository)

		// TODO Once the event has been sent, return the result (if any)
		// wrapped in a MuleMessage object

		//throw new UnsupportedOperationException("doSend");
		logger.debug("doSend(MuleEvent event)  " + event.getMessageAsString());

		Object payload = event.getMessage().getPayload();
		if (!(payload instanceof LDAPMessage))
			return event.getMessage();

		LDAPMessage ldapMessage = (LDAPMessage) payload;
		if (null != event.getMessage().getCorrelationId()) {
			ldapMessage.setTag(event.getMessage().getCorrelationId());
		}

		return createMuleMessage(getLdapxConnector().doLDAPRequest(ldapMessage, false), event.getMessage(), event.getEncoding());
	}

//	@Override
//	public void doDispose() {
//		// Optional; does not need to be implemented. Delete if not required
//
//		/* IMPLEMENTATION NOTE: Is called when the Dispatcher is being
//		disposed and should clean up any open resources. */
//	}

}
