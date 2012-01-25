package org.mule.transport.ldapx.transformers;

import com.novell.ldap.LDAPAddRequest;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

public class LdapEntryToAddRequest extends AbstractTransformer {

	@Override
	protected Object doTransform(Object source, String string) throws TransformerException {
		if (null == source || !(source instanceof LDAPEntry)) {
			throw new TransformerException(this, new IllegalArgumentException("source is null or not instance of LDAPEntry"));
		}

		try {
			return new LDAPAddRequest((LDAPEntry) source, null);
		} catch (LDAPException ex) {
			throw new TransformerException(this, ex);
		}
	}
}
