package cpix.dto;

import javax.xml.bind.annotation.XmlElement;

public class SecretDTO {
    private String plainValue;

    @XmlElement(name="PlainValue", namespace="urn:ietf:params:xml:ns:keyprov:pskc")
    public String getPlainValue() {
        return plainValue;
    }

    public void setPlainValue(String plainValue) {
        this.plainValue = plainValue;
    }


}
