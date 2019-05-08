package cpix.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class ContentKeyUsageRuleDTO {
    private String kid;
    private List<KeyPeriodFilterDTO> keyPeriodFilter;
    private List<VideoFilterDTO> videoFilter;

    public ContentKeyUsageRuleDTO() {
    }

    public ContentKeyUsageRuleDTO(String kid) {
        this.kid = kid;
    }

    public ContentKeyUsageRuleDTO(List<KeyPeriodFilterDTO> keyPeriodFilter, List<VideoFilterDTO> videoFilter) {
        this.keyPeriodFilter = keyPeriodFilter;
        this.videoFilter = videoFilter;
    }

    @XmlAttribute(name="kid")
    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }


    @XmlElement(name="KeyPeriodFilter", type=KeyPeriodFilterDTO.class, namespace="urn:dashif:org:cpix")
    public List<KeyPeriodFilterDTO> getKeyPeriodFilter() {
        return keyPeriodFilter;
    }

    public void setKeyPeriodFilter(List<KeyPeriodFilterDTO> keyPeriodFilter) {
        this.keyPeriodFilter = keyPeriodFilter;
    }

    @XmlElement(name="VideoFilter", type=VideoFilterDTO.class, namespace="urn:dashif:org:cpix")
    public List<VideoFilterDTO> getVideoFilter() {
        return videoFilter;
    }

    public void setVideoFilter(List<VideoFilterDTO> videoFilter) {
        this.videoFilter = videoFilter;
    }
}
