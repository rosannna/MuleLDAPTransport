package org.mule.transport.ldapx.transformers;

import com.novell.ldap.LDAPSearchResult;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

public class SearchResultToLdapEntry extends AbstractTransformer {

	@Override
	protected Object doTransform(Object source, String string) throws TransformerException {
		if (null == source || !(source instanceof LDAPSearchResult)) {
			throw new TransformerException(this, new IllegalArgumentException("source is null or not instance of LDAPSearchResult"));
		}
		return ((LDAPSearchResult) source).getEntry();
	}
}
