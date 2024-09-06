package de.alex.byteSerializer;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class HashMapSerializer extends TypeSerializer{
	@Override
	protected void serialize(Object object, ByteBuffer buffer) {
		int startPosition = buffer.position();
		buffer.putShort(Short.MAX_VALUE); //as a placeholder //second place where this being a short might matter
		HashMap<Object, Object> hashmap = (HashMap) object;
		for (Map.Entry<Object, Object> objectObjectEntry : hashmap.entrySet()) {
			TypeSerializer valueTypeSerializer = getSerializer(objectObjectEntry.getValue());
			TypeSerializer keyTypeSerializer = getSerializer(objectObjectEntry.getKey());
			if(valueTypeSerializer==null){
				if(!basicSerializer.suppressWarnings){
					System.out.println("hashmap value "+objectObjectEntry.getValue().getClass().getName()+" does have a registered Serializer");
				}
				continue;
			}
			if(keyTypeSerializer==null){
				if(!basicSerializer.suppressWarnings){
					System.out.println("hashmap key "+objectObjectEntry.getKey().getClass().getName()+" does have a registered Serializer");
				}
				continue;
			}
			getSerializer(PresetStringSerializer.class.getName()).serialize(keyTypeSerializer.getType().getName(),buffer);
			keyTypeSerializer.serialize(objectObjectEntry.getKey(),buffer);
			if (objectObjectEntry.getValue()!=null) {
				getSerializer(PresetStringSerializer.class.getName()).serialize(valueTypeSerializer.getType().getName(),buffer);
				valueTypeSerializer.serialize(objectObjectEntry.getValue(),buffer);
			}else{
				getSerializer(PresetStringSerializer.class.getName()).serialize("",buffer);
			}
		}
		Buffer.putSizeIn(startPosition,buffer);
	}

	@Override
	protected Object deserialize(ByteBuffer buffer) {
		int objectStart = buffer.position();
		int objectSize = buffer.getShort();
		int objectEnd = objectSize+objectStart;
		HashMap<Object, Object> hashMap = new HashMap<>();
		while (buffer.position()<objectEnd) {
			String keyClass = (String) getSerializer(PresetStringSerializer.class.getName()).deserialize(buffer);
			Object keyDeserialize = getSerializerFromType(keyClass).deserialize(buffer);
			String valueClass = (String) getSerializer(PresetStringSerializer.class.getName()).deserialize(buffer);
			Object valueDeserialize = null;
			if(valueClass!=null)valueDeserialize = getSerializerFromType(valueClass).deserialize(buffer);
			hashMap.put(keyDeserialize,valueDeserialize);
		}
		return hashMap;
	}

	@Override
	protected Class getType() {
		return HashMap.class;
	}
}
