/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mule.transport.ldapx;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPMessage;
import com.novell.ldap.LDAPMessageQueue;

/**
 *
 * @author eburcev
 */
public class LdapxMessageQueue {

	private LDAPMessageQueue messageQueue = null;

	protected LdapxMessageQueue() {
	}

	private static LdapxMessageQueue instance = new LdapxMessageQueue();

	public static LdapxMessageQueue getInstance() {
		return instance;
	}
			
	public synchronized void sendRequest(LDAPConnection connection, LDAPMessage message) throws LDAPException {
		if (null == messageQueue) {
			messageQueue = connection.sendRequest(message, null);
		} else {
			connection.sendRequest(message, messageQueue);
		}
	}

	public synchronized LDAPMessage poll() throws LDAPException {
		if (null != messageQueue) {
			if (messageQueue.getMessageIDs().length > 0) {
				return messageQueue.getResponse();
			}
		}
		return null;
	}
}
