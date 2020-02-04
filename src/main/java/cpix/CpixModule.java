package cpix;

import com.mashape.unirest.http.exceptions.UnirestException;
import cpix.dto.CpixDTO;
import cpix.exception.CpixException;

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
    String getPlayReadyKeyServerUrl();
    CpixDTO parseCpixData(String cpixData) throws CpixException;
    CpixDTO getDashKeyInfo(String encToken) throws CpixException;
    CpixDTO getDashKeyInfo(String encToken, String contentId) throws CpixException;
    CpixDTO getDashKeyInfo(String encToken, String contentId, Boolean keyRotation, long periodIndex) throws CpixException;
    CpixDTO getHlsKeyInfo(String encToken) throws CpixException;
    CpixDTO getHlsKeyInfo(String encToken, String contentId) throws CpixException;
    CpixDTO getHlsKeyInfo(String encToken, String contentId, Boolean keyRotation, long periodIndex) throws CpixException;

}
