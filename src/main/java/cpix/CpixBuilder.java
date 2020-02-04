package cpix;

import cpix.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Brown on 2019-02-11.
 */
public class CpixBuilder {
    private final static String PREFIX_KEY_PERIOD_ID = "keyPeriod_";

    private String kId;
    private String id = null;
    private boolean keyRotation = false;
    private List<ContentKeyDTO> contentKeyList;
    private List<DrmSystemDTO> drmSystemList;
    private long periodIndex = 0L;

    public CpixBuilder(){
        this.contentKeyList = new ArrayList<>();
        this.drmSystemList = new ArrayList<>();
        this.kId = UUID.randomUUID().toString();
        this.contentKeyList.add(new ContentKeyDTO(this.kId));
    }
    public CpixBuilder addContentKey(ContentKeyDTO contentKeyDTO) {
        this.contentKeyList.add(contentKeyDTO);
        return this;
    }
    public CpixBuilder addDrmSystemList(DrmSystemDTO drmSystemDTO){
        this.drmSystemList.add(drmSystemDTO);
        return this;
    }

    public CpixBuilder setKeyRotation(boolean keyrotation) {
        this.keyRotation = keyRotation;
        return this;
    }

    public CpixBuilder setPeriodIndex(Long index){
        this.periodIndex= index;
        return this;
    }

    public CpixBuilder setWidevine() {
        DrmSystemDTO drmSystemDTO = new DrmSystemDTO(this.kId, DRMSystemId.WIDEVINE);
        this.drmSystemList.add(drmSystemDTO);
        return this;
    }

    public CpixBuilder setPlayReady() {
        DrmSystemDTO drmSystemDTO = new DrmSystemDTO(this.kId, DRMSystemId.PLAYREADY);
        this.drmSystemList.add(drmSystemDTO);
        return this;
    }

    public CpixBuilder setFairPlay() {
        DrmSystemDTO drmSystemDTO = new DrmSystemDTO(this.kId, DRMSystemId.FAIRPLAY);
        this.drmSystemList.add(drmSystemDTO);
        return this;
    }

    public CpixBuilder setHlsAes128(){
        DrmSystemDTO drmSystemDTO = new DrmSystemDTO(this.kId, DRMSystemId.HLSAES);
        this.drmSystemList.add(drmSystemDTO);
        return this;
    }
    public CpixBuilder setId(String id){
        this.id = id;
        return this;
    }

    public CpixDTO build(){
        CpixDTO cpixDTO = new CpixDTO();
        cpixDTO.setContentKeyList(this.contentKeyList);
        cpixDTO.setDrmSystemList(this.drmSystemList);
        cpixDTO.setId(this.id);
        if(this.keyRotation){
            List<ContentKeyPeriodDTO> contentKeyPeriodList = new ArrayList<>();
            ContentKeyPeriodDTO contentKeyPeriodDTO = new ContentKeyPeriodDTO(this.PREFIX_KEY_PERIOD_ID + this.kId);
            contentKeyPeriodList.add(contentKeyPeriodDTO);

            List<ContentKeyUsageRuleDTO> contentKeyUsageRuleList = new ArrayList<>();
            ContentKeyUsageRuleDTO contentKeyUsageRuleDTO = new ContentKeyUsageRuleDTO(this.kId);
            List<KeyPeriodFilterDTO> keyPeriodFilterDTOList = new ArrayList<>();
            KeyPeriodFilterDTO keyPeriodFilterDTO = new KeyPeriodFilterDTO(this.PREFIX_KEY_PERIOD_ID + this.kId);

            keyPeriodFilterDTOList.add(keyPeriodFilterDTO);
            contentKeyUsageRuleDTO.setKeyPeriodFilter(keyPeriodFilterDTOList);
            contentKeyUsageRuleList.add(contentKeyUsageRuleDTO);

            cpixDTO.setContentKeyPeriodList(contentKeyPeriodList);
            cpixDTO.setContentKeyUsageRuleList(contentKeyUsageRuleList);

            if(this.periodIndex > 0 ){
                contentKeyPeriodList.stream()
                    .filter( keyPeriodDTO -> keyPeriodDTO.getId().equals(this.PREFIX_KEY_PERIOD_ID+this.kId))
                    .findFirst()
                    .ifPresent(
                        keyPeriodDTO -> keyPeriodDTO.setIndex(Long.toString(periodIndex)
                    )
                );
            }
        }

        return cpixDTO;
    }

    public String getkId() {
        return kId;
    }

    public void setkId(String kId) {
        this.kId = kId;
    }
}
