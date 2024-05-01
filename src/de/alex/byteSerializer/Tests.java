package de.alex.byteSerializer;

import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Tests {
    public static void main(String[] args) {
        Testing testing = new Testing("something");
//        String serialize = de.alex.serializer.BasicSerializer.serialize(testing);
//        System.out.println(serialize.length);
        testing.idk=false;
        testing.bytes="a".getBytes(StandardCharsets.UTF_8);
        testing.aLong=1L;
        testing.string="nothing";
        testing.map.put("a",13L);
        testing.map.put("b",-13L);
        testing.map.put("c",15L);
        testing.list.add(99L);
        testing.list.add(91319L);
        testing.list.add(9139L);
        testing.list.add(991L);
        testing.list.remove(1);
        testing.bList.add((byte)255);
        testing.bList.add((byte)252);
        testing.bList.add((byte)251);
        testing.tList.add(new Testing("adb"));
        testing.tList.add(new Testing("adb"));
        BasicSerializer.gzip=true;
        BasicSerializer.register_external_serializer(new ObjectSerializer(Testing.class));
        BasicSerializer.register_external_serializer(new TypeSerializer() {
            @Override
            protected void serialize(Object object, ByteBuffer buffer) {
                Testing.wirdEnum object1 = (Testing.wirdEnum) object;
                TypeSerializer serializer = getSerializer(object1.ordinal());
                serialize_external(serializer,object1.ordinal(),buffer);
            }

            @Override
            protected Object deserialize(ByteBuffer buffer) {
                int o = (int)deserialize_external(getSerializerFromType(Integer.class.getName()), buffer);
                return Testing.wirdEnum.values()[o];
            }

            @Override
            protected Class getType() {
                return Testing.wirdEnum.class;
            }
        });
        byte[] serialize = BasicSerializer.serialize(testing);
        byte[] serializeL = BasicSerializer.serialize(testing.tList);
        ArrayList<Testing> deserialize1 = (ArrayList<Testing>) BasicSerializer.deserialize(serializeL);

        System.out.println("done");
//        de.alex.serializer.BasicSerializer.deserialize_this(serialize,testing.getClass().getName(),testing);
//        BasicSerializer.deserializeThis(serialize,testing);
        Testing deserialize = (Testing) BasicSerializer.deserialize(serialize);

        System.out.println("done");


    }
    public static class Testing{
        public static enum wirdEnum{
            IDK,
            IDK2,
            AdaD
        }
        wirdEnum anEnum = wirdEnum.IDK2;
        String string;
        byte[] bytes;
        Boolean idk = null;
        Long aLong;
        HashMap<String,Long> map=new HashMap<>();
        ArrayList<Long> list=new ArrayList<>();
        ArrayList<Byte> bList=new ArrayList<>();
        ArrayList<Testing> tList=new ArrayList<>();
        private Testing(){

        }
        public Testing(String string) {
            this.string = string;
        }

    }
}
