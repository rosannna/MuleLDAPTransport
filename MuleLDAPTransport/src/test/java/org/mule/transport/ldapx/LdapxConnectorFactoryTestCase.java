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

import org.mule.api.endpoint.InboundEndpoint;
import org.mule.tck.AbstractMuleTestCase;

public class LdapxConnectorFactoryTestCase extends AbstractMuleTestCase {
	/* For general guidelines on writing transports see
	http://www.mulesoft.org/documentation/display/MULE3USER/Creating+Transports */

	public void testCreateFromFactory() throws Exception {
		InboundEndpoint endpoint = muleContext.getRegistry().lookupEndpointFactory().getInboundEndpoint(getEndpointURI());
		assertNotNull(endpoint);
		assertNotNull(endpoint.getConnector());
		assertTrue(endpoint.getConnector() instanceof LdapxConnector);
		assertEquals(getEndpointURI(), endpoint.getEndpointURI().getAddress());
	}

	public String getEndpointURI() {
		// TODO return a valid endpoint URI string for your transport
		// i.e. tcp://localhost:1234
		throw new UnsupportedOperationException("getEndpointURI");
	}
}
