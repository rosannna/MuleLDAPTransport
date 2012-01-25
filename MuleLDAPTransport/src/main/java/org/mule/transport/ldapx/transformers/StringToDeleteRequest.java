package org.mule.transport.ldapx.transformers;

import com.novell.ldap.LDAPDeleteRequest;
import com.novell.ldap.LDAPException;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;
import org.mule.util.StringUtils;

public class StringToDeleteRequest extends AbstractTransformer {

	@Override
	protected Object doTransform(Object source, String string) throws TransformerException {
		if (null == source || StringUtils.isEmpty(source.toString())) {
			throw new TransformerException(this, new IllegalArgumentException("source can not be null or empty"));
		}
		try {
			return new LDAPDeleteRequest(source.toString(), null);
		} catch (LDAPException ex) {
			throw new TransformerException(this, ex);
		}
	}

}
