package cpix;

import cpix.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Brown on 2019-02-11.
 */
public class CpixBuilder {
    private CpixDTO cpixDTO;
    private String kId;
    private final static String PREFIX_KEY_PERIOD_ID = "keyPeriod_";

    public CpixBuilder(final Boolean keyrotation){
        List<ContentKeyDTO> contentKeyList = new ArrayList<>();
        List<DrmSystemDTO> drmSystemList = new ArrayList<>();
        this.cpixDTO = new CpixDTO();
        this.kId = UUID.randomUUID().toString();
        contentKeyList.add(new ContentKeyDTO(this.kId));
        this.cpixDTO.setContentKeyList(contentKeyList);
        this.cpixDTO.setDrmSystemList(drmSystemList);
        if(keyrotation){
            List<ContentKeyPeriodDTO> contentKeyPeriodList = new ArrayList<>();
            ContentKeyPeriodDTO contentKeyPeriodDTO = new ContentKeyPeriodDTO(PREFIX_KEY_PERIOD_ID + this.kId);
            contentKeyPeriodList.add(contentKeyPeriodDTO);

            List<ContentKeyUsageRuleDTO> contentKeyUsageRuleList = new ArrayList<>();
            ContentKeyUsageRuleDTO contentKeyUsageRuleDTO = new ContentKeyUsageRuleDTO(this.kId);
            List<KeyPeriodFilterDTO> keyPeriodFilterDTOList = new ArrayList<>();
            KeyPeriodFilterDTO keyPeriodFilterDTO = new KeyPeriodFilterDTO(PREFIX_KEY_PERIOD_ID + this.kId);

            keyPeriodFilterDTOList.add(keyPeriodFilterDTO);
            contentKeyUsageRuleDTO.setKeyPeriodFilter(keyPeriodFilterDTOList);
            contentKeyUsageRuleList.add(contentKeyUsageRuleDTO);

            this.cpixDTO.setContentKeyPeriodList(contentKeyPeriodList);
            this.cpixDTO.setContentKeyUsageRuleList(contentKeyUsageRuleList);
        }
    }
    public CpixBuilder addContentKey(ContentKeyDTO contentKeyDTO) {
        this.cpixDTO.getContentKeyList().add(contentKeyDTO);
        return this;
    }
    public CpixBuilder addDrmSystemList(DrmSystemDTO drmSystemDTO){
        this.cpixDTO.getDrmSystemList().add(drmSystemDTO);
        return this;
    }

    public CpixBuilder setPeriodIndex(Long index){
        List<ContentKeyPeriodDTO> contentKeyPeriodDTOList = this.cpixDTO.getContentKeyPeriodList();
        contentKeyPeriodDTOList.stream()
                .filter( contentKeyPeriodDTO -> contentKeyPeriodDTO.getId().equals(this.PREFIX_KEY_PERIOD_ID+this.kId))
                .findFirst()
                .ifPresent(
                    contentKeyPeriodDTO -> contentKeyPeriodDTO.setIndex(Long.toString(index))
                );
        return this;
    }

    public CpixBuilder setWidevine() {
        DrmSystemDTO drmSystemDTO = new DrmSystemDTO(this.kId, DRMSystemId.WIDEVINE);
        this.cpixDTO.getDrmSystemList().add(drmSystemDTO);
        return this;
    }

    public CpixBuilder setPlayReady() {
        DrmSystemDTO drmSystemDTO = new DrmSystemDTO(this.kId, DRMSystemId.PLAYREADY);
        this.cpixDTO.getDrmSystemList().add(drmSystemDTO);
        return this;
    }

    public CpixBuilder setFairPlay() {
        DrmSystemDTO drmSystemDTO = new DrmSystemDTO(this.kId, DRMSystemId.FAIRPLAY);
        this.cpixDTO.getDrmSystemList().add(drmSystemDTO);
        return this;
    }

    public CpixBuilder setHlsAes128(){
        DrmSystemDTO drmSystemDTO = new DrmSystemDTO(this.kId, DRMSystemId.HLSAES);
        this.cpixDTO.getDrmSystemList().add(drmSystemDTO);
        return this;
    }

    public CpixDTO getCpixDTO() {
        return cpixDTO;
    }

    public void setCpixDTO(CpixDTO cpixDTO) {
        this.cpixDTO = cpixDTO;
    }

    public String getkId() {
        return kId;
    }

    public void setkId(String kId) {
        this.kId = kId;
    }
}
