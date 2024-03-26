package de.alex.byteSerializer;

import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class Tests {
    public static void main(String[] args) {
        Testing testing = new Testing("something");
//        String serialize = de.alex.serializer.BasicSerializer.serialize(testing);
        byte[] serialize = BasicSerializer.serialize(testing);
        System.out.println(serialize.length);
        testing.idk=false;
        testing.bytes="a".getBytes(StandardCharsets.UTF_8);
        testing.aLong=1L;
        testing.string="nothing";

        System.out.println("done");
//        de.alex.serializer.BasicSerializer.deserialize_this(serialize,testing.getClass().getName(),testing);
        BasicSerializer.deserializeThis(serialize,testing);

        System.out.println("done");


    }
    public static class Testing{
        String string;
        byte[] bytes;
        Boolean idk = null;
        Long aLong;
        private Testing(){

        }
        public Testing(String string) {
            this.string = string;
        }

    }
}
