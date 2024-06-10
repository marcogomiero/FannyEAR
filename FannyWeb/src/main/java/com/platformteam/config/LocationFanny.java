package com.platformteam.config;

import org.infinispan.distribution.group.Grouper;

public class LocationFanny {

    public static class LocationGrouper implements Grouper<String> {


        @Override
        public Class<String> getKeyType() {
            return String.class;
        }
    }

}
