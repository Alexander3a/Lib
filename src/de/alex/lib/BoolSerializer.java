package de.alex.lib;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class BoolSerializer extends TypeSerializer {
    @Override
    protected String serialize(Object object) {
        return URLEncoder.encode(((Boolean) object).toString(), StandardCharsets.UTF_8);
    }

    @Override
    protected Object deserialize(String serialized) {
        return Boolean.parseBoolean(URLDecoder.decode(serialized, StandardCharsets.UTF_8));
    }

    @Override
    protected Class<Boolean> getType() {
        return Boolean.class;
    }
}
