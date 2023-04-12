package de.alex.serializer;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public abstract class GenericObjectSerializer extends TypeSerializer {
    Class aClass = null;

    public GenericObjectSerializer(Class aClass) {
        this.aClass = aClass;
        BasicSerializer.register_external_serializer(this);
    }

    @Override
    protected String serialize(Object object) {
        //basic copy of default object serializer
        StringBuilder builder = new StringBuilder();
        Field[] field = object.getClass().getDeclaredFields();  //gets all fields in the BasicGameState class
        for (Field field1 : field) {
            try {
                field1.setAccessible(true);
                Object field_object = field1.get(object);   //gets the variable from the field
                TypeSerializer typeSerializer = getSerializer(field_object);
                if (typeSerializer != null) {
                    builder.append(encode(field1.getName(), serialize_external(typeSerializer,field_object), field_object.getClass().getName()));
                } else {
                    //TODO make some out debug output logger
                    if(!BasicSerializer.suppressWarnings){
                        System.out.println(field1.getName() + " does have a registered Serializer");
                    }
                }
            } catch (Exception ignored) {
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
        try {
            serialized= URLDecoder.decode(serialized,StandardCharsets.UTF_8.name());
            Object object_obj = getType().getDeclaredConstructor().newInstance();
            HashMap<String, Field> mapped_fields = new HashMap<>();
            for (Field declaredField : object_obj.getClass().getDeclaredFields()) {     //gets all fields in the BasicGameState/Objects class
                mapped_fields.put(declaredField.getName(), declaredField);
            }
            String[] vars_together = serialized.split("\"");
            HashMap<String, String> mapped_vars = new HashMap<>();
            for (int i = 0; i < vars_together.length; i += 2) {
                mapped_vars.put(vars_together[i].substring(0, vars_together[i].length() - 1), vars_together[i + 1]);
            }
            for (Map.Entry<String, String> Entry : mapped_vars.entrySet()) {
                if (mapped_fields.containsKey(Entry.getKey())) {
                    Field field = mapped_fields.get(Entry.getKey());
                    String mixed_value = Entry.getValue();
                    String type = mixed_value.split(":")[0];
                    String value = "";
                    try {
                        value = mixed_value.split(":")[1];
                    }catch (ArrayIndexOutOfBoundsException e){
                        // most likely just an empty string
                        //nothing to worry about
                        if(!mixed_value.equals(type+":")){
                            throw e;

                        }
                    }
                    field.setAccessible(true);
                    try {
                        field.set(object_obj, deserialize_external(type,value));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            return object_obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected Class getType() {
        return aClass;
    }
}
