package org.mule.transport.ldapx.transformers;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.LDAPModifyRequest;
import com.novell.ldap.LDAPSearchResults;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.mule.transformer.AbstractMessageTransformer;
import org.mule.transport.ldapx.LdapxConnector;
import org.mule.util.StringUtils;

public class MessageToModifyRequest extends AbstractMessageTransformer {

	public static final String LDAP_PROPERTY_PREFIX = "ldap_";
	public static final String LDAP_ADD_PROPERTY_PREFIX = "ldap_add_";
	public static final String LDAP_REMOVE_PROPERTY_PREFIX = "ldap_remove_";

	private LdapxConnector ldapConnector = null;

	private LDAPAttribute getLdapAttributeForName(String name, String prefix, MuleMessage message) {
		Object prop = message.getProperty(name, PropertyScope.OUTBOUND);
		name = name.replaceFirst(prefix, "");
		logger.debug("Property class is " + prop.getClass().getName());
		if (prop instanceof String) {
			return new LDAPAttribute(name, (String) prop);
		} else if (prop instanceof String[]) {
			return new LDAPAttribute(name, (String[]) prop);
		} else if (prop instanceof byte[]) {
			return new LDAPAttribute(name, (byte[]) prop);
		}
		return null;
	}

	@Override
	public Object transformMessage(MuleMessage message, String string) throws TransformerException {
		Object payload = message.getPayload();
		if (null == payload || !(payload instanceof String) || StringUtils.isEmpty((String) payload)) {
			throw new TransformerException(this, new IllegalArgumentException("payload can not be null or empty"));
		}
		try {
			String dn = (String) payload;
			String uid = dn.substring(0, dn.indexOf(","));
			String base = dn.substring(dn.indexOf(",") + 1);
			logger.debug("DN: " + dn);
			logger.debug("UID: " + uid);
			logger.debug("BASE: " + base);
			LDAPSearchResults results = null;
			LDAPEntry old = null;
			List<LDAPModification> modifications = new LinkedList<LDAPModification>();
			for (String key : message.getOutboundPropertyNames()) {
				if (!key.startsWith(LDAP_PROPERTY_PREFIX))
					continue;
				if (key.startsWith(LDAP_ADD_PROPERTY_PREFIX)) {
					LDAPAttribute attr = getLdapAttributeForName(key, LDAP_ADD_PROPERTY_PREFIX, message);
					logger.debug("Added attribute: " + attr.getName());
					modifications.add(new LDAPModification(LDAPModification.ADD, attr));
				} else if (key.startsWith(LDAP_REMOVE_PROPERTY_PREFIX)) {
					LDAPAttribute attr = getLdapAttributeForName(key, LDAP_REMOVE_PROPERTY_PREFIX, message);
					logger.debug("Removed attribute: " + attr.getName());
					modifications.add(new LDAPModification(LDAPModification.DELETE, attr));
				} else {
					LDAPAttribute attr = getLdapAttributeForName(key, LDAP_PROPERTY_PREFIX, message);
					if (null == results) {
						results = getLdapConnector().getLdapConnection().search(base, getLdapConnector().getSearchScope(), "(" + uid + ")", null, false);
						if (!results.hasMore()) {
							continue;
						}
					}
					if (null == old) {
						old = results.next();
					}
					String attributeName = attr.getName();
					LDAPAttribute oldAttr = old.getAttribute(attributeName);
					if (null == oldAttr) {
						logger.debug("Added attribute: " + attributeName);
						modifications.add(new LDAPModification(LDAPModification.ADD, attr));
					} else {
						if (!Arrays.equals(oldAttr.getStringValueArray(), attr.getStringValueArray())) {
							logger.debug("Changed attribute: " + attributeName);
							logger.debug("OLD: " + oldAttr.getStringValue());
							logger.debug("NEW: " + attr.getStringValue());
							modifications.add(new LDAPModification(LDAPModification.REPLACE, attr));
						}
					}
				}
			}
			return new LDAPModifyRequest(dn, modifications.toArray(new LDAPModification[1]), null);
		} catch (LDAPException ex) {
			throw new TransformerException(this, ex);
		}
	}

	/**
	 * @return the ldapConnector
	 */
	public LdapxConnector getLdapConnector() {
		return ldapConnector;
	}

	/**
	 * @param ldapConnector the ldapConnector to set
	 */
	public void setLdapConnector(LdapxConnector ldapConnector) {
		this.ldapConnector = ldapConnector;
	}
}
