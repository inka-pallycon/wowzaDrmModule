package cpix.mapper;


import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class CpixNamespaceMapper extends NamespacePrefixMapper {
    private static final String CPIX_PREFIX = "cpix"; // DEFAULT NAMESPACE
    private static final String CPIX_URI = "urn:dashif:org:cpix";

    private static final String PSKC_PREFIX = "pskc";
    private static final String PSKC_URI = "urn:ietf:params:xml:ns:keyprov:pskc";

    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        String prefix = suggestion;
        switch (namespaceUri) {
            case CPIX_URI:
                prefix = CPIX_PREFIX;
                break;
            case PSKC_URI:
                prefix = PSKC_PREFIX;
                break;
        }
        return prefix;
    }

    @Override
    public String[] getPreDeclaredNamespaceUris() {
        return new String[] { CPIX_URI, PSKC_URI };
    }

}
