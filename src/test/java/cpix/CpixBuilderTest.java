package cpix;

import cpix.dto.ContentKeyDTO;
import cpix.dto.DRMSystemId;
import cpix.dto.DrmSystemDTO;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Created by Brown on 2019-02-15.
 */
public class CpixBuilderTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    CpixBuilder cpixBuilder;
    String kId;
    @Before
    public void setUp() throws Exception{
        cpixBuilder = new CpixBuilder();
        this.kId = "7f841eb7-514c-28a7-d965-68656f25be31";
    }
    @Test
    public void addContentKey() throws Exception {

        assertEquals(1, cpixBuilder.build().getContentKeyList().size());

        ContentKeyDTO contentKeyDTO = new ContentKeyDTO(this.kId);
        cpixBuilder.addContentKey(contentKeyDTO);
        assertEquals(2, cpixBuilder.build().getContentKeyList().size());
    }

    @Test
    public void addDrmSystemList() throws Exception {
        DrmSystemDTO drmSystemDTO = new DrmSystemDTO(this.kId, DRMSystemId.FAIRPLAY);
        cpixBuilder.addDrmSystemList(drmSystemDTO);
        assertEquals(1, cpixBuilder.build().getDrmSystemList().size());
        assertEquals("94CE86FB-07FF-4F43-ADB8-93D2FA968CA2", cpixBuilder.build().getDrmSystemList().get(0).getSystemId());
    }

    @Test
    public void setPeriodIndex() throws Exception {
        cpixBuilder = new CpixBuilder();
        cpixBuilder.setKeyRotation(true).setPeriodIndex(24L);
        assertEquals("24", cpixBuilder.build().getContentKeyPeriodList().get(0).getIndex());
    }

    @Test
    public void setWidevine() throws Exception {
        cpixBuilder.setWidevine();
        assertEquals(DRMSystemId.WIDEVINE, cpixBuilder.build().getDrmSystemList().get(0).getSystemId());
    }

    @Test
    public void setPlayReady() throws Exception {
        cpixBuilder.setPlayReady();
        assertEquals(DRMSystemId.PLAYREADY, cpixBuilder.build().getDrmSystemList().get(0).getSystemId());
    }

    @Test
    public void setFairPlay() throws Exception {
        cpixBuilder.setFairPlay();
        assertEquals(DRMSystemId.FAIRPLAY, cpixBuilder.build().getDrmSystemList().get(0).getSystemId());
    }

    @Test
    public void setHlsAes128() throws Exception {
        cpixBuilder.setHlsAes128();
        assertEquals(DRMSystemId.HLSAES, cpixBuilder.build().getDrmSystemList().get(0).getSystemId());
    }

}