package de.alex.lib;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FloatSerializer extends TypeSerializer {
    @Override
    protected String serialize(Object object) {
        return URLEncoder.encode(object.toString(), StandardCharsets.UTF_8);
    }

    @Override
    protected Object deserialize(String serialized) {
        return Float.parseFloat(URLDecoder.decode(serialized, StandardCharsets.UTF_8));
    }

    @Override
    protected Class<Float> getType() {
        return Float.class;
    }
}
