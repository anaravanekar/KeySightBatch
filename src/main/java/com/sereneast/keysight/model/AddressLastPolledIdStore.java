package com.sereneast.keysight.model;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AddressLastPolledIdStore {
    private String lastPolledId;

    public String getLastPolledId() {
        synchronized (lastPolledId) {
            return lastPolledId;
        }
    }

    public void setLastPolledId(String idToUpdate) {
        if(StringUtils.isEmpty(idToUpdate)) {
            return;
        }
        synchronized (lastPolledId) {
            if (idToUpdate.compareTo(lastPolledId) > 0)
                lastPolledId = idToUpdate;
        }
    }
}
