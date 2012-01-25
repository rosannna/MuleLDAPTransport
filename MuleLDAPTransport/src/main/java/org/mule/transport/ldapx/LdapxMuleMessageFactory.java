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

import org.mule.api.MuleContext;
import org.mule.transport.AbstractMuleMessageFactory;

/**
 * <code>LdapxMuleMessageFactory</code> TODO document
 */
public class LdapxMuleMessageFactory extends AbstractMuleMessageFactory {
	/* For general guidelines on writing transports see
	http://www.mulesoft.org/documentation/display/MULE3USER/Creating+Transports */

	/* IMPLEMENTATION NOTE: The MuleMessageFactory is used to convert an underlying message 
	into a MuleMessage instance. It extracts the payload of the underlying message and
	any message properties and attachments. */
	/* IMPLEMENTATION NOTE: If the underlying transport data is available as a stream
	it is recommended that you pass the stream object into the MuleMessageFacotry as the payload.
	This will ensure that Mule will use streaming where possible. */
	public LdapxMuleMessageFactory(MuleContext context) {
		super(context);
	}

	@Override
	protected Class<?>[] getSupportedTransportMessageTypes() {
		// TODO return the supported message types. This is typically the class name of the
		// underlying transport message

		return new Class<?>[] { Object.class };
	}

	@Override
	protected Object extractPayload(Object transportMessage, String encoding) throws Exception {
		// TODO extract the payload from the transport message
		return transportMessage;
	}

	/* If the transport message supports message properties implement this method to transfer
	all message properties from the transport message to the MuleMessage.
	
	@Override
	protected void addProperties(DefaultMuleMessage message, Object transportMessage) throws Exception
	{
	}
	 */
	/* If the transport message supports attachments implement this method to transfer all 
	attachments to from the transport message to the MuleMessage.
	
	@Override
	protected void addAttachments(DefaultMuleMessage message, Object transportMessage) throws Exception
	{
	}    
	 */
}
