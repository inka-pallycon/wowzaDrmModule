package com.pallycon.wowza.session;

import com.pallycon.cpix.dto.CpixDTO;
import com.wowza.wms.logging.WMSLogger;

import java.util.HashMap;

public class SessionChecker
{
    public static HashMap<String, CpixDTO> map = new HashMap();
    public static SessionChecker instance = null;
    public WMSLogger logger = null;

    public static SessionChecker getInstance(WMSLogger logger) {
        if (instance == null) {
            instance = new SessionChecker(logger);
        }

        return instance;
    }

    public SessionChecker(WMSLogger logger)
    {
        this.logger = logger;
    }

    public boolean isValid(String sessionId)
    {
        return map.containsKey(sessionId);
    }

    public void setSession(String sessionId, CpixDTO value)
    {
        map.put(sessionId, value);
        this.logger.info("Session added. total count : " + map.size());
    }

    public CpixDTO getSession(String sessionId) {
//        this.logger.info("Session total count : " + map.size());
        return map.get(sessionId);
    }

    public void removeSession(String sessionId) {
        map.remove(sessionId);
        this.logger.info("Session removed. total count : " + map.size());
    }
}