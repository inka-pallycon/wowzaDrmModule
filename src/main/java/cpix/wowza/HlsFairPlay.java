package cpix.wowza;

import com.wowza.util.Base64;
import cpix.CpixBuilder;

import cpix.CpixModule;
import cpix.PallyConModule;
import cpix.dto.CpixDTO;
import cpix.exception.CpixException;
import cpix.session.SessionChecker;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.httpstreamer.cupertinostreaming.file.IHTTPStreamerCupertinoIndex;
import com.wowza.wms.httpstreamer.cupertinostreaming.httpstreamer.HTTPStreamerSessionCupertino;
import com.wowza.wms.httpstreamer.cupertinostreaming.util.CupertinoEncInfo;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.stream.livepacketizer.ILiveStreamPacketizer;

import java.util.Random;

/**
 * Wowza HLS FairPlay Sample Class
 * Created by Brown on 2019-02-08.
 */
public class HlsFairPlay extends ModuleBase {
    private String requestUrl;
    private Boolean keyrotation = false;
    private String pallyconEncToken;

    public void onAppStart(IApplicationInstance appInstance){
        this.requestUrl = (String)appInstance.getProperties().getProperty("RequestUrl");
        if( null != appInstance.getProperties().getProperty("KeyRotation")){
            this.keyrotation = (Boolean)appInstance.getProperties().getProperty("KeyRotation") ;
        }
        //Only PallyCon Service Use.
        //an API authentication token that is generated when you sign up PallyCon service, and can be found on the PallyCon Console site.
        this.pallyconEncToken = (String)appInstance.getProperties().getProperty("PallyConEncToken");
    }
    /**
     * https://www.wowza.com/docs/how-to-secure-apple-hls-streaming-using-drm-encryption
     * @param httpSession
     * @param index
     * @param encInfo
     * @param chunkId
     * @param mode
     */
    public void onHTTPCupertinoEncryptionKeyVODChunk(HTTPStreamerSessionCupertino httpSession, IHTTPStreamerCupertinoIndex index, CupertinoEncInfo encInfo, long chunkId, int mode){
        String apiResponseData;
        CpixBuilder cpixBuilder = new CpixBuilder();
        // HLS AES-128
        CpixDTO requestCpix = cpixBuilder.setHlsAes128().build();
        CpixModule cpixModule = new PallyConModule();

        try {
            String sessionId = httpSession.getSessionId();
            if(!SessionChecker.getInstance(getLogger()).isValid(sessionId)) {
                apiResponseData = cpixModule.getHlsEncKeyInfo( httpSession.getStreamName(), encInfo
                        , requestUrl + pallyconEncToken, requestCpix);

                SessionChecker.getInstance(getLogger()).setSession(sessionId, apiResponseData);
            }else{
                apiResponseData = SessionChecker.getInstance(getLogger()).getSession(sessionId);
            }
            getLogger().info(apiResponseData);

            if(cpixModule.checkError(apiResponseData)){
                CpixDTO responseCpixDTO = cpixModule.parseCpixData(apiResponseData);
                cpixModule.setHlsKeyInfo(responseCpixDTO, encInfo);
            }else{
                throw new CpixException(apiResponseData);
            }
        } catch (CpixException e) {
            getLogger().error(e.toString());
            //TODO
            // If an error occurs, must be set random key.
            // Otherwise, it will be the original stream.
            createErrorRandomKey(encInfo, cpixBuilder);
        }
    }

    /**
     * https://www.wowza.com/docs/how-to-secure-apple-hls-streaming-using-drm-encryption
     * @param liveStreamPacketizer
     * @param streamName
     * @param encInfo
     * @param chunkId
     * @param mode
     */
    public void onHTTPCupertinoEncryptionKeyLiveChunk(ILiveStreamPacketizer liveStreamPacketizer, String streamName, CupertinoEncInfo encInfo, long chunkId, int mode){
        CpixBuilder cpixBuilder = new CpixBuilder();
        // FairPlay
        CpixDTO requestCpix = cpixBuilder.setKeyRotation(this.keyrotation).setFairPlay().build();
        CpixModule cpixModule = new PallyConModule();
        try {
            String apiResponseData = cpixModule.getHlsEncKeyInfo( streamName, encInfo
                    , this.requestUrl+this.pallyconEncToken, requestCpix);
            if(cpixModule.checkError(apiResponseData)){
                CpixDTO responseCpixDTO = cpixModule.parseCpixData(apiResponseData);
                cpixModule.setHlsKeyInfo(responseCpixDTO, encInfo);
            }else{
                throw new CpixException(apiResponseData);
            }
        } catch (CpixException e) {
            getLogger().error(e.toString());
            //TODO
            // If an error occurs, must be set random key.
            // Otherwise, it will be the original stream.
            createErrorRandomKey(encInfo, cpixBuilder);
        }
    }
    private void createErrorRandomKey(CupertinoEncInfo encInfo, CpixBuilder cpixBuilder){
        byte[] key = new byte[16];
        new Random().nextBytes(key);
        byte[] iv = new byte[16];
        new Random().nextBytes(iv);
        String extXKey = "skd://" + Base64.encodeBytes(cpixBuilder.getkId().getBytes());
        encInfo.setEncMethod(CupertinoEncInfo.METHOD_SAMPLE_AES);
        encInfo.setEncKeyFormat("com.apple.streamingkeydelivery");
        encInfo.setEncUrl(Base64.encodeBytes(extXKey.getBytes()));
        encInfo.setEncKeyBytes(key);
        encInfo.setEncIVBytes(iv);
        encInfo.setEncKeyFormatVersion("1");
        encInfo.setEncIVBytesInChunklist(false);
    }
}
