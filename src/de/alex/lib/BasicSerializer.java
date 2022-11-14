package de.alex.lib;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BasicSerializer {
    private final static HashMap<String, TypeSerializer> serializers = new HashMap<>();

    public static String serialize(Object someObject) {
        StringBuilder builder = new StringBuilder();
        Field[] field = someObject.getClass().getDeclaredFields();  //gets all fields in the BasicGameState class
        for (Field field1 : field) {
            try {
                boolean access = field1.canAccess(someObject);
                field1.setAccessible(true);
                Object field_object = field1.get(someObject);   //gets the variable from the field
                TypeSerializer typeSerializer = BasicSerializer.getSerializer(field_object);
                if (typeSerializer != null) {
                    builder.append(BasicSerializer.encode(field1.getName(), typeSerializer.serialize(field_object), field_object.getClass().getName()));
                } else {
                    //TODO make some out debug output logger
                    System.out.println(field1.getName() + " does have a registered Serializer");
                }
                field1.setAccessible(access);
            } catch (Exception ignored) {
            }
        }
        return builder.toString();
    }

    // only good for static context objects since otherwise you have to create a new object of the class that would be discarded
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(T object, String serialised) {
        return (T) BasicSerializer.deserialize(serialised, object.getClass().getName());
    }

    //better for non-static context since you just have to go .getClass()
    //or use this.getClass() in a static context
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(Class<T> _class, String serialised) {
        return (T) BasicSerializer.deserialize(serialised, _class.getName());
    }

    public static Object deserialize(String serialized, String class_name) {
        return deserialize(serialized,class_name,null);
    }
    public static Object deserialize_this(String serialized, String class_name,Object old_instance) {
        return deserialize(serialized,class_name,old_instance);
    }

    private static Object deserialize(String serialized, String class_name,Object old_instance) {
        try {
            Object object_obj = old_instance;
            if(old_instance==null){
                object_obj= Class.forName(class_name).getDeclaredConstructor().newInstance();
            }
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
                    boolean accessible = field.canAccess(object_obj);
                    field.setAccessible(true);
                    try {
                        field.set(object_obj, BasicSerializer.getSerializer(type).deserialize(value));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    field.setAccessible(accessible);
                }
            }
            return object_obj;
        } catch (Exception e) {
            System.out.println("failed to deserialize");
//            e.printStackTrace();
            return null;
        }

    }

    protected static String encode(String name, String val, String class_name) {
        return name + "=\"" + class_name + ':' + val + '"';
    }

    protected static <T> TypeSerializer getSerializer(T object) {
        return BasicSerializer.getSerializer(object.getClass().getName());
    }

    protected static TypeSerializer getSerializer(String class_name) {
        if (BasicSerializer.serializers.isEmpty()) {
            //registers all serializers
            BasicSerializer.register_serializer(new StringSerializer());
            BasicSerializer.register_serializer(new ArrayListSerializer());
            BasicSerializer.register_serializer(new HashMapSerializer());
            BasicSerializer.register_serializer(new BoolSerializer());
            BasicSerializer.register_serializer(new IntSerializer());
            BasicSerializer.register_serializer(new FloatSerializer());
            BasicSerializer.register_serializer(new LongSerializer());
        }
        return BasicSerializer.serializers.getOrDefault(class_name, null);
    }

    private static void register_serializer(TypeSerializer serializer) {
        BasicSerializer.serializers.put(serializer.getType().getName(), serializer);
    }
    public static void register_external_serializer(TypeSerializer serializer){
        BasicSerializer.serializers.put(serializer.getType().getName(),serializer);
    }

}
