package org.mule.transport.ldapx.transformers;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPEntry;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.mule.transformer.AbstractMessageTransformer;
import org.mule.util.StringUtils;

public class MessageToLdapEntry extends AbstractMessageTransformer {

	public static final String LDAP_PROPERTY_PREFIX = "ldap_";

	private LDAPAttribute getLdapAttributeForName(String name, MuleMessage message) {
		Object prop = message.getProperty(LDAP_PROPERTY_PREFIX + name, PropertyScope.OUTBOUND);
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

		String names = "";
		for (String s : message.getInboundPropertyNames()) {
			names += s + ", ";
		}
		logger.debug(names);
		for (String s : message.getOutboundPropertyNames()) {
			names += s + ", ";
		}
		logger.debug(names);
		LDAPAttributeSet attributes = new LDAPAttributeSet();
		for (String key : message.getOutboundPropertyNames()) {
			if (!key.startsWith(LDAP_PROPERTY_PREFIX))
				continue;
			attributes.add(getLdapAttributeForName(key.replaceFirst(LDAP_PROPERTY_PREFIX, ""), message));
		}
		return new LDAPEntry((String) payload, attributes);
	}
}
