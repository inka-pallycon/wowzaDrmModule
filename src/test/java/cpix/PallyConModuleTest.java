package cpix;

import cpix.dto.CpixDTO;
import cpix.util.StringUtil;
import com.wowza.util.Base64;
import com.wowza.wms.drm.cenc.CencInfo;
import com.wowza.wms.drm.cenc.ICencDRMInfo;
import cpix.wowza.PallyConModule;
import cpix.wowza.WowzaModule;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by Brown on 2019-02-15.
 */
public class PallyConModuleTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    CpixDTO requestCpixDTO;
    CpixDTO responseCpixDTO;
    CPixCommonModule cPixCommonModule;

    @Before
    public void setUp() throws Exception{
        this.cPixCommonModule = new CPixCommonModule();
    }
    @Test
    public void parseCpixData() throws Exception {
        String cpix = cPixCommonModule.toCpixString(new CpixBuilder().setFairPlay().build());
        logger.info(cpix);
        String cpixData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<cpix:CPIX xmlns:cpix=\"urn:dashif:org:cpix\" xmlns:pskc=\"urn:ietf:params:xml:ns:keyprov:pskc\">\n" +
                "    <cpix:ContentKeyList>\n" +
                "        <cpix:ContentKey kid=\"a0ec4f4a-8edd-4e3a-9f7b-b2aef76f770a\"/>\n" +
                "    </cpix:ContentKeyList>\n" +
                "    <cpix:DRMSystemList>\n" +
                "        <cpix:DRMSystem kid=\"a0ec4f4a-8edd-4e3a-9f7b-b2aef76f770a\" systemId=\"94CE86FB-07FF-4F43-ADB8-93D2FA968CA2\"/>\n" +
                "    </cpix:DRMSystemList>\n" +
                "</cpix:CPIX>";
        CpixDTO cpixDTO = cPixCommonModule.parseCpixData(cpixData);
        assertEquals("a0ec4f4a-8edd-4e3a-9f7b-b2aef76f770a", cpixDTO.getContentKeyList().get(0).getKid());
        assertEquals("94CE86FB-07FF-4F43-ADB8-93D2FA968CA2", cpixDTO.getDrmSystemList().get(0).getSystemId());
    }

    @Test
    public void setDashKeyInfo() throws Exception {
        String responseData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<cpix:CPIX id=\"testKey\" xmlns:cpix=\"urn:dashif:org:cpix\" xmlns:speke=\"urn:aws:amazon:com:speke\" xmlns:pskc=\"urn:ietf:params:xml:ns:keyprov:pskc\">\n" +
                "    <cpix:ContentKeyList>\n" +
                "        <cpix:ContentKey explicitIV=\"MDEyMzQ1Njc4OWFiY2RlZg==\" kid=\"7f841eb7-514c-28a7-d965-68656f25be31\">\n" +
                "            <cpix:Data>\n" +
                "                <pskc:Secret>\n" +
                "                    <pskc:PlainValue>Q3R2DJqAjr0ao8igD23XIw==</pskc:PlainValue>\n" +
                "                </pskc:Secret>\n" +
                "            </cpix:Data>\n" +
                "        </cpix:ContentKey>\n" +
                "    </cpix:ContentKeyList>\n" +
                "    <cpix:DRMSystemList>\n" +
                "        <cpix:DRMSystem kid=\"7f841eb7-514c-28a7-d965-68656f25be31\" systemId=\"9A04F079-9840-4286-AB92-E65BE0885F95\">\n" +
                "            <cpix:PSSH>AAACvnBzc2gAAAAAmgTweZhAQoarkuZb4IhflQAAAp6eAgAAAQABAJQCPABXAFIATQBIAEUAQQBEAEUAUgAgAHgAbQBsAG4AcwA9ACIAaAB0AHQAcAA6AC8ALwBzAGMAaABlAG0AYQBzAC4AbQBpAGMAcgBvAHMAbwBmAHQALgBjAG8AbQAvAEQAUgBNAC8AMgAwADAANwAvADAAMwAvAFAAbABhAHkAUgBlAGEAZAB5AEgAZQBhAGQAZQByACIAIAB2AGUAcgBzAGkAbwBuAD0AIgA0AC4AMAAuADAALgAwACIAPgA8AEQAQQBUAEEAPgA8AFAAUgBPAFQARQBDAFQASQBOAEYATwA+ADwASwBFAFkATABFAE4APgAxADYAPAAvAEsARQBZAEwARQBOAD4APABBAEwARwBJAEQAPgBBAEUAUwBDAFQAUgA8AC8AQQBMAEcASQBEAD4APAAvAFAAUgBPAFQARQBDAFQASQBOAEYATwA+ADwASwBJAEQAPgB0AHgANgBFAGYAMAB4AFIAcAB5AGoAWgBaAFcAaABsAGIAeQBXACsATQBRAD0APQA8AC8ASwBJAEQAPgA8AEMASABFAEMASwBTAFUATQA+ADAAcAAvAEcAaQBqADMAQQBlAFAARQA9ADwALwBDAEgARQBDAEsAUwBVAE0APgA8AEwAQQBfAFUAUgBMAD4AaAB0AHQAcABzADoALwAvAGwAaQBjAGUAbgBzAGUALgBwAGEAbABsAHkAYwBvAG4ALgBjAG8AbQAvAHIAaQAvAHAAbABhAHkAcgBlAGEAZAB5AC8AbABpAGMAZQBuAHMAZQBNAGEAbgBhAGcAZQByAC4AZABvADwALwBMAEEAXwBVAFIATAA+ADwALwBEAEEAVABBAD4APAAvAFcAUgBNAEgARQBBAEQARQBSAD4A</cpix:PSSH>\n" +
                "        </cpix:DRMSystem>\n" +
                "        <cpix:DRMSystem kid=\"7f841eb7-514c-28a7-d965-68656f25be31\" systemId=\"EDEF8BA9-79D6-4ACE-A3C8-27DCD51D21ED\">\n" +
                "            <cpix:PSSH>AAAAU3Bzc2gAAAAA7e+LqXnWSs6jyCfc1R0h7QAAADMIARIQf4Qet1FMKKfZZWhlbyW+MRoMaW5rYWVudHdvcmtzIgtoZWFsdGhjaGVjayoCSEQ=</cpix:PSSH>\n" +
                "        </cpix:DRMSystem>\n" +
                "    </cpix:DRMSystemList>\n" +
                "</cpix:CPIX>";
        CpixDTO cpixDTO = cPixCommonModule.parseCpixData(responseData);

        CencInfo cencInfo = new CencInfo();
        WowzaModule wowzaModule = new PallyConModule();

        wowzaModule.setDashKeyInfo(cpixDTO, cencInfo);
        assertEquals("7F841EB7-514C-28A7-D965-68656F25BE31", cencInfo.getKID());
        assertEquals("Q3R2DJqAjr0ao8igD23XIw==", cencInfo.getEncKeyString());
        Map<String, ICencDRMInfo> cencDrms = cencInfo.getDRMs();
        cencDrms.forEach((drmSystemId, drmInfo) ->{
            if( "myWidevineDRM:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed".equals(drmSystemId)) {
                assertEquals("AAAAU3Bzc2gAAAAA7e+LqXnWSs6jyCfc1R0h7QAAADMIARIQf4Qet1FMKKfZZWhlbyW+MRoMaW5r\nYWVudHdvcmtzIgtoZWFsdGhjaGVjayoCSEQ=", Base64.encodeBytes(drmInfo.getPsshData(true)));
            }
        });

    }

    @Test
    public void toCpixString() throws Exception {
        CpixDTO cpixDTO = new CpixBuilder().setFairPlay().build();
        String kid = cpixDTO.getContentKeyList().stream().findFirst().get().getKid();
        String cpix = cPixCommonModule.toCpixString(cpixDTO);
        logger.info(cpix);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<cpix:CPIX xmlns:cpix=\"urn:dashif:org:cpix\" xmlns:pskc=\"urn:ietf:params:xml:ns:keyprov:pskc\">\n" +
                "    <cpix:ContentKeyList>\n" +
                "        <cpix:ContentKey kid=\"" + kid + "\"/>\n" +
                "    </cpix:ContentKeyList>\n" +
                "    <cpix:DRMSystemList>\n" +
                "        <cpix:DRMSystem kid=\"" + kid + "\" systemId=\"94CE86FB-07FF-4F43-ADB8-93D2FA968CA2\"/>\n" +
                "    </cpix:DRMSystemList>\n" +
                "</cpix:CPIX>\n", cpix);
    }

}
