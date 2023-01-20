package de.alex.lib;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static de.alex.lib.BasicSerializer.encode;
import static de.alex.lib.BasicSerializer.getSerializer;


public class ArrayListSerializer extends TypeSerializer {
    @SuppressWarnings("rawtypes")
    @Override
    protected String serialize(Object object) {
        @SuppressWarnings("unchecked") ArrayList<Object> list = (ArrayList) object;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            TypeSerializer typeSerializer = getSerializer(list.get(i));
            if (typeSerializer != null) {
                builder.append(encode(String.valueOf(i), typeSerializer.serialize(list.get(i)), list.get(i).getClass().getName()));
            } else {
                //TODO make some out debug output logger
                System.out.println(list.get(i).getClass().getName() + " does have a registered Serializer");
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
        ArrayList<Object> list = new ArrayList<>();
        String[] vars_together = inp.split("\"");
        if(serialized.equals(""))return list;
        HashMap<Integer, String> mapped_vars = new HashMap<>();
        for (int i = 0; i < vars_together.length; i += 2) {
            mapped_vars.put(Integer.parseInt(vars_together[i].substring(0, vars_together[i].length() - 1)), vars_together[i + 1]);
        }
        for (Map.Entry<Integer, String> Entry : mapped_vars.entrySet()) {
            String type = Entry.getValue().split(":")[0];
            String value = Entry.getValue().split(":")[1];
            list.add(Entry.getKey(), getSerializerFromType(type).deserialize(value));
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected Class<ArrayList> getType() {
        return ArrayList.class;
    }
}
