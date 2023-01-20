package de.alex.lib;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FloatSerializer extends TypeSerializer {
    @Override
    protected String serialize(Object object) {
        try {
            return URLEncoder.encode(object.toString(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Object deserialize(String serialized) {
        try {
            return Float.parseFloat(URLDecoder.decode(serialized, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Class<Float> getType() {
        return Float.class;
    }
}
