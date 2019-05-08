package cpix.dto;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="CPIX", namespace="urn:dashif:org:cpix")
public class CpixDTO {
    private String id;
    private List<ContentKeyDTO> contentKeyList;
    private List<DrmSystemDTO> drmSystemList;
    private List<ContentKeyPeriodDTO> contentKeyPeriodList;
    private List<ContentKeyUsageRuleDTO> contentKeyUsageRuleList;

    @XmlAttribute(name="id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElementWrapper(name="ContentKeyList", namespace="urn:dashif:org:cpix")
    @XmlElement(name="ContentKey", type=ContentKeyDTO.class, namespace="urn:dashif:org:cpix")
    public List<ContentKeyDTO> getContentKeyList() {
        return contentKeyList;
    }

    public void setContentKeyList(List<ContentKeyDTO> contentKeyList) {
        this.contentKeyList = contentKeyList;
    }

    @XmlElementWrapper(name="DRMSystemList", namespace="urn:dashif:org:cpix")
    @XmlElement(name="DRMSystem", type=DrmSystemDTO.class, namespace="urn:dashif:org:cpix")
    public List<DrmSystemDTO> getDrmSystemList() {
        return drmSystemList;
    }

    public void setDrmSystemList(List<DrmSystemDTO> drmSystemList) {
        this.drmSystemList = drmSystemList;
    }

    @XmlElementWrapper(name="ContentKeyPeriodList", namespace="urn:dashif:org:cpix")
    @XmlElement(name="ContentKeyPeriod", type=ContentKeyPeriodDTO.class, namespace="urn:dashif:org:cpix")
    public List<ContentKeyPeriodDTO> getContentKeyPeriodList() {
        return contentKeyPeriodList;
    }

    public void setContentKeyPeriodList(List<ContentKeyPeriodDTO> contentKeyPeriodList) {
        this.contentKeyPeriodList = contentKeyPeriodList;
    }

    @XmlElementWrapper(name="ContentKeyUsageRuleList", namespace="urn:dashif:org:cpix")
    @XmlElement(name="ContentKeyUsageRule", type=ContentKeyUsageRuleDTO.class, namespace="urn:dashif:org:cpix")
    public List<ContentKeyUsageRuleDTO> getContentKeyUsageRuleList() {
        return contentKeyUsageRuleList;
    }

    public void setContentKeyUsageRuleList(List<ContentKeyUsageRuleDTO> contentKeyUsageRuleList) {
        this.contentKeyUsageRuleList = contentKeyUsageRuleList;
    }
}
