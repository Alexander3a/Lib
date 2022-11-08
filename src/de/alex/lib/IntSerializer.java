package de.alex.lib;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class IntSerializer extends TypeSerializer {
    @Override
    protected String serialize(Object object) {
        return URLEncoder.encode(((Integer) object).toString(), StandardCharsets.UTF_8);
    }

    @Override
    protected Object deserialize(String serialized) {
        return Integer.parseInt(URLDecoder.decode(serialized, StandardCharsets.UTF_8));
    }

    @Override
    protected Class<Integer> getType() {
        return Integer.class;
    }
}
