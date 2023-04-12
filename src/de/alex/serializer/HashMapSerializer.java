package de.alex.serializer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static de.alex.serializer.BasicSerializer.getSerializer;

public class HashMapSerializer extends TypeSerializer {
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected String serialize(Object object) {
        HashMap<Object, Object> hashmap = (HashMap) object;
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> Entry : hashmap.entrySet()) {
            TypeSerializer valueTypeSerializer = getSerializer(Entry.getValue());
            TypeSerializer keyTypeSerializer = getSerializer(Entry.getKey());
            if (valueTypeSerializer != null) {
                builder.append(encode(keyTypeSerializer.getType().getName() + ':' + keyTypeSerializer.serialize(Entry.getKey()), valueTypeSerializer.serialize(Entry.getValue()), valueTypeSerializer.getType().getName()));
            } else {
                //TODO make some out debug output logger
                if(!BasicSerializer.suppressWarnings){
                    System.out.println(Entry.getValue().getClass().getName() + " does have a registered Serializer");
                }
            }
        }
        try {
            return URLEncoder.encode(builder.toString(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Object deserialize(String serialized) {
        String inp = null;
        try {
            inp = URLDecoder.decode(serialized, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        HashMap<Object, Object> hashMap = new HashMap<>();
        String[] vars_together = inp.split("\"");
        HashMap<String, String> mapped_vars = new HashMap<>();
        if(serialized.equals(""))return hashMap;
        for (int i = 0; i < vars_together.length; i += 2) {
            mapped_vars.put(vars_together[i].substring(0, vars_together[i].length() - 1), vars_together[i + 1]);
        }
        for (Map.Entry<String, String> Entry : mapped_vars.entrySet()) {
            String value_type = Entry.getValue().split(":")[0];
//            String value = Entry.getValue().split(":")[1];
            String value = "";    //fixes stupid issue when trying to deserialize an empty array list
            if(!Entry.getValue().equals(value_type+":")){
                value = Entry.getValue().split(":")[1];
            }
            String key = Entry.getKey().split(":")[1];
            String key_type = Entry.getKey().split(":")[0];
            hashMap.put(getSerializerFromType(key_type).deserialize(key), getSerializerFromType(value_type).deserialize(value));
        }
        return hashMap;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected Class<HashMap> getType() {
        return HashMap.class;
    }
}
