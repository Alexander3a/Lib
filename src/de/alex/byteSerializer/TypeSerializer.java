package de.alex.byteSerializer;

import java.nio.ByteBuffer;

public abstract class TypeSerializer {
    protected abstract void serialize(Object object, ByteBuffer buffer);

    protected abstract Object deserialize(ByteBuffer buffer);

    @SuppressWarnings("rawtypes")
    protected abstract Class getType();

    protected static <T> TypeSerializer getSerializer(T object) {
        return BasicSerializer.getSerializer(object.getClass().getName());
    }
    protected static <T> TypeSerializer getSerializerFromType(String type) {
        return BasicSerializer.getSerializer(type);
    }
}
