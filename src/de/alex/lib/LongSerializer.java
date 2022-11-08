package de.alex.lib;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class LongSerializer extends TypeSerializer {
    @Override
    protected String serialize(Object object) {
        return URLEncoder.encode(((Long) object).toString(), StandardCharsets.UTF_8);
    }

    @Override
    protected Object deserialize(String serialized) {
        return Long.parseLong(URLDecoder.decode(serialized, StandardCharsets.UTF_8));
    }

    @Override
    protected Class<Long> getType() {
        return Long.class;
    }
}
