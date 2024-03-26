package de.alex.byteSerializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class BasicSerializer {
    private final static HashMap<String, TypeSerializer> serializers = new HashMap<>();
    public static Boolean suppressWarnings=false;
    public static byte[] serialize(Object someObject) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.MAX_VALUE);
        BasicSerializer.getDefaultSerializer().serialize(someObject,buffer);
        byte[] output = new byte[buffer.position()];
        buffer.position(0);
        buffer.get(output);
        return output;
    }

    public static void deserializeThis(byte[] serialized,Object thisObject){
        deserialize(serialized,thisObject);
    }
    public static Object deserialize(byte[] serialized) {
        return deserialize(serialized,(byte[])null);
    }

    private static Object deserialize(byte[] serialized,Object old_instance) {
        ByteBuffer buffer = ByteBuffer.wrap(serialized);
        return ((ObjectSerializer) BasicSerializer.getDefaultSerializer()).deserialize(buffer,old_instance);

    }

    protected static <T> TypeSerializer getSerializer(T object) {
        return BasicSerializer.getSerializer(object.getClass().getName());
    }

    protected static TypeSerializer getSerializer(String class_name) {
        if (!BasicSerializer.serializers.containsKey("java.lang.String")) {
            //registers all serializers
            BasicSerializer.register_serializer(new StringSerializer());
            BasicSerializer.register_serializer(new ObjectSerializer());
            BasicSerializer.register_serializer(new ByteSerializer());
            BasicSerializer.register_serializer(new LongSerializer());
            BasicSerializer.register_serializer(new BoolSerializer());
        }
        return BasicSerializer.serializers.getOrDefault(class_name, null);
    }
    protected static TypeSerializer getDefaultSerializer(){
        return BasicSerializer.getSerializer(Object.class.getName());
    }

    private static void register_serializer(TypeSerializer serializer) {
        BasicSerializer.serializers.put(serializer.getType().getName(), serializer);
    }

}
