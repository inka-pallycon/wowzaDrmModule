package cpix;

import cpix.dto.*;
import cpix.exception.CpixException;
import com.wowza.wms.drm.cenc.CencInfo;
import com.wowza.wms.httpstreamer.cupertinostreaming.util.CupertinoEncInfo;
import org.json.simple.parser.JSONParser;


/**
 * Created by Brown on 2019-02-08.
 */
public class PallyConModule extends WowzaModule{
    private final static String PALLYCON_PLAYREADY_URL = "https://license.pallycon.com/ri/playready/licenseManager.do";

    @Override
    public String getDashKeyInfo(String streamPath, CencInfo cencInfo
                                 , String requestUrl, CpixDTO cpixDTO) throws CpixException {
        String contentId= getStreamName(streamPath);
        cpixDTO.setId(contentId);

        String responseData = callMethodPostDrmKeyServer(toCpixString(cpixDTO), requestUrl);
        return responseData;
    }

    @Override
    public String getHlsEncKeyInfo(String streamPath, CupertinoEncInfo encInfo
            , String requestUrl, CpixDTO cpixDTO) throws CpixException{
        String contentId= getStreamName(streamPath);
        cpixDTO.setId(contentId);

        String responseData = callMethodPostDrmKeyServer(toCpixString(cpixDTO), requestUrl);

        return responseData;
    }

    @Override
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
        }
        return streamName;
    }

    /**
     * The pallycon service returns a json string when an error occurs.
     * @param responseData
     * @return
     */
    @Override
    public Boolean checkError(String responseData){
        Boolean rtn = false;
        try{
            JSONParser jsonParser = new JSONParser();
            jsonParser.parse(responseData);
        }catch (Exception e){
            rtn = true;
        }
        return rtn;
    }
}
