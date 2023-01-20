package de.alex.lib;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class BoolSerializer extends TypeSerializer {
    @Override
    protected String serialize(Object object) {
        try {
            return URLEncoder.encode(((Boolean) object).toString(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Object deserialize(String serialized) {
        try {
            return Boolean.parseBoolean(URLDecoder.decode(serialized, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Class<Boolean> getType() {
        return Boolean.class;
    }
}
