package com.pallycon.wowza;

import com.pallycon.cpix.CpixBuilder;
import com.pallycon.cpix.dto.CpixDTO;
import com.pallycon.cpix.exception.CpixException;
import com.pallycon.wowza.session.SessionChecker;
import com.wowza.util.Base64;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.drm.cenc.CencDRMInfoPlayready;
import com.wowza.wms.drm.cenc.CencDRMInfoWidevine;
import com.wowza.wms.drm.cenc.CencInfo;
import com.wowza.wms.drm.playready.PlayReadyKeyInfo;
import com.wowza.wms.httpstreamer.cupertinostreaming.file.IHTTPStreamerCupertinoIndex;
import com.wowza.wms.httpstreamer.cupertinostreaming.httpstreamer.HTTPStreamerSessionCupertino;
import com.wowza.wms.httpstreamer.cupertinostreaming.util.CupertinoEncInfo;
import com.wowza.wms.httpstreamer.model.IHTTPStreamerSession;
import com.wowza.wms.httpstreamer.mpegdashstreaming.file.IHTTPStreamerMPEGDashIndex;
import com.wowza.wms.httpstreamer.mpegdashstreaming.httpstreamer.HTTPStreamerSessionMPEGDash;
import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.stream.IMediaStream;
import com.wowza.wms.stream.livepacketizer.ILiveStreamPacketizer;

import java.util.Arrays;
import java.util.Random;

/**
 * Wowza Dash CENC Sample Class
 * Created by Brown on 2019-02-08.
 */
public class DrmModule extends ModuleBase {
    private String requestUrl;
    private Boolean keyrotation = false;
    private String pallyconEncToken;

    public void onAppStart(IApplicationInstance appInstance){
        String fullName = appInstance.getApplication().getName() + "/" + appInstance.getName();
        getLogger().info("[PallyCon] onAppStart: " + fullName);

        //Only PallyCon Service Use.
        //an API authentication token that is generated when you sign up PallyCon service, and can be found on the PallyCon Console site.
        this.requestUrl = appInstance.getProperties().getPropertyStr("KmsUrl");
        getLogger().info("[PallyCon] [On App Start] requestUrl : " + this.requestUrl);

        this.pallyconEncToken = appInstance.getProperties().getPropertyStr("PallyConEncToken");
        getLogger().info("[PallyCon] [On App Start] pallyconEncToken : " + this.pallyconEncToken);

        this.keyrotation = appInstance.getProperties().getPropertyBoolean("KeyRotation", false) ;
        getLogger().info("[PallyCon] [On App Start] keyrotation : " + this.keyrotation);
    }

    public void onAppStop(IApplicationInstance appInstance) {
        String fullName = appInstance.getApplication().getName() + "/" + appInstance.getName();
        getLogger().info("[PallyCon] [On App Stop] pallyconEncToken : " + fullName);
    }

    public void onStreamCreate(IMediaStream stream) {
        getLogger().info("onStreamCreate: " + stream.getSrc());
        getLogger().info( "onStreamCreate getName : " + stream.getName());
        getLogger().info( "onStreamCreate getClientId : " + stream.getClientId());
    }

    public void onHTTPSessionDestroy(IHTTPStreamerSession httpSession) {
        getLogger().info("onHTTPSessionDestroy: " + httpSession.getSessionId());
        SessionChecker.getInstance(getLogger()).removeSession(httpSession.getSessionId());
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
            getLogger().error(e.getMessage(), e);
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
        WMSLogger wmsLogger = getLogger();
        CpixDTO responseCpixDTO;
        CpixBuilder cpixBuilder = new CpixBuilder();

        CpixDTO requestCpix = cpixBuilder.setWidevine()
                                            .setPlayReady()
                                            .build();

        WowzaModule wowzaModule = new PallyConModule();
        try {
            String sessionId = httpSession.getSessionId();
            if(!SessionChecker.getInstance(getLogger()).isValid(sessionId)) {
                //키를 가져온다
                responseCpixDTO = wowzaModule.getDrmKeyInfo(httpSession.getStreamName()
                        , requestUrl + "/" + pallyconEncToken, requestCpix);

                SessionChecker.getInstance(getLogger()).setSession(sessionId, responseCpixDTO);
            }else{

                responseCpixDTO = SessionChecker.getInstance(getLogger()).getSession(sessionId);
            }
            //키를 세팅한다.
            wowzaModule.setDashKeyInfo(responseCpixDTO, cencInfo);

        } catch (CpixException e) {
            getLogger().error(e.getMessage());
            wmsLogger.error("[PallyCon] create Random Key");
            createRandomKeyInfo(cencInfo);
        }
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
        CpixDTO responseCpixDTO;
        CpixBuilder cpixBuilder = new CpixBuilder();
        // HLS AES-128
        CpixDTO requestCpix = cpixBuilder.setKeyRotation(this.keyrotation).setFairPlay().build();

        WowzaModule wowzaModule = new PallyConModule();

        try {
            String sessionId = httpSession.getSessionId();
            if(!SessionChecker.getInstance(getLogger()).isValid(sessionId)) {
                //키를 가져온다.
                responseCpixDTO = wowzaModule.getDrmKeyInfo( httpSession.getStreamName()
                        , requestUrl + "/" + pallyconEncToken, requestCpix);

                SessionChecker.getInstance(getLogger()).setSession(sessionId, responseCpixDTO);
            }else{
                responseCpixDTO = SessionChecker.getInstance(getLogger()).getSession(sessionId);
            }

            getLogger().info(responseCpixDTO.getId());
            //키를 세팅한다.
            wowzaModule.setHlsKeyInfo(responseCpixDTO, encInfo);

        } catch (CpixException e) {
            getLogger().error(e.getMessage());
            getLogger().error("[PallyCon] create Random Key");
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
        CpixDTO responseCpixDTO;
        CpixBuilder cpixBuilder = new CpixBuilder();
        // FairPlay
        CpixDTO requestCpix = cpixBuilder.setKeyRotation(this.keyrotation).setFairPlay().build();
        WowzaModule wowzaModule = new PallyConModule();
        try {
            responseCpixDTO = wowzaModule.getDrmKeyInfo( streamName
                    , requestUrl + "/" + pallyconEncToken, requestCpix);
            wowzaModule.setHlsKeyInfo(responseCpixDTO, encInfo);

        } catch (CpixException e) {
            getLogger().error(e.getMessage());
            getLogger().error("[PallyCon] create Random Key");
            createErrorRandomKey(encInfo, cpixBuilder);
        }
    }
    private void createErrorRandomKey(CupertinoEncInfo encInfo, CpixBuilder cpixBuilder){
        byte[] key = new byte[16];
        new Random().nextBytes(key);
        byte[] iv = new byte[16];
        new Random().nextBytes(iv);
        String extXKey = "skd://" + Base64.encodeBytes(cpixBuilder.getkId().getBytes());
        getLogger().info("createRandomKey extXKey : " + extXKey);
        encInfo.setEncMethod(CupertinoEncInfo.METHOD_SAMPLE_AES);
        encInfo.setEncKeyFormat("com.apple.streamingkeydelivery");
        encInfo.setEncUrl(extXKey);
        encInfo.setEncKeyBytes(key);
        encInfo.setEncIVBytes(iv);
        encInfo.setEncKeyFormatVersion("1");
        encInfo.setEncIVBytesInChunklist(false);

    }

    private void createRandomKeyInfo(CencInfo cencInfo){
        byte[] block = new byte[16];
        new Random().nextBytes(block);
        CencDRMInfoWidevine cencDRMInfoWidevine = new CencDRMInfoWidevine();
        cencDRMInfoWidevine.setPsshData(PallyConModule.getPSSHData(block, "errorCid".getBytes()));
        getLogger().info("errorPSSH : " + new String(cencDRMInfoWidevine.getPsshData(true)));
        cencInfo.addDRM("myWidevineDRM:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed", cencDRMInfoWidevine);
        // add PlayReady DRM info
        CencDRMInfoPlayready cencDRMInfoPlayready = new CencDRMInfoPlayready();
        PlayReadyKeyInfo playReadyKeyInfo = new PlayReadyKeyInfo();
        playReadyKeyInfo.setLicenseURL(PallyConModule.PALLYCON_PLAYREADY_URL);
        playReadyKeyInfo.setKeyId(block);
        playReadyKeyInfo.setContentKey(block);
        playReadyKeyInfo.generateChecksum();
        cencDRMInfoPlayready.setPlayReadyKeyInfo(playReadyKeyInfo);
        cencInfo.addDRM("myPlayReadyDRM:9a04f079-9840-4286-ab92-e65be0885f95", cencDRMInfoPlayready);
        getLogger().info("[PallyCon] createRandomKey END");
    }


}
