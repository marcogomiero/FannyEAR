package com.platformteam;

import org.infinispan.distribution.group.Grouper;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class LocationFanny {

    public static class LocationGrouper implements Grouper<String> {


        @Override
        public Class<String> getKeyType() {
            return String.class;
        }
    }

}
