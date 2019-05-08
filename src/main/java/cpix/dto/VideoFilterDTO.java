package cpix.dto;

import javax.xml.bind.annotation.XmlAttribute;

public class VideoFilterDTO {
    int minPixels;
    int maxPixels;

    public VideoFilterDTO() {}

    public VideoFilterDTO(int minPixels, int maxPixels) {
        this.minPixels = minPixels;
        this.maxPixels = maxPixels;
    }

    @XmlAttribute(name="minPixels")
    public int getMinPixels() {
        return minPixels;
    }

    public void setMinPixels(int minPixels) {
        this.minPixels = minPixels;
    }

    @XmlAttribute(name="maxPixels")
    public int getMaxPixels() {
        return maxPixels;
    }

    public void setMaxPixels(int maxPixels) {
        this.maxPixels = maxPixels;
    }
}
