package de.alex.serializer;

import de.alex.Base85;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class BytesSerializer extends TypeSerializer {
    @Override
    protected String serialize(Object object) {
        if(BasicSerializer.experimentalByteSerialization){
            try {
                byte[] compressed = new byte[]{};
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(((byte[]) object).length);
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                try {
                    gzipOutputStream.write((byte[]) object);
                    gzipOutputStream.close();
                    compressed = byteArrayOutputStream.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                byteArrayOutputStream.close();
                gzipOutputStream.close();
                return new String(Base85.getRfc1924Encoder().encode(compressed), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            return Base64.getEncoder().encodeToString((byte[]) object);
        }
    }

    @Override
    protected Object deserialize(String serialized) {
        if(BasicSerializer.experimentalByteSerialization){
            try {
                byte[] decode = Base85.getRfc1924Decoder().decode(serialized.getBytes(StandardCharsets.UTF_8));
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decode);
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
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    return bytes;
                } catch (IOException e) {
                    gzipInputStream.close();
                    byteArrayInputStream.close();
                    byteArrayOutputStream.close();
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return Base64.getDecoder().decode(serialized);
    }

    @Override
    protected Class<byte[]> getType() {
        return byte[].class;
    }
}
