package de.alex.byteSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BasicSerializer {
    private final static HashMap<String, TypeSerializer> serializers = new HashMap<>();
    public static Boolean suppressWarnings=false;
    public static Boolean gzip=true;
    public static byte[] serialize(Object someObject) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.MAX_VALUE);
        getSerializer(PresetStringSerializer.class.getName()).serialize(getDefaultSerializerFor(someObject).getType().getName(),buffer);
        BasicSerializer.getDefaultSerializerFor(someObject).serialize(someObject,buffer);
        if(gzip){
            try {
                byte[] compressed = new byte[]{};
                int size = buffer.position();
                buffer.position(0);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(size);
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                for (int i = 0; i < size; i++) {
                    gzipOutputStream.write(buffer.get());
                }
                gzipOutputStream.close();
                compressed = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                gzipOutputStream.close();
                return compressed;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            byte[] output = new byte[buffer.position()];
            buffer.position(0);
            buffer.get(output);
            return output;
        }
    }

    public static void deserializeThis(byte[] serialized,Object thisObject){
        deserialize(serialized,thisObject);
    }
    public static Object deserialize(byte[] serialized) {
        return deserialize(serialized,(byte[])null);
    }

    private static Object deserialize(byte[] serialized,Object old_instance) {
        if(gzip){
            try {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serialized);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
                try {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = gzipInputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, len);
                    }
                    gzipInputStream.close();
                    byteArrayInputStream.close();
                    serialized = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    gzipInputStream.close();
                    byteArrayInputStream.close();
                    byteArrayOutputStream.close();
                    throw new RuntimeException(e);
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }

        }
        ByteBuffer buffer = ByteBuffer.wrap(serialized);
        TypeSerializer serializer = BasicSerializer.getSerializer((String) BasicSerializer.getSerializer(PresetStringSerializer.class.getName()).deserialize(buffer));
        if(serializer instanceof ObjectSerializer){
            return ((ObjectSerializer) serializer).deserialize(buffer,old_instance);
        }else {
            if(old_instance!=null){
                System.out.println("[BasicSerializer] ["+new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"] "+"ignoring Old Instance on non Custom Object");
            }
            return serializer.deserialize(buffer);
        }

    }

    protected static <T> TypeSerializer getSerializer(T object) {
        return BasicSerializer.getSerializer(object.getClass().getName());
    }

    protected static TypeSerializer getSerializer(String class_name) {
        if (!BasicSerializer.serializers.containsKey("java.lang.String")) {
            //registers all serializers
            BasicSerializer.register_serializer(new StringSerializer());
            BasicSerializer.register_serializer(new ObjectSerializer());
            BasicSerializer.register_serializer(new BytesSerializer());
            BasicSerializer.register_serializer(new LongSerializer());
            BasicSerializer.register_serializer(new BoolSerializer());
            BasicSerializer.register_serializer(new HashMapSerializer());
            BasicSerializer.register_serializer(new PresetStringSerializer());
            BasicSerializer.register_serializer(new IntSerializer());
            BasicSerializer.register_serializer(new ArrayListSerializer());
            BasicSerializer.register_serializer(new ShortSerializer());
            BasicSerializer.register_serializer(new ByteSerializer());
            BasicSerializer.register_serializer(new PrimitiveByteSerializer());
        }
        return BasicSerializer.serializers.getOrDefault(class_name, null);
    }
    protected static TypeSerializer getDefaultSerializerFor(Object object){
        if(object==null)return BasicSerializer.getSerializer(Object.class.getName());
        TypeSerializer serializer = BasicSerializer.getSerializer(object.getClass().getName());
        return serializer==null?BasicSerializer.getSerializer(Object.class.getName()):serializer;
    }

    private static void register_serializer(TypeSerializer serializer) {
        BasicSerializer.serializers.put(serializer.getType().getName(), serializer);
    }
    public static void register_external_serializer(TypeSerializer serializer){
        BasicSerializer.serializers.put(serializer.getType().getName(),serializer);
    }
    protected static Set<String> getSerializerRegisteredNames(){
        return BasicSerializer.serializers.keySet();
    }

    protected static ArrayList<String> dictStrings=new ArrayList<>();
    public static void clearDictString(){
        dictStrings.clear();
    }
    public static void addDictString(String string){
        dictStrings.add(string);
    }

}
