package de.alex.serializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Tests {
    public static void main(String[] args) {
//        Testing testing = new Testing();
//        testing.aLong=new Random().nextLong();
//        testing.string="kekse";
//        try {
//            testing.bytes= Files.readAllBytes(Path.of("/home/alex/Dokumente/maindb_key.kdbx"));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        Testing testing1 = BasicSerializer.deserialize(Testing.class,BasicSerializer.serialize(testing));
//        System.out.println("out "+testing1.equals(testing));


    }
    public static class Testing{
        String string;
        byte[] bytes;
        Long aLong;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Testing testing = (Testing) o;
            return Objects.equals(string, testing.string) && Arrays.equals(bytes, testing.bytes) && Objects.equals(aLong, testing.aLong);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(string, aLong);
            result = 31 * result + Arrays.hashCode(bytes);
            return result;
        }
    }
}
