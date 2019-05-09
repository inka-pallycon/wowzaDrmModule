package cpix;

import com.wowza.util.Base64;
import com.wowza.wms.drm.cenc.CencDRMInfoPlayready;
import com.wowza.wms.drm.cenc.CencDRMInfoWidevine;
import com.wowza.wms.drm.cenc.CencInfo;
import com.wowza.wms.drm.playready.PlayReadyKeyInfo;
import com.wowza.wms.httpstreamer.cupertinostreaming.util.CupertinoEncInfo;
import cpix.dto.CpixDTO;
import cpix.dto.DRMSystemId;
import cpix.exception.CpixException;
import cpix.util.StringUtil;

/**
 * Created by Brown on 2019-05-09.
 */
public abstract class WowzaModule extends CpixAbstractModule{
    /**
     *
     * @param responseCpixDTO
     * @param cencInfo
     * @throws CpixException
     */
    @Override
    public void setDashKeyInfo(CpixDTO responseCpixDTO, CencInfo cencInfo) throws CpixException {
        byte[] keyId, key;
        //key info
        if( 0 < responseCpixDTO.getContentKeyList().size()){
            keyId = StringUtil.guidStringToByteArray(responseCpixDTO.getContentKeyList().get(0).getKid()) ;
            key = Base64.decode(responseCpixDTO.getContentKeyList().get(0).getData().getSecret().getPlainValue());

            cencInfo.setAlgorithm(CencInfo.ALGORITHMID_CTR);
            cencInfo.setKID(keyId);
            cencInfo.setEncKeyBytes(key);
        }else{
            throw new CpixException("Key informations were invalid.");
        }

        // add DRM info for first DRM system (Widevine in this case)
        //widevine info
        responseCpixDTO.getDrmSystemList().stream().forEach(drmSystemDTO -> {
            if(DRMSystemId.WIDEVINE.equals(drmSystemDTO.getSystemId())){
                CencDRMInfoWidevine cencDRMInfoWidevine = new CencDRMInfoWidevine();
                cencDRMInfoWidevine.setPsshData(Base64.decode(drmSystemDTO.getPssh()));
                cencInfo.addDRM("myWidevineDRM:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed", cencDRMInfoWidevine);
            }else if(DRMSystemId.PLAYREADY.equals(drmSystemDTO.getSystemId())){
                // add PlayReady DRM info
                CencDRMInfoPlayready cencDRMInfoPlayready = new CencDRMInfoPlayready();
                PlayReadyKeyInfo playReadyKeyInfo = new PlayReadyKeyInfo();
                playReadyKeyInfo.setLicenseURL(getPlayReadyKeyServerUrl());
                playReadyKeyInfo.setKeyId(keyId);
                playReadyKeyInfo.setContentKey(key);
                playReadyKeyInfo.generateChecksum();
                cencDRMInfoPlayready.setPlayReadyKeyInfo(playReadyKeyInfo);
                cencInfo.addDRM("myPlayReadyDRM:9a04f079-9840-4286-ab92-e65be0885f95", cencDRMInfoPlayready);
            }
        });
    }

    @Override
    public void setHlsKeyInfo(CpixDTO responseCpixDTO, CupertinoEncInfo encInfo) throws CpixException {
        byte[] key, iv;
        //key info
        if( 0 < responseCpixDTO.getContentKeyList().size()){
            key = Base64.decode(responseCpixDTO.getContentKeyList().get(0).getData().getSecret().getPlainValue());
            iv = Base64.decode(responseCpixDTO.getContentKeyList().get(0).getExplicitIV());
            responseCpixDTO.getDrmSystemList().stream().forEach(drmSystemDTO -> {
                if( DRMSystemId.FAIRPLAY.equals(drmSystemDTO.getSystemId())){
                    encInfo.setEncMethod(CupertinoEncInfo.METHOD_SAMPLE_AES);
                    encInfo.setEncKeyFormat(this.FPS_KEYFORMAT);
                }else if(DRMSystemId.HLSAES.equals(drmSystemDTO.getSystemId())){
                    encInfo.setEncMethod(CupertinoEncInfo.METHOD_AES_128);
                }
                encInfo.setEncUrl(drmSystemDTO.getUriExtXKey());
                encInfo.setEncKeyBytes(key);
                encInfo.setEncIVBytes(iv);
                encInfo.setEncKeyFormatVersion("1");
                encInfo.setEncIVBytesInChunklist(false);
            });
        }else{
            throw new CpixException("Key informations were invalid.");
        }
    }
}
