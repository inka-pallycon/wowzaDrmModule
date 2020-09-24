package com.pallycon.wowza;

import com.google.protobuf.ByteString;
import com.pallycon.cpix.CPixCommonModule;
import com.pallycon.cpix.dto.CpixDTO;
import com.pallycon.cpix.dto.DRMSystemId;
import com.pallycon.cpix.exception.CpixException;
import com.pallycon.wowza.util.StringUtil;
import com.wowza.util.Base64;
import com.wowza.wms.drm.cenc.CencDRMInfoPlayready;
import com.wowza.wms.drm.cenc.CencDRMInfoWidevine;
import com.wowza.wms.drm.cenc.CencInfo;
import com.wowza.wms.drm.playready.PlayReadyKeyInfo;
import com.wowza.wms.httpstreamer.cupertinostreaming.util.CupertinoEncInfo;
import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.logging.WMSLoggerFactory;

import static com.pallycon.cpix.CPixCommonModule.toCpixString;


/**
 * Created by Brown on 2019-02-08.
 */
public class PallyConModule implements WowzaModule {
    public final static String PALLYCON_PLAYREADY_URL = "https://license.pallycon.com/ri/licenseManager.do";
    private static final String PROVIDER_NAME = "inkaentworks";
    private final static String FPS_KEYFORMAT = "com.apple.streamingkeydelivery";
    private WMSLogger logger = WMSLoggerFactory.getLogger(null);

    /**
     * kms 서버로부터 키정보를 받아온다.
     * @param streamPath
     * @param requestUrl
     * @param cpixDTO
     * @return
     * @throws CpixException
     */
    public CpixDTO getDrmKeyInfo(String streamPath, String requestUrl, CpixDTO cpixDTO) throws CpixException {
        CpixDTO responseCpixDTO;
        String contentId= getStreamName(streamPath);
        logger.info("[PallyCon] Request CID : " + contentId);
        cpixDTO.setId(contentId);
        CPixCommonModule cPixCommonModule = new CPixCommonModule();
        logger.info("[PallyCon] GetDrmKeyInfo Request ContentId : " + contentId);

        String requestCPix = toCpixString(cpixDTO);
        String responseData = cPixCommonModule.callMethodPostDrmKeyServer(requestCPix, requestUrl);

        logger.info("[PallyCon] kms responseData : " + responseData);
        if(cPixCommonModule.checkError(responseData)){
            responseCpixDTO = cPixCommonModule.parseCpixData(responseData);
            logger.info("[PallyCon] GetDrmKeyInfo Response first Key : " + responseCpixDTO.getContentKey());
        }else{
            throw new CpixException(responseData);
        }
        return responseCpixDTO;

    }

    protected String getPlayReadyKeyServerUrl() {
        return this.PALLYCON_PLAYREADY_URL;
    }

    private String getStreamName(String filePath) throws NullPointerException{
        String streamName;
        String[] stream = filePath.replace('\\', '/').split("/");
        if(stream == null) {
            throw new NullPointerException("stream name is null.");
        } else if(stream.length < 1) {
            streamName = filePath;
        } else {
            streamName = stream[stream.length - 1];
            int sepaIndex = streamName.lastIndexOf(".");
            if( sepaIndex > -1){
                streamName = streamName.substring(0, sepaIndex);
            }
        }
        return streamName;
    }
    /**
     * kms서버로 부터 받아온 cpix 에서 dash 키값을 wowza에 맞게 세팅한다.
     * @param responseCpixDTO
     * @param cencInfo
     * @throws CpixException
     */
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
                cencDRMInfoWidevine.setPsshData(Base64.decode(responseCpixDTO.getBitmovinPssh(DRMSystemId.WIDEVINE)));
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

    /**
     * kms서버로 부터 받아온 cpix 에서 hls 키값을 wowza에 맞게 세팅한다.
     * @param responseCpixDTO
     * @param encInfo
     * @throws CpixException
     */
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
                encInfo.setEncUrl(new String(Base64.decode(drmSystemDTO.getUriExtXKey())));
                encInfo.setEncKeyBytes(key);
                encInfo.setEncIVBytes(iv);
                encInfo.setEncKeyFormatVersion("1");
                encInfo.setEncIVBytesInChunklist(false);
            });
        }else{
            throw new CpixException("Key informations were invalid.");
        }
    }

    public static byte[] getPSSHData( byte[] sB64KeyID, byte[] sContentID )
    {
        byte[] psshData;

        try
        {
            //new
            WidevinePSSH.WidevineCencHeader.Builder cencHeader = WidevinePSSH.WidevineCencHeader.newBuilder();

            //setAlgorithm : AESCTR(1)
            cencHeader.setAlgorithm(WidevinePSSH.WidevineCencHeader.Algorithm.valueOf(1));

            //setKeyId : base64 decoded clean data, length is must 16 byte
            ByteString bsKeyId = ByteString.copyFrom(sB64KeyID);
            cencHeader.addKeyId(bsKeyId);

            //setProvider : inkaentworks
            cencHeader.setProvider(PROVIDER_NAME);

            //setContentId : content id (optional)
            ByteString bsContentId = ByteString.copyFrom(sContentID);
            cencHeader.setContentId(bsContentId);

            //setTrackType : default "HD"
            cencHeader.setTrackType("HD");

            //base64 encoding
            psshData = cencHeader.build().toByteArray();
        }
        catch( Exception e )
        {
            String m_sErrorMsg = "[ERROR]sPSSHData Exception ==> " + e.getMessage();
            e.printStackTrace();
            //System.out.println(m_sErrorMsg);
            return null;
        }

        return psshData;
    }

}
