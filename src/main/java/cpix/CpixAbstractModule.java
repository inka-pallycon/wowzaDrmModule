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
    protected abstract String getPlayReadyKeyServerUrl();

    public abstract Boolean checkError(String responseData);
    public abstract String getDashKeyInfo(String streamPath, CencInfo cencInfo, String requestUrl, CpixDTO cpixDTO) throws CpixException;
    public abstract String getHlsEncKeyInfo(String streamPath, CupertinoEncInfo encInfo, String requestUrl, CpixDTO cpixDTO) throws CpixException;
    public abstract void setDashKeyInfo(CpixDTO responseCpixDTO, CencInfo cencInfo) throws CpixException;
    public abstract void setHlsKeyInfo(CpixDTO responseCpixDTO, CupertinoEncInfo encInfo) throws CpixException;


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
