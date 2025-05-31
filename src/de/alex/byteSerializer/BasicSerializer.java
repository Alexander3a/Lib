package de.alex.byteSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BasicSerializer {
    private final HashMap<String, TypeSerializer> serializers = new HashMap<>();
    public Boolean suppressWarnings=false;
    public Boolean gzip=true;
    public static BasicSerializer defaultInstance = new BasicSerializer();
    private final byte version = 0x1;
    public byte readVersion = 0x1;
    public byte[] serialize(Object someObject) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.MAX_VALUE);
        //if(gzip) header+=(byte) (1<<7);
        //if(!gzip)buffer.put(header);
        buffer.put(version);
        if(gzip){
            getSerializer(PresetStringSerializer.class.getName()).serialize(GZipWrapper.class.getName(),buffer);
            this.getSerializer(GZipWrapper.class.getName()).serialize(someObject,buffer);
        }else{
            getSerializer(PresetStringSerializer.class.getName()).serialize(getDefaultSerializerFor(someObject).getType().getName(),buffer);
            this.getDefaultSerializerFor(someObject).serialize(someObject,buffer);
        }

        byte[] output = new byte[buffer.position()];
        buffer.position(0);
        buffer.get(output);
        return output;

        //if(gzip){
        //    try {
        //        byte[] compressed = new byte[]{};
        //        int size = buffer.position();
        //        buffer.position(0);
        //        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(size);
        //        byteArrayOutputStream.write(header);
        //        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        //        for (int i = 0; i < size; i++) {
        //            gzipOutputStream.write(buffer.get());
        //        }
        //        gzipOutputStream.close();
        //        compressed = byteArrayOutputStream.toByteArray();
        //        byteArrayOutputStream.close();
        //        gzipOutputStream.close();
        //        return compressed;
        //    } catch (Exception e) {
        //        throw new RuntimeException(e);
        //    }
        //}else{
        //    byte[] output = new byte[buffer.position()];
        //    buffer.position(0);
        //    buffer.get(output);
        //    return output;
        //}
    }

    public void deserializeThis(byte[] serialized,Object thisObject){
        deserialize(serialized,thisObject);
    }
    public Object deserialize(byte[] serialized) {
        return deserialize(serialized,(byte[])null);
    }

    private Object deserialize(byte[] serialized,Object old_instance) {
        boolean gzipDecode = gzip;
        byte version = readVersion;
        if(version>0){
            byte header = serialized[0];
            version = header;
            //gzipDecode = (header & 128) != 0;
            //version = (byte) (header & 127);
            serialized= Arrays.copyOfRange(serialized,1,serialized.length);
        }else{
            if(gzipDecode){
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
        }
        ByteBuffer buffer = ByteBuffer.wrap(serialized);
        TypeSerializer serializer = this.getSerializer((String) this.getSerializer(PresetStringSerializer.class.getName()).deserialize(buffer));
        if(serializer instanceof ObjectSerializer){
            return ((ObjectSerializer) serializer).deserialize(buffer,old_instance);
        } else if (serializer instanceof GZipWrapper gZipWrapper) {
            return gZipWrapper.deserialize(buffer,old_instance);
        } else {
            if(old_instance!=null){
                System.out.println("[BasicSerializer] ["+new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"] "+"ignoring Old Instance on non Custom Object");
            }
            return serializer.deserialize(buffer);
        }

    }

    protected <T> TypeSerializer getSerializer(T object) {
        return this.getSerializer(object.getClass().getName());
    }

    protected TypeSerializer getSerializer(String class_name) {
        if (!this.serializers.containsKey("java.lang.String")) {
            //registers all serializers
            this.register_serializer(new StringSerializer());
            this.register_serializer(new ObjectSerializer());
            this.register_serializer(new BytesSerializer());
            this.register_serializer(new LongSerializer());
            this.register_serializer(new BoolSerializer());
            this.register_serializer(new HashMapSerializer());
            this.register_serializer(new PresetStringSerializer());
            this.register_serializer(new IntSerializer());
            this.register_serializer(new ArrayListSerializer());
            this.register_serializer(new ShortSerializer());
            this.register_serializer(new ByteSerializer());
            this.register_serializer(new PrimitiveByteSerializer());
            this.register_serializer(new GZipWrapper());
        }
        return this.serializers.getOrDefault(class_name, null);
    }
    protected TypeSerializer getDefaultSerializerFor(Object object){
        if(object==null)return this.getSerializer(Object.class.getName());
        TypeSerializer serializer = this.getSerializer(object.getClass().getName());
        return serializer==null?this.getSerializer(Object.class.getName()):serializer;
    }

    private void register_serializer(TypeSerializer serializer) {
        serializer.basicSerializer=this;
        this.serializers.put(serializer.getType().getName(), serializer);
    }
    public void register_external_serializer(TypeSerializer serializer){
        serializer.basicSerializer=this;
        this.serializers.put(serializer.getType().getName(),serializer);
    }
    protected Set<String> getSerializerRegisteredNames(){
        return this.serializers.keySet();
    }

    protected ArrayList<String> dictStrings=new ArrayList<>();
    public void resetSettings(){
        dictStrings.clear();
        suppressWarnings=false;
        gzip=true;
        serializers.clear();
    }
    public void addDictString(String string){
        dictStrings.add(string);
    }

}
