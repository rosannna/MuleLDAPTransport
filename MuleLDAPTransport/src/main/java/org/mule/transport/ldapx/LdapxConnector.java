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

import com.novell.ldap.LDAPAddRequest;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPDeleteRequest;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPMessage;
import com.novell.ldap.LDAPModifyDNRequest;
import com.novell.ldap.LDAPModifyRequest;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchRequest;
import java.io.UnsupportedEncodingException;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.transport.MessageReceiver;
import org.mule.transport.AbstractConnector;
import org.mule.transport.AbstractPollingMessageReceiver;
import org.mule.transport.ConnectException;

/**
 * <code>LdapxConnector</code> TODO document
 */
public class LdapxConnector extends AbstractConnector {
	/* This constant defines the main transport protocol identifier */

	public static final String LDAPX = "ldapx";

	private String ldapHost = null;
	private Integer ldapPort = null;
	private String loginDN = null;
	private String password = null;
	private String searchBase = null;
	private Integer searchScope = null;
	private Integer dereference = null;
	private Integer maxResults = null;
	private Integer timeLimit = null;

	private LDAPConnection ldapConnection = null;

	/* For general guidelines on writing transports see
	http://www.mulesoft.org/documentation/display/MULE3USER/Creating+Transports */

	/* IMPLEMENTATION NOTE: All configuaration for the transport should be set
	on the Connector object, this is the object that gets configured in
	MuleXml */
	public LdapxConnector(MuleContext context) {
		super(context);
	}

    @Override
    public MessageReceiver createReceiver(FlowConstruct flowConstruct, InboundEndpoint endpoint) throws Exception {
		logger.debug("createReceiver(FlowConstruct flowConstruct, InboundEndpoint endpoint)");
        return serviceDescriptor.createMessageReceiver(this, flowConstruct, endpoint,
                                                       AbstractPollingMessageReceiver.DEFAULT_POLL_FREQUENCY);
    }

	@Override
	public void doInitialise() throws InitialisationException {
		// Optional; does not need to be implemented. Delete if not required

		/* IMPLEMENTATION NOTE: Is called once all bean properties have been
		set on the connector and can be used to validate and initialise the
		connectors state. */
	}

	@Override
	public void doConnect() throws Exception {
		logger.debug("doConnect()");
		try {
			setLdapConnection(new LDAPConnection());
			getLdapConnection().connect(getLdapHost(), getLdapPort());
			getLdapConnection().bind(LDAPConnection.LDAP_V3, getLoginDN(), getPassword().getBytes("UTF8"));
		} catch(LDAPException e) {
			logger.error(e, e);
		} catch(UnsupportedEncodingException e) {
			logger.error(e, e);
		}
	}

	@Override
	public void doDisconnect() throws Exception {
		logger.debug("doDisconnect()");
		try {
			if (null != getLdapConnection()) {
				getLdapConnection().disconnect();
				setLdapConnection(null);
			}
		} catch(LDAPException e) {
			logger.error(e, e);
		}
	}

	public synchronized void testLdapConnection() throws ConnectException {
		if (null == getLdapConnection() || !getLdapConnection().isConnected() || !getLdapConnection().isConnectionAlive()) {
			try {
				doConnect();
			} catch (Exception ex) {
				throw new ConnectException(ex, this);
			}
		}
	}

	public Object doLDAPRequest(LDAPMessage ldapMessage, boolean async) throws LDAPException {
		try {
			if (async) {
				LdapxMessageQueue.getInstance().sendRequest(getLdapConnection(), ldapMessage);
			} else {
				if (ldapMessage instanceof LDAPAddRequest) {
					logger.debug("Add request");
					getLdapConnection().add(((LDAPAddRequest) ldapMessage).getEntry());
				} else if (ldapMessage instanceof LDAPDeleteRequest) {
					logger.debug("Delete request");
					getLdapConnection().delete(((LDAPDeleteRequest) ldapMessage).getDN());
				} else if (ldapMessage instanceof LDAPModifyRequest) {
					logger.debug("Modify request");
					getLdapConnection().modify(((LDAPModifyRequest) ldapMessage).getDN(), ((LDAPModifyRequest) ldapMessage).getModifications());
				} else if (ldapMessage instanceof LDAPModifyDNRequest) {
					logger.debug("Modify DN request");
					LDAPModifyDNRequest request = (LDAPModifyDNRequest) ldapMessage;
					getLdapConnection().rename(request.getDN(), request.getNewRDN(), request.getDeleteOldRDN());
				} else if (ldapMessage instanceof LDAPSearchRequest) {
					logger.debug("Search request");
					LDAPSearchRequest request = ((LDAPSearchRequest) ldapMessage);
					return getLdapConnection().search(request.getDN(), request.getScope(), request.getStringFilter(), request.getAttributes(), request.isTypesOnly(), (LDAPSearchConstraints) null);
				} else {
					logger.error(String.format("Unknown LDAP message class: %s", ldapMessage.getClass().getName()));
				}
			}
		} catch(LDAPException e) {
			if (e.getResultCode() == LDAPException.NO_SUCH_OBJECT) {
				logger.error("Error: No such object");
			} else if (e.getResultCode() == LDAPException.INSUFFICIENT_ACCESS_RIGHTS) {
				logger.error("Error: Insufficient rights");
			} else {
				logger.error("Error: " + e.toString());
			}
			throw e;
		}
		return ldapMessage;
	}

	@Override
	public void doStart() throws MuleException {
		// Optional; does not need to be implemented. Delete if not required

		/* IMPLEMENTATION NOTE: If there is a single server instance or
		connection associated with the connector i.e. Jms Connection or Jdbc Connection, 
		this method should put the resource in a started state here. */
	}

	@Override
	public void doStop() throws MuleException {
		// Optional; does not need to be implemented. Delete if not required

		/* IMPLEMENTATION NOTE: Should put any associated resources into a
		stopped state. Mule will automatically call the stop() method. */
	}

	@Override
	public void doDispose() {
		// Optional; does not need to be implemented. Delete if not required

		/* IMPLEMENTATION NOTE: Should clean up any open resources associated
		with the connector. */
	}

	public String getProtocol() {
		return LDAPX;
	}

	/**
	 * @return the ldapHost
	 */
	public String getLdapHost() {
		return ldapHost;
	}

	/**
	 * @param ldapHost the ldapHost to set
	 */
	public void setLdapHost(String ldapHost) {
		this.ldapHost = ldapHost;
	}

	/**
	 * @return the ldapPort
	 */
	public Integer getLdapPort() {
		return ldapPort;
	}

	/**
	 * @param ldapPort the ldapPort to set
	 */
	public void setLdapPort(Integer ldapPort) {
		this.ldapPort = ldapPort;
	}

	/**
	 * @return the loginDN
	 */
	public String getLoginDN() {
		return loginDN;
	}

	/**
	 * @param loginDN the loginDN to set
	 */
	public void setLoginDN(String loginDN) {
		this.loginDN = loginDN;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * @return the searchBase
	 */
	public String getSearchBase() {
		return searchBase;
	}

	/**
	 * @param searchBase the searchBase to set
	 */
	public void setSearchBase(String searchBase) {
		this.searchBase = searchBase;
	}

	/**
	 * @return the searchScope
	 */
	public Integer getSearchScope() {
		return searchScope;
	}

	/**
	 * @param searchScope the searchScope to set
	 */
	public void setSearchScope(Integer searchScope) {
		this.searchScope = searchScope;
	}

	/**
	 * @return the dereference
	 */
	public Integer getDereference() {
		return dereference;
	}

	/**
	 * @param dereference the dereference to set
	 */
	public void setDereference(Integer dereference) {
		this.dereference = dereference;
	}

	/**
	 * @return the maxResults
	 */
	public Integer getMaxResults() {
		return maxResults;
	}

	/**
	 * @param maxResults the maxResults to set
	 */
	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * @return the timeLimit
	 */
	public Integer getTimeLimit() {
		return timeLimit;
	}

	/**
	 * @param timeLimit the timeLimit to set
	 */
	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}

	/**
	 * @return the ldapConnection
	 */
	public LDAPConnection getLdapConnection() {
		return ldapConnection;
	}

	/**
	 * @param ldapConnection the ldapConnection to set
	 */
	private void setLdapConnection(LDAPConnection ldapConnection) {
		this.ldapConnection = ldapConnection;
	}
}
