package com.sereneast.keysight.model;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AccountLastPolledIdStore {
    private String lastPolledId="";

    private Integer lock=1;

    public String getLastPolledId() {
        synchronized (lock) {
            return lastPolledId;
        }
    }

    public void setLastPolledId(String idToUpdate) {
        if(StringUtils.isEmpty(idToUpdate)) {
            return;
        }
        synchronized (lock) {
            if (idToUpdate.compareTo(lastPolledId) > 0)
                lastPolledId = idToUpdate;
        }
    }
}
