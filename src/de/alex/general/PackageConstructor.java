package de.alex.general;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PackageConstructor<T> {
//    public static void main(String[] args) {
//        List<TypeSerializer> baseMods = new PackageConstructor<TypeSerializer>().constructAll("de.alex.byteSerializer",  x->Arrays.stream(x.getDeclaredConstructors()).anyMatch(z->z.getParameters().length==0), null);
//        System.out.println(((List) baseMods).stream().map(x->(Object)x).filter(x->x instanceof TypeSerializer).map(x->x.getClass().getSimpleName()).filter(x-> !((String) x).isBlank()).collect(Collectors.joining(", ")));
//    }
    public List<T> constructAll(String pkgPath,Function<Class<T>,Boolean> filterCondition,Function<Class<T>, T> constructor){
        if(filterCondition == null){
            filterCondition = x->true;
        }
        if(constructor == null){
            constructor = x->{
                try {
                    Constructor<T> declaredConstructor = x.getDeclaredConstructor();
                    declaredConstructor.setAccessible(true);
                    return declaredConstructor.newInstance();
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                         InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        }
        Function<Class<T>, Boolean> finalFilterCondition = filterCondition;
        Function<Class<T>, T> finalConstructor = constructor;
        return allPkgFsw(pkgPath).stream().map(x -> {
            try {
                return Class.forName(x);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).filter(x-> finalFilterCondition.apply((Class<T>) x)).map(x->{
            try {
                if(Modifier.isAbstract(x.getModifiers()))return null;
                return finalConstructor.apply((Class<T>) x);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
    private List<String> allPkgFsw(String pkgName){
        try {
            if(FileSystems.getDefault()==null){
                throw new RuntimeException("how did we even get to this?????");
            }
            Path root = Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            if(root.getFileName().toString().endsWith(".jar")){
//                System.out.println("[PackageConstructor] ["+new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"] "+"root "+root.toString());
//            Path start = Paths.get(this.getClass().getClassLoader().getResource(pkgName.replaceAll("\\.", "/")).toURI());
                URI uri = this.getClass().getClassLoader().getResource(pkgName.replaceAll("\\.", "/")).toURI();
//            System.out.println("[PackageConstructor] ["+new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"] "+"pain "+uri.toURL());
//                Path start = Paths.get(uri.toURL().getPath());
                try(FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
//                System.out.println("[PackageConstructor] ["+new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"] "+"system "+root.isAbsolute()+" "+start.isAbsolute());
//                System.out.println("[PackageConstructor] ["+new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"] "+"start "+start);
                    List<Path> collect = Files.walk(fileSystem.getPath(pkgName.replaceAll("\\.", "/"))).collect(Collectors.toList());
                    return collect.stream().map(Path::toString).filter(x -> x.endsWith(".class")).map(x->x.replace(root.getFileSystem().getSeparator(),".").substring(0,x.length()-x.split("\\.")[1].length()-1)).collect(Collectors.toList());
                }
           }
            Path start = Paths.get(this.getClass().getClassLoader().getResource(pkgName.replaceAll("\\.", "/")).toURI());
            List<Path> collect = Files.walk(start).collect(Collectors.toList());
            return collect.stream().map(x->root.getParent()!=null ?root.relativize(x): x).map(Path::toString).filter(x -> x.endsWith(".class")).map(x->x.replace(root.getFileSystem().getSeparator(),".").substring(0,x.length()-x.split("\\.")[1].length()-1)).collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
