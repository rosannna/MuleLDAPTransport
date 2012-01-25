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
import org.mule.api.MuleMessage;
import org.mule.transport.ConnectException;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.lifecycle.CreateException;
import org.mule.api.transport.Connector;
import org.mule.transport.AbstractPollingMessageReceiver;

/**
 * <code>LdapxMessageReceiver</code> TODO document
 */
public class LdapxMessageReceiver extends AbstractPollingMessageReceiver {
	/* For general guidelines on writing transports see
	http://www.mulesoft.org/documentation/display/MULE3USER/Creating+Transports */

	public LdapxMessageReceiver(Connector connector, FlowConstruct flowConstruct,
			InboundEndpoint endpoint, long frequency)
			throws CreateException {
		super(connector, flowConstruct, endpoint);
		logger.debug("LdapxMessageReceiver(Connector connector, FlowConstruct flowConstruct, InboundEndpoint endpoint, long frequency)");
		setFrequency(frequency);
	}

	@Override
	public void doConnect() throws ConnectException {
		try {
			((LdapxConnector) getEndpoint().getConnector()).testLdapConnection();
			super.doConnect();
		} catch (Exception ex) {
			throw new ConnectException(ex, this);
		}
	}

	@Override
	public void poll() throws Exception {
        /* IMPLEMENTATION NOTE: Once you have read the object it can be passed
           into Mule by first creating a new MuleMesage with the object and 
           calling routeMessage i.e.

         MuleMessage message = createMuleMessage(object);
            routeMessage(message));
        */
		LDAPMessage ldapMessage = LdapxMessageQueue.getInstance().poll();
		if (null != ldapMessage)
			logger.debug("Received message: " + ldapMessage.getClass());
		MuleMessage muleMessage = createMuleMessage(ldapMessage);
		routeMessage(muleMessage);
	}
}
