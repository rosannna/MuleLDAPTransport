package org.mule.transport.ldapx.transformers;

import com.novell.ldap.LDAPEntry;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

public class LdapEntryToString extends AbstractTransformer {

	@Override
	protected Object doTransform(Object source, String string) throws TransformerException {
		if (null == source || !(source instanceof LDAPEntry)) {
			throw new TransformerException(this, new IllegalArgumentException("source is null or not instance of LDAPEntry"));
		}
		return ((LDAPEntry) source).getDN();
	}	
}
