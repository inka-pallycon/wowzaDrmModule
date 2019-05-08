package cpix.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class ContentKeyDTO {
    //optional
    private String id;
    //optional
    private String algorithm;
    //content key id . UUID
    private String kid;
    private String explicitIV;
    private DataDTO data;

    public ContentKeyDTO() {}

    public ContentKeyDTO(String kid) {
        this.kid = kid;
    }

    @XmlAttribute(name="id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @XmlAttribute(name="algorithm")
    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    @XmlAttribute(name="kid")
    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    @XmlAttribute(name="explicitIV")
    public String getExplicitIV() {
        return explicitIV;
    }

    public void setExplicitIV(String explicitIV) {
        this.explicitIV = explicitIV;
    }

    @XmlElement(name="Data", type=DataDTO.class, namespace="urn:dashif:org:cpix")
    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }


}
