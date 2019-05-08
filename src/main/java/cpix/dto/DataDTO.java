package cpix.dto;

import javax.xml.bind.annotation.XmlElement;

public class DataDTO {
    private SecretDTO secret;

    @XmlElement(name="Secret", namespace="urn:ietf:params:xml:ns:keyprov:pskc")
    public SecretDTO getSecret() {
        return secret;
    }

    public void setSecret(SecretDTO secret) {
        this.secret = secret;
    }
}
