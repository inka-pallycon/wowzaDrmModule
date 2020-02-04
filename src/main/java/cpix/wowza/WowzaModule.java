package cpix.wowza;

import com.wowza.wms.drm.cenc.CencInfo;
import com.wowza.wms.httpstreamer.cupertinostreaming.util.CupertinoEncInfo;
import cpix.dto.CpixDTO;
import cpix.exception.CpixException;

/**
 * Created by Brown on 2019-12-09.
 */
public interface WowzaModule {
    public CpixDTO getDrmKeyInfo(String streamPath, String requestUrl, CpixDTO cpixDTO) throws CpixException;
    void setDashKeyInfo(CpixDTO responseCpixDTO, CencInfo cencInfo) throws CpixException;
    void setHlsKeyInfo(CpixDTO responseCpixDTO, CupertinoEncInfo encInfo) throws CpixException;
}
