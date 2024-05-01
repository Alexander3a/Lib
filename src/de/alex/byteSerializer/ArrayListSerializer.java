package de.alex.byteSerializer;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ArrayListSerializer extends TypeSerializer{
	@Override
	protected void serialize(Object object, ByteBuffer buffer) {
		int startPosition = buffer.position();
		buffer.putShort(Short.MAX_VALUE); //as a placeholder //second place where this being a short might matter
		ArrayList<Object> list =((ArrayList<Object>) object);
		for (Object ListEntry : list) {
			if (ListEntry == null) continue;
			TypeSerializer valueTypeSerializer = getSerializer(ListEntry);
			if (valueTypeSerializer == null) {
				if (!de.alex.serializer.BasicSerializer.suppressWarnings) {
					System.out.println("array value " + ListEntry.getClass().getName() + " does have a registered Serializer");
				}
				BasicSerializer.getSerializer(PresetStringSerializer.class.getName()).serialize("", buffer);
				continue;
			}
			BasicSerializer.getSerializer(PresetStringSerializer.class.getName()).serialize(valueTypeSerializer.getType().getName(), buffer);
			valueTypeSerializer.serialize(ListEntry, buffer);
		}
		Buffer.putSizeIn(startPosition,buffer);
	}

	@Override
	protected Object deserialize(ByteBuffer buffer) {
		int objectStart = buffer.position();
		int objectSize = buffer.getShort();
		int objectEnd = objectSize+objectStart;
		ArrayList<Object> list = new ArrayList<>();
		while (buffer.position()<objectEnd) {
			String valueClass = (String) BasicSerializer.getSerializer(PresetStringSerializer.class.getName()).deserialize(buffer);
			if(valueClass.isEmpty()){
				list.add(null);
				continue;
			}
			Object valueDeserialize = getSerializerFromType(valueClass).deserialize(buffer);
			list.add(valueDeserialize);
		}
		return list;
	}

	@Override
	protected Class getType() {
		return ArrayList.class;
	}
}
