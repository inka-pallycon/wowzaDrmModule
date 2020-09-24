package com.pallycon.wowza;

import com.pallycon.cpix.CpixBuilder;
import com.pallycon.cpix.dto.CpixDTO;
import com.pallycon.cpix.exception.CpixException;
import com.wowza.wms.drm.cenc.CencDRMInfoPlayready;
import com.wowza.wms.drm.cenc.CencDRMInfoWidevine;
import com.wowza.wms.drm.cenc.CencInfo;
import com.wowza.wms.drm.playready.PlayReadyKeyInfo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Created by brown on 2020-09-23.
 */
public class DashCencTest {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void dashVodTest(){
        try {
            CpixBuilder cpixBuilder = new CpixBuilder();

            CpixDTO requestCpix = cpixBuilder.setWidevine()
                    .setPlayReady()
                    .build();

            WowzaModule wowzaModule = new PallyConModule();

            CpixDTO responseCpixDTO = wowzaModule.getDrmKeyInfo("test"
                    , "https://kms.pallycon.com/cpix/pallycon/getKey/null", requestCpix);
            logger.info(responseCpixDTO.getId());
        }catch (CpixException e) {
            logger.info(e.getMessage(), e);
            CencInfo cencInfo = new CencInfo();
            createRandomKeyInfo(cencInfo);
        }

    }

    private void createRandomKeyInfo(CencInfo cencInfo){
        byte[] block = new byte[16];
        new Random().nextBytes(block);
        CencDRMInfoWidevine cencDRMInfoWidevine = new CencDRMInfoWidevine();
        cencDRMInfoWidevine.setPsshData(PallyConModule.getPSSHData(block, "errorCid".getBytes()));
        cencInfo.addDRM("myWidevineDRM:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed", cencDRMInfoWidevine);
        // add PlayReady DRM info
        CencDRMInfoPlayready cencDRMInfoPlayready = new CencDRMInfoPlayready();
        PlayReadyKeyInfo playReadyKeyInfo = new PlayReadyKeyInfo();
        playReadyKeyInfo.setLicenseURL("test");
        playReadyKeyInfo.setKeyId(block);
        playReadyKeyInfo.setContentKey(block);
        playReadyKeyInfo.generateChecksum();
        cencDRMInfoPlayready.setPlayReadyKeyInfo(playReadyKeyInfo);
        cencInfo.addDRM("myPlayReadyDRM:9a04f079-9840-4286-ab92-e65be0885f95", cencDRMInfoPlayready);
    }
}
