package cpix;

import cpix.dto.*;
import cpix.mapper.CpixNamespaceMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import cpix.exception.CpixException;
import com.wowza.util.Base64;
import com.wowza.wms.drm.cenc.CencDRMInfoPlayready;
import com.wowza.wms.drm.cenc.CencDRMInfoWidevine;
import com.wowza.wms.drm.cenc.CencInfo;
import com.wowza.wms.drm.playready.PlayReadyKeyInfo;
import com.wowza.wms.httpstreamer.cupertinostreaming.util.CupertinoEncInfo;
import cpix.util.StringUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Brown on 2019-02-08.
 */
public abstract class CpixAbstractModule implements CpixModule {
    protected final static String FPS_KEYFORMAT = "com.apple.streamingkeydelivery";

    public abstract String getDashKeyInfo(String streamPath, CencInfo cencInfo, String requestUrl, CpixDTO cpixDTO) throws CpixException;
    public abstract String getHlsEncKeyInfo(String streamPath, CupertinoEncInfo encInfo, String requestUrl, CpixDTO cpixDTO) throws CpixException;
    public abstract Boolean checkError(String responseData);
    protected abstract String getPlayReadyKeyServerUrl();
    /**
     *
     * @param requestUrl
     * @param headerMap
     * @param parameterMap
     * @return
     * @throws UnirestException
     */
    public String callMethodGetDrmKeyServer(String requestUrl, Map<String, String> headerMap, Map<String, Object> parameterMap) throws UnirestException{
        HttpResponse<String> responseData = Unirest.get(requestUrl)
                .headers(headerMap)
                .queryString(parameterMap)
                .asString();
        return responseData.getBody();
    }

    /**
     *
     * @param requestUrl
     * @param headerMap
     * @return
     * @throws Exception
     */
    public String callMethodGetDrmKeyServer(String requestUrl, Map<String, String> headerMap) throws Exception{
        HttpResponse<String> responseData = Unirest.get(requestUrl)
                .headers(headerMap)
                .asString();
        return responseData.getBody();
    }

    /**
     *
     * @param requestData
     * @param requestUrl
     * @param headerMap
     * @return
     * @throws Exception
     */
    public String callMethodPostDrmKeyServer(String requestData, String requestUrl
            , Map<String, String> headerMap) throws Exception{
        HttpResponse<String> responseData = Unirest.post(requestUrl)
                .headers(headerMap)
                .body(requestData).asString();
        return responseData.getBody();
    }

    /**
     *
     * @param requestData
     * @param requestUrl
     * @return
     * @throws CpixException
     */
    public String callMethodPostDrmKeyServer(String requestData, String requestUrl) throws CpixException{
        HttpResponse<String> responseData = null;
        try {
            responseData = Unirest.post(requestUrl)
                    .body(requestData).asString();
        } catch (UnirestException e) {
            e.printStackTrace();
            throw new CpixException("Drm Key Server call failed.");
        }
        return responseData.getBody();
    }

    /**
     *
     * @param cpixData
     * @return
     * @throws CpixException
     */
    public CpixDTO parseCpixData(String cpixData) throws CpixException {
        CpixDTO cpixDTO;
        try {
            StringReader stringReader = new StringReader(cpixData);
            JAXBContext jc = JAXBContext.newInstance(CpixDTO.class);
            XMLInputFactory xif = XMLInputFactory.newInstance();
            XMLStreamReader xsr = xif.createXMLStreamReader(stringReader);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            cpixDTO = (CpixDTO) unmarshaller.unmarshal(xsr);
        }catch (Exception e){
            throw new CpixException("Response CPIX Data Parsing Failed.");
        }
        return cpixDTO;
    }

    /**
     *
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

    public void createRandomContentKey(ContentKeyDTO contentKeyDTO){
        DataDTO dataDTO = new DataDTO();
        SecretDTO secretDTO = new SecretDTO();
        secretDTO.setPlainValue(StringUtil.randomBase64String(16));
        dataDTO.setSecret(secretDTO);
        contentKeyDTO.setData(dataDTO);
        contentKeyDTO.setExplicitIV(StringUtil.randomBase64String(16));
    }

    /**
     * DTO to CPIX format string
     * @param cpixDTO
     * @return cpix String
     * @throws CpixException
     */
    public static String toCpixString(CpixDTO cpixDTO) throws CpixException{
        StringWriter sw = new StringWriter();
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(CpixDTO.class);
            Marshaller m = context.createMarshaller();
            //xml header 추가
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML
//            m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            m.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new CpixNamespaceMapper());
            m.marshal(cpixDTO, sw);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new CpixException("Response CPIX Data make failed.");
        }
        System.out.println(sw.toString());
        return sw.toString();
    }

}
