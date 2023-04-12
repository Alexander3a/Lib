package de.alex.serializer;

import java.util.Base64;

public class BytesSerializer extends TypeSerializer {
    @Override
    protected String serialize(Object object) {
        return Base64.getEncoder().encodeToString((byte[]) object);
    }

    @Override
    protected Object deserialize(String serialized) {
        return Base64.getDecoder().decode(serialized);
    }

    @Override
    protected Class<byte[]> getType() {
        return byte[].class;
    }
}
