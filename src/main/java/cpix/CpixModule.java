package cpix;

import cpix.dto.ContentKeyDTO;
import cpix.dto.CpixDTO;
import com.mashape.unirest.http.exceptions.UnirestException;
import cpix.dto.DrmSystemDTO;
import cpix.exception.CpixException;
import com.wowza.wms.drm.cenc.CencInfo;
import com.wowza.wms.httpstreamer.cupertinostreaming.util.CupertinoEncInfo;

import java.util.Map;

/**
 * Created by brown on 2019-02-01.
 */
public interface CpixModule {

    String callMethodGetDrmKeyServer(String requestUrl, Map<String, String> headerMap, Map<String, Object> parameterMap) throws UnirestException;
    String callMethodGetDrmKeyServer(String requestUrl, Map<String, String> headerMap) throws Exception;
    String callMethodPostDrmKeyServer(String requestData, String requestUrl, Map<String, String> headerMap) throws Exception;
    String callMethodPostDrmKeyServer(String requestData, String requestUrl) throws CpixException;
    Boolean checkError(String responseData);
    void createRandomContentKey(ContentKeyDTO contentKeyDTO);
    String getDashKeyInfo(String streamPath, CencInfo cencInfo, String requestUrl, CpixDTO cpixDTO) throws CpixException;
    String getHlsEncKeyInfo(String streamPath, CupertinoEncInfo encInfo, String requestUrl, CpixDTO cpixDTO) throws CpixException;
    CpixDTO parseCpixData(String cpixData) throws CpixException;
    void setDashKeyInfo(CpixDTO responseCpixDTO, CencInfo cencInfo) throws CpixException;
    void setHlsKeyInfo(CpixDTO responseCpixDTO, CupertinoEncInfo encInfo) throws CpixException;


}
