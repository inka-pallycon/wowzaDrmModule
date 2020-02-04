package cpix.wowza;

import com.wowza.wms.drm.cenc.CencDRMInfoPlayready;
import com.wowza.wms.drm.cenc.CencDRMInfoWidevine;
import com.wowza.wms.drm.playready.PlayReadyKeyInfo;
import cpix.CPixCommonModule;
import cpix.CpixBuilder;
import cpix.CpixModule;
import cpix.dto.CpixDTO;
import cpix.exception.CpixException;
import cpix.session.SessionChecker;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.drm.cenc.CencInfo;
import com.wowza.wms.httpstreamer.mpegdashstreaming.file.IHTTPStreamerMPEGDashIndex;
import com.wowza.wms.httpstreamer.mpegdashstreaming.httpstreamer.HTTPStreamerSessionMPEGDash;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.stream.livepacketizer.ILiveStreamPacketizer;
import java.util.Random;

/**
 * Wowza Dash CENC Sample Class
 * Created by Brown on 2019-02-08.
 */
public class DashCenc extends ModuleBase {
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
    public void onHTTPMPEGDashEncryptionKeyLiveChunk(ILiveStreamPacketizer liveStreamPacketizer, String streamName, CencInfo cencInfo, long chunkId)
    {
        CpixBuilder cpixBuilder = new CpixBuilder();
        CpixDTO requestCpix = cpixBuilder.setPeriodIndex(chunkId)
                                            .setWidevine()
                                            .setPlayReady()
                                            .build();
        WowzaModule wowzaModule = new PallyConModule();
        try {
            CpixDTO responseCpixDTO = wowzaModule.getDrmKeyInfo(streamName
                    , requestUrl + "/" + pallyconEncToken, requestCpix);
            wowzaModule.setDashKeyInfo(responseCpixDTO, cencInfo);
        } catch (CpixException e) {
            getLogger().error(e.toString());
            //TODO
            // If an error occurs, must be set random key.
            // Otherwise, it will be the original stream.
            createRandomKeyInfo(cencInfo);
        }
    }

    /**
     * MPEG-Dash VOD
     * @param httpSession
     * @param index
     * @param cencInfo
     * @param chunkId
     */
    public void onHTTPMPEGDashEncryptionKeyVODChunk(HTTPStreamerSessionMPEGDash httpSession
            , IHTTPStreamerMPEGDashIndex index, CencInfo cencInfo, long chunkId) {
        CpixDTO responseCpixDTO;
        CpixBuilder cpixBuilder = new CpixBuilder();

        CpixDTO requestCpix = cpixBuilder.setWidevine()
                                            .setPlayReady()
                                            .build();

        WowzaModule wowzaModule = new PallyConModule();
        try {
            String sessionId = httpSession.getSessionId();
            if(!SessionChecker.getInstance(getLogger()).isValid(sessionId)) {
                responseCpixDTO = wowzaModule.getDrmKeyInfo(httpSession.getStreamName()
                        , requestUrl + "/" + pallyconEncToken, requestCpix);
                wowzaModule.setDashKeyInfo(responseCpixDTO, cencInfo);

                SessionChecker.getInstance(getLogger()).setSession(sessionId, responseCpixDTO);
            }else{
                responseCpixDTO = SessionChecker.getInstance(getLogger()).getSession(sessionId);
            }
            getLogger().info(responseCpixDTO.getId());
            wowzaModule.setDashKeyInfo(responseCpixDTO, cencInfo);

        } catch (CpixException e) {
            getLogger().error(e.toString());
            //TODO
            // If an error occurs, must be set random key.
            // Otherwise, it will be the original stream.
            createRandomKeyInfo(cencInfo);
        }
    }

    private void createRandomKeyInfo(CencInfo cencInfo){
        byte[] block = new byte[16];
        new Random().nextBytes(block);
        CencDRMInfoWidevine cencDRMInfoWidevine = new CencDRMInfoWidevine();
        cencDRMInfoWidevine.setPsshData(block);
        cencInfo.addDRM("myWidevineDRM:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed", cencDRMInfoWidevine);
        // add PlayReady DRM info
        CencDRMInfoPlayready cencDRMInfoPlayready = new CencDRMInfoPlayready();
        PlayReadyKeyInfo playReadyKeyInfo = new PlayReadyKeyInfo();
        playReadyKeyInfo.setLicenseURL(this.requestUrl);
        playReadyKeyInfo.setKeyId(block);
        playReadyKeyInfo.setContentKey(block);
        playReadyKeyInfo.generateChecksum();
        cencDRMInfoPlayready.setPlayReadyKeyInfo(playReadyKeyInfo);
        cencInfo.addDRM("myPlayReadyDRM:9a04f079-9840-4286-ab92-e65be0885f95", cencDRMInfoPlayready);
    }


}
