package org.mule.transport.ldapx.transformers;

import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;
import java.util.LinkedList;
import java.util.List;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

public class SearchResultsToList extends AbstractTransformer {

	@Override
	protected Object doTransform(Object source, String string) throws TransformerException {
		if (null == source || !(source instanceof LDAPSearchResults)) {
			throw new TransformerException(this, new IllegalArgumentException("source is null or not LDAPSearchResults"));
		}
		List<LDAPEntry> list = new LinkedList<LDAPEntry>();
		LDAPSearchResults searchResults = (LDAPSearchResults) source;
		while (searchResults.hasMore()) {
			LDAPEntry entry = null;
			try {
				entry = searchResults.next();
				list.add(entry);
			} catch(LDAPException e) {
				System.out.println("Error: " + e.toString());
				if(e.getResultCode() != LDAPException.LDAP_TIMEOUT
						&& e.getResultCode() != LDAPException.CONNECT_ERROR) {
					continue;
				} else {
					break;
				}
			}
		}
		return list;
	}
}
