package org.mule.transport.ldapx.transformers;

import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchRequest;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;
import org.mule.transport.ldapx.LdapxConnector;
import org.mule.util.StringUtils;

public class StringToSearchRequest extends AbstractTransformer {

	private LdapxConnector ldapConnector = null;
	private String searchBase = null;

	@Override
	protected Object doTransform(Object source, String string) throws TransformerException {
		if (null == source || StringUtils.isEmpty(source.toString())) {
			throw new TransformerException(this, new IllegalArgumentException("source can not be null or empty"));
		}
		try {
			return new LDAPSearchRequest((null != searchBase) ? searchBase : getLdapConnector().getSearchBase(),
					getLdapConnector().getSearchScope(), source.toString(),
					null, // TODO: Fix it (attributes to return)
					getLdapConnector().getDereference(),
					getLdapConnector().getMaxResults(),
					getLdapConnector().getTimeLimit(),
					false, // TODO: Fix it
					null);
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
}
