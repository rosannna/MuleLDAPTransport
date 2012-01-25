package org.mule.transport.ldapx.transformers;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.LDAPModifyRequest;
import com.novell.ldap.LDAPSearchResults;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;
import org.mule.transport.ldapx.LdapxConnector;

public class LdapEntryToModifyRequest extends AbstractTransformer {

	private LdapxConnector ldapConnector = null;

	@Override
	protected Object doTransform(Object source, String string) throws TransformerException {
		if (null == source || !(source instanceof LDAPEntry)) {
			throw new TransformerException(this, new IllegalArgumentException("source is null or not instance of LDAPEntry"));
		}

		try {
			LDAPEntry entry = (LDAPEntry) source;
			logger.debug("DN: " + entry.getDN());
			String dn = entry.getDN();
			String uid = dn.substring(0, dn.indexOf(","));
			String base = dn.substring(dn.indexOf(",") + 1);
			logger.debug("UID: " + uid);
			logger.debug("BASE: " + base);
			LDAPSearchResults results = getLdapConnector().getLdapConnection().search(base, getLdapConnector().getSearchScope(), "(" + uid + ")", null, false);
			if (!results.hasMore()) {
				return null;
			}
			LDAPEntry old = results.next();
			Iterator<LDAPAttribute> it = entry.getAttributeSet().iterator();
			List<LDAPModification> modifications = new LinkedList<LDAPModification>();
			while (it.hasNext()) {
				LDAPAttribute attr = it.next();
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
			return new LDAPModifyRequest(entry.getDN(), modifications.toArray(new LDAPModification[1]), null);
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
