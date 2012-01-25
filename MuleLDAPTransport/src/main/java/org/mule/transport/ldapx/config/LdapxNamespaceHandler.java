/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.ldapx.config;

import org.mule.config.spring.handlers.AbstractMuleNamespaceHandler;
import org.mule.config.spring.parsers.specific.MessageProcessorDefinitionParser;
import org.mule.transport.ldapx.LdapxConnector;
import org.mule.endpoint.URIBuilder;
import org.mule.transport.ldapx.transformers.LdapEntryToAddRequest;
import org.mule.transport.ldapx.transformers.LdapEntryToModifyRequest;
import org.mule.transport.ldapx.transformers.LdapEntryToString;
import org.mule.transport.ldapx.transformers.MessageToLdapEntry;
import org.mule.transport.ldapx.transformers.MessageToModifyRequest;
import org.mule.transport.ldapx.transformers.SearchResultToLdapEntry;
import org.mule.transport.ldapx.transformers.SearchResultsToList;
import org.mule.transport.ldapx.transformers.StringToAddRequest;
import org.mule.transport.ldapx.transformers.StringToDeleteRequest;
import org.mule.transport.ldapx.transformers.StringToSearchRequest;

/**
 * Registers a Bean Definition Parser for handling <code><ldapx:connector></code> elements
 * and supporting endpoint elements.
 */
public class LdapxNamespaceHandler extends AbstractMuleNamespaceHandler {

	public void init() {
		/* This creates handlers for 'endpoint', 'outbound-endpoint' and 'inbound-endpoint' elements.
		The defaults are sufficient unless you have endpoint styles different from the Mule standard ones
		The URIBuilder as constants for common required attributes, but you can also pass in a user-defined String[].
		 */
		registerStandardTransportEndpoints(LdapxConnector.LDAPX, URIBuilder.PATH_ATTRIBUTES);

		/* This will create the handler for your custom 'connector' element.  You will need to add handlers for any other
		xml elements you define.  For more information see:
		http://www.mulesoft.org/documentation/display/MULE3USER/Creating+a+Custom+XML+Namespace
		 */
		registerConnectorDefinitionParser(LdapxConnector.class);

		registerMuleBeanDefinitionParser("searchresult-to-ldapentry-transformer", new MessageProcessorDefinitionParser(SearchResultToLdapEntry.class));
		registerMuleBeanDefinitionParser("ldapentry-to-string-transformer", new MessageProcessorDefinitionParser(LdapEntryToString.class));
		registerMuleBeanDefinitionParser("searchresults-to-list-transformer", new MessageProcessorDefinitionParser(SearchResultsToList.class));
		registerMuleBeanDefinitionParser("message-to-ldapentry-transformer", new MessageProcessorDefinitionParser(MessageToLdapEntry.class));
		registerMuleBeanDefinitionParser("ldapentry-to-addrequest-transformer", new MessageProcessorDefinitionParser(LdapEntryToAddRequest.class));
		registerMuleBeanDefinitionParser("ldapentry-to-modifyrequest-transformer", new MessageProcessorDefinitionParser(LdapEntryToModifyRequest.class));
		registerMuleBeanDefinitionParser("message-to-modifyrequest-transformer", new MessageProcessorDefinitionParser(MessageToModifyRequest.class));
		registerMuleBeanDefinitionParser("string-to-addrequest-transformer", new MessageProcessorDefinitionParser(StringToAddRequest.class));
		registerMuleBeanDefinitionParser("string-to-deleterequest-transformer", new MessageProcessorDefinitionParser(StringToDeleteRequest.class));
		registerMuleBeanDefinitionParser("string-to-searchrequest-transformer", new MessageProcessorDefinitionParser(StringToSearchRequest.class));
	}
}
