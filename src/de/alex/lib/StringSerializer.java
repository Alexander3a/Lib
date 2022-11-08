package de.alex.lib;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class StringSerializer extends TypeSerializer {
    @Override
    protected String serialize(Object object) {
        return URLEncoder.encode((String) object, StandardCharsets.UTF_8);
    }

    @Override
    protected Object deserialize(String serialized) {
        return URLDecoder.decode(serialized, StandardCharsets.UTF_8);
    }

    @Override
    protected Class<String> getType() {
        return String.class;
    }
}
