package cpix.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class DrmSystemDTO {
    String systemId;
    String kid;
    String pssh;
    String uriExtXKey;
    String contentProtectionData;

    public DrmSystemDTO(){}
    public DrmSystemDTO(String kid, String systemId) {
        this.systemId = systemId;
        this.kid = kid;
    }

    @XmlAttribute(name="systemId")
    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @XmlAttribute(name="kid")
    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    @XmlElement(name="PSSH", namespace="urn:dashif:org:cpix")
    public String getPssh() {
        return pssh;
    }

    public void setPssh(String pssh) {
        this.pssh = pssh;
    }

    @XmlElement(name="URIExtXKey", namespace="urn:dashif:org:cpix")
    public String getUriExtXKey() {
        return uriExtXKey;
    }

    public void setUriExtXKey(String uriExtXKey) {
        this.uriExtXKey = uriExtXKey;
    }

    @XmlElement(name="ContentProtectionData", namespace="urn:dashif:org:cpix")
    public String getContentProtectionData() {
        return contentProtectionData;
    }

    public void setContentProtectionData(String contentProtectionData) {
        this.contentProtectionData = contentProtectionData;
    }
}
