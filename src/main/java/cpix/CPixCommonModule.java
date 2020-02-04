package cpix;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import cpix.dto.CpixDTO;
import cpix.exception.CpixException;
import cpix.mapper.CpixNamespaceMapper;
import cpix.util.StringUtil;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by Brown on 2019-10-23.
 */
public class CPixCommonModule implements CpixModule {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final static String FPS_KEYFORMAT = "com.apple.streamingkeydelivery";
    private final static String PALLYCON_PLAYREADY_URL = "https://license.pallycon.com/ri/playready/licenseManager.do";
    private final static String KMS_PREFIX_URL= "https://kms.pallycon.com/cpix/pallycon/getKey/";

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
    public String callMethodGetDrmKeyServer(String requestUrl, Map<String, String> headerMap) throws Exception {
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
            , Map<String, String> headerMap) throws Exception {
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
     * @throws .CpixException
     */
    public String callMethodPostDrmKeyServer(String requestData, String requestUrl) throws CpixException {
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
     * @throws .CpixException
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

    public String getPlayReadyKeyServerUrl() {
        return this.PALLYCON_PLAYREADY_URL;
    }

    @Override
    public Boolean checkError(String responseData) {
        Boolean rtn = false;
        try{
            JSONParser jsonParser = new JSONParser();
            jsonParser.parse(responseData);
        }catch (Exception e){
            rtn = true;
        }
        return rtn;
    }

    /**
     * Random 된 cid와 키를 생성한다.
     * @param encToken
     * @return
     * @throws CpixException
     */
    public CpixDTO getDashKeyInfo(String encToken) throws CpixException {
        CpixDTO responseCpixDTO;
        CpixBuilder cpixBuilder = new CpixBuilder();
        CpixDTO requestCpixDTO = cpixBuilder.setWidevine()
                .setPlayReady()
                .setId(StringUtil.randomBase64String(16))
                .build();

        String responseData = callMethodPostDrmKeyServer(toCpixString(requestCpixDTO), KMS_PREFIX_URL + encToken);
        if(checkError(responseData)) {
            responseCpixDTO= parseCpixData(responseData);
        }else{
            throw new CpixException(responseData);
        }
        return responseCpixDTO;
    }

    /**
     * contentid 정보에 맞는 key정보를 가져온다.
     * @param encToken
     * @param contentId
     * @return
     * @throws CpixException
     */
    public CpixDTO getDashKeyInfo(String encToken, String contentId) throws CpixException {
        CpixDTO responseCpixDTO;
        CpixBuilder cpixBuilder = new CpixBuilder();
        CpixDTO requestCpixDTO = cpixBuilder.setWidevine()
                .setPlayReady()
                .setId(contentId)
                .build();

        String responseData = callMethodPostDrmKeyServer(toCpixString(requestCpixDTO), KMS_PREFIX_URL + encToken);
        if(checkError(responseData)) {
            responseCpixDTO= parseCpixData(responseData);
        }else{
            throw new CpixException(responseData);
        }
        return responseCpixDTO;
    }

    /**
     *
     * @param encToken PallyCon에서 발급하는 토큰
     * @param contentId
     * @param keyRotation 키로테이션 여부.
     * @param periodIndex 키로테이션시 인덱스 값. 키로테이션을 사용하지 않는 경우 세팅을 하여도 적용되지 않는다.
     * @return
     * @throws CpixException
     */
    public CpixDTO getDashKeyInfo(String encToken, String contentId, Boolean keyRotation, long periodIndex) throws CpixException {
        CpixDTO responseCpixDTO;
        CpixBuilder cpixBuilder = new CpixBuilder();
        CpixDTO requestCpixDTO = cpixBuilder.setKeyRotation(keyRotation)
                .setPeriodIndex(periodIndex)
                .setWidevine()
                .setPlayReady()
                .setId(contentId)
                .build();
        String responseData = callMethodPostDrmKeyServer(toCpixString(requestCpixDTO), KMS_PREFIX_URL + encToken);
        if(checkError(responseData)) {
            responseCpixDTO= parseCpixData(responseData);
        }else{
            throw new CpixException(responseData);
        }
        return responseCpixDTO;
    }

    public CpixDTO getHlsKeyInfo(String encToken) throws CpixException {
        CpixDTO responseCpixDTO;
        CpixBuilder cpixBuilder = new CpixBuilder();
        CpixDTO requestCpixDTO = cpixBuilder.setFairPlay()
                .setId(StringUtil.randomBase64String(16))
                .build();
        String responseData = callMethodPostDrmKeyServer(toCpixString(requestCpixDTO), KMS_PREFIX_URL + encToken);
        if(checkError(responseData)) {
            responseCpixDTO= parseCpixData(responseData);
        }else{
            throw new CpixException(responseData);
        }
        return responseCpixDTO;
    }

    public CpixDTO getHlsKeyInfo(String encToken, String contentId) throws CpixException {
        CpixDTO responseCpixDTO;
        CpixBuilder cpixBuilder = new CpixBuilder();
        CpixDTO requestCpixDTO = cpixBuilder.setFairPlay()
                .setId(contentId)
                .build();
        String responseData = callMethodPostDrmKeyServer(toCpixString(requestCpixDTO), KMS_PREFIX_URL + encToken);
        if(checkError(responseData)) {
            responseCpixDTO= parseCpixData(responseData);
        }else{
            throw new CpixException(responseData);
        }
        return responseCpixDTO;
    }

    public CpixDTO getHlsKeyInfo(String encToken, String contentId, Boolean keyRotation, long periodIndex) throws CpixException {
        CpixDTO responseCpixDTO;
        CpixBuilder cpixBuilder = new CpixBuilder();
        CpixDTO requestCpixDTO = cpixBuilder.setKeyRotation(keyRotation)
                .setPeriodIndex(periodIndex)
                .setFairPlay()
                .setId(contentId)
                .build();
        String responseData = callMethodPostDrmKeyServer(toCpixString(requestCpixDTO), KMS_PREFIX_URL + encToken);
        if(checkError(responseData)) {
            responseCpixDTO= parseCpixData(responseData);
        }else{
            throw new CpixException(responseData);
        }
        return responseCpixDTO;
    }

    /**
     * DTO to CPIX format string
     * @param cpixDTO
     * @return cpix String
     * @throws CpixException
     */
    public static String toCpixString(CpixDTO cpixDTO) throws CpixException {
        StringWriter sw = new StringWriter();
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(CpixDTO.class);
            Marshaller m = context.createMarshaller();
            //xml header 추가
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML
//            m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new CpixNamespaceMapper());
            m.marshal(cpixDTO, sw);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new CpixException("Response CPIX Data make failed.");
        }
        System.out.println("toCpixString : " + sw.toString());
        return sw.toString();
    }
}
