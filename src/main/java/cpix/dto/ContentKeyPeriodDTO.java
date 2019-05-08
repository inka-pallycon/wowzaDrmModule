package cpix.dto;

import javax.xml.bind.annotation.XmlAttribute;

public class ContentKeyPeriodDTO {
    String id;
    String index;
    String start;
    String end;

    public ContentKeyPeriodDTO(){}
    public ContentKeyPeriodDTO(String id) {
        this.id = id;
    }

    @XmlAttribute(name="id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name="index")
    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    @XmlAttribute(name="start")
    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    @XmlAttribute(name="end")
    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
