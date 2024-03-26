package de.alex.byteSerializer;

import de.alex.serializer.BasicSerializer;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ObjectSerializer extends TypeSerializer{
	@Override
	protected void serialize(Object object,ByteBuffer buffer) {
		//basic copy of default object serializer
		int startPosition = buffer.position();
		buffer.putInt(Integer.MAX_VALUE); //as a placeholder
		de.alex.byteSerializer.BasicSerializer.getSerializer(String.class.getName()).serialize(object.getClass().getName(),buffer);
		Field[] field = object.getClass().getDeclaredFields();  //gets all fields in the class
		for (Field field1 : field) {
			if(field1.getName().startsWith("__"))continue;
			if(Modifier.isTransient(field1.getModifiers()))continue;
			try {
				field1.setAccessible(true);
				Object field_object = field1.get(object);   //gets the variable from the field
				if(field_object!=null){
					TypeSerializer serializer = getSerializer(field_object);
					if (serializer != null) {
						de.alex.byteSerializer.BasicSerializer.getSerializer(String.class.getName()).serialize(field_object.getClass().getName(),buffer);
						de.alex.byteSerializer.BasicSerializer.getSerializer(String.class.getName()).serialize(field1.getName(),buffer);
						serializer.serialize(field_object,buffer);
					} else {
						if(!BasicSerializer.suppressWarnings){
							System.out.println(field1.getName() + " does have a registered Serializer");
						}
					}
				}else{
					de.alex.byteSerializer.BasicSerializer.getSerializer(String.class.getName()).serialize("",buffer);
					de.alex.byteSerializer.BasicSerializer.getSerializer(String.class.getName()).serialize(field1.getName(),buffer);
				}

			} catch (Exception ignored) {
				ignored.printStackTrace();
			}
		}
		Buffer.putSizeIn(startPosition,buffer);
		return;
	}


	protected Object deserialize(ByteBuffer buffer,Object old_instance) {
		int objectStart = buffer.position();
		int objectSize = buffer.getInt();
		int objectEnd = objectSize+objectStart;
		String objectClassName = (String) de.alex.byteSerializer.BasicSerializer.getSerializer(String.class.getName()).deserialize(buffer);
		try {
			Object object_obj;
			Class<?> aClass = Class.forName(objectClassName);
			if(old_instance==null){
				Constructor declaredConstructor = aClass.getDeclaredConstructor();
				declaredConstructor.setAccessible(true);
				object_obj = declaredConstructor.newInstance();
			}else {
				object_obj=old_instance;
			}
			while (buffer.position()<objectEnd){
				String className = (String) de.alex.byteSerializer.BasicSerializer.getSerializer(String.class.getName()).deserialize(buffer);
				String variableName = (String) de.alex.byteSerializer.BasicSerializer.getSerializer(String.class.getName()).deserialize(buffer);
				Object deserialize;
				if(!className.equals("")){
					TypeSerializer serializer = de.alex.byteSerializer.BasicSerializer.getSerializer(className);
					deserialize = serializer.deserialize(buffer);
				}else{
					deserialize = null;
				}
				Field declaredField = aClass.getDeclaredField(variableName);
				declaredField.setAccessible(true);
				declaredField.set(object_obj,deserialize);
			}
			return object_obj;
		}catch (Exception e){
			e.printStackTrace();
			buffer.position(objectEnd);
		}
		return null;
	}
	@Override
	protected Object deserialize(ByteBuffer buffer) {
		return deserialize(buffer,null);
	}


	@Override
	protected Class getType() {
		return Object.class;
	}
}
