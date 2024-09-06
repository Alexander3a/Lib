package de.alex.byteSerializer;

import java.nio.ByteBuffer;

public abstract class TypeSerializer {
    BasicSerializer basicSerializer=null;
    protected abstract void serialize(Object object, ByteBuffer buffer);

    protected abstract Object deserialize(ByteBuffer buffer);

    @SuppressWarnings("rawtypes")
    protected abstract Class getType();

    protected <T> TypeSerializer getSerializer(T object) {
        return basicSerializer.getSerializer(object.getClass().getName());
    }
    protected <T> TypeSerializer getSerializerFromType(String type) {
        return basicSerializer.getSerializer(type);
    }
    protected void serialize_external(TypeSerializer typeSerializer, Object object,ByteBuffer buffer){
        typeSerializer.serialize(object,buffer);
    }
    protected Object deserialize_external(TypeSerializer typeSerializer, ByteBuffer buffer){
        return typeSerializer.deserialize(buffer);
    }
    protected Object deserialize_external(String type_name,ByteBuffer buffer){
        return basicSerializer.getSerializer(type_name).deserialize(buffer);
    }
}
