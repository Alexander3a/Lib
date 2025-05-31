package de.alex.byteSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipWrapper extends TypeSerializer{
    @Override
    protected void serialize(Object object, ByteBuffer orgBuffer) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.MAX_VALUE);
        TypeSerializer defaultSerializerFor = basicSerializer.getDefaultSerializerFor(object);
        basicSerializer.getSerializer(PresetStringSerializer.class.getName()).serialize(defaultSerializerFor.getType().getName(),buffer);
        defaultSerializerFor.serialize(object,buffer);
        try {
            int size = buffer.position();
            buffer.position(0);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(size);
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            for (int i = 0; i < size; i++) {
                gzipOutputStream.write(buffer.get());
            }
            gzipOutputStream.close();
            orgBuffer.put(byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.close();
            gzipOutputStream.close();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        buffer.clear();
    }

    @Override
    protected Object deserialize(ByteBuffer buffer) {
        return deserialize(buffer,null);
    }
    protected Object deserialize(ByteBuffer orgBuffer,Object old_instance) {
        try {
            ByteBufferBackedInputStream byteArrayInputStream = new ByteBufferBackedInputStream(orgBuffer.slice());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            try {
                byte[] bytes = new byte[1024];
                int len;
                while ((len = gzipInputStream.read(bytes)) != -1) {
                    byteArrayOutputStream.write(bytes, 0, len);
                }
                gzipInputStream.close();
                byteArrayInputStream.close();
                ByteBuffer byteBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
                byteArrayOutputStream.close();
                TypeSerializer serializer = basicSerializer.getSerializer((String) basicSerializer.getSerializer(PresetStringSerializer.class.getName()).deserialize(byteBuffer));
                if(serializer instanceof ObjectSerializer){
                    return ((ObjectSerializer) serializer).deserialize(byteBuffer,old_instance);
                } else if (serializer instanceof GZipWrapper gZipWrapper) {
                    return gZipWrapper.deserialize(byteBuffer,old_instance);
                } else {
                    if(old_instance!=null){
                        System.out.println("[BasicSerializer] ["+new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"] "+"ignoring Old Instance on non Custom Object");
                    }
                    return serializer.deserialize(byteBuffer);
                }

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
    protected static class ByteBufferBackedInputStream extends InputStream {

        ByteBuffer buf;

        public ByteBufferBackedInputStream(ByteBuffer buf) {
            this.buf = buf;
        }

        public int read() throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }
            return buf.get() & 0xFF;
        }

        public int read(byte[] bytes, int off, int len)
                throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }

            len = Math.min(len, buf.remaining());
            buf.get(bytes, off, len);
            return len;
        }
    }

    @Override
    protected Class getType() {
        return GZipWrapper.class;
    }
}
