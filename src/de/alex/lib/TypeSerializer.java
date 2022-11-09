package de.alex.lib;

public abstract class TypeSerializer {
    protected abstract String serialize(Object object);

    protected abstract Object deserialize(String serialized);

    @SuppressWarnings("rawtypes")
    protected abstract Class getType();

    protected static <T> TypeSerializer getSerializer(T object) {
        return BasicSerializer.getSerializer(object.getClass().getName());
    }
    protected static String encode(String name, String val, String class_name) {
        return name + "=\"" + class_name + ':' + val + '"';
    }
    protected String serialize_external(TypeSerializer typeSerializer,Object object){
        return BasicSerializer.getSerializer(object).serialize(object);
    }
    protected Object deserialize_external(TypeSerializer typeSerializer,String serialized){
        return BasicSerializer.getSerializer(typeSerializer.getType()).deserialize(serialized);
    }
    protected Object deserialize_external(String type_name,String serialized){
        return BasicSerializer.getSerializer(type_name).deserialize(serialized);
    }
}
