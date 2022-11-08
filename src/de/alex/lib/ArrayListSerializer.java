package de.alex.lib;

import java.net.URLDecoder;
import java.net.URLEncoder;
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
        for (Object o : list) {
            TypeSerializer typeSerializer = getSerializer(o);
            if (typeSerializer != null) {
                builder.append(encode(String.valueOf(list.indexOf(o)), typeSerializer.serialize(o), o.getClass().getName()));
            } else {
                //TODO make some out debug output logger
                System.out.println(o.getClass().getName() + " does have a registered Serializer");
            }
        }
        return URLEncoder.encode(builder.toString(), StandardCharsets.UTF_8);
    }

    @Override
    protected Object deserialize(String serialized) {
        String inp = URLDecoder.decode(serialized, StandardCharsets.UTF_8);
        ArrayList<Object> list = new ArrayList<>();
        String[] vars_together = inp.split("\"");
        HashMap<String, String> mapped_vars = new HashMap<>();
        for (int i = 0; i < vars_together.length; i += 2) {
            mapped_vars.put(vars_together[i].substring(0, vars_together[i].length() - 1), vars_together[i + 1]);
        }
        for (Map.Entry<String, String> Entry : mapped_vars.entrySet()) {
            String type = Entry.getValue().split(":")[0];
            String value = Entry.getValue().split(":")[1];
            list.add(Integer.parseInt(Entry.getKey()), getSerializer(type).deserialize(value));
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected Class<ArrayList> getType() {
        return ArrayList.class;
    }
}
