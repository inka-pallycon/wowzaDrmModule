package cpix.dto;

import javax.xml.bind.annotation.XmlAttribute;

public class KeyPeriodFilterDTO {
    String periodId;

    public KeyPeriodFilterDTO(){}

    public KeyPeriodFilterDTO(String periodId) {
        this.periodId = periodId;
    }

    @XmlAttribute(name="periodId")
    public String getPeriodId() {
        return periodId;
    }

    public void setPeriodId(String periodId) {
        this.periodId = periodId;
    }
}
