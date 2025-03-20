package de.alex.byteSerializer;

import java.nio.ByteBuffer;

public class EnumSerializer<T extends Enum<T>> extends TypeSerializer{
	Class aClass;
	public EnumSerializer(Class<T> aClass){
		this.aClass=aClass;
	}

	@Override
	protected void serialize(Object object, ByteBuffer buffer) {
		Enum object1 = (Enum) object;
		TypeSerializer serializer = getSerializer(object1.ordinal());
		serialize_external(serializer,object1.ordinal(),buffer);
	}

	@Override
	protected Object deserialize(ByteBuffer buffer) {
		int o = (int)deserialize_external(getSerializerFromType(Integer.class.getName()), buffer);
		return aClass.getEnumConstants()[o];
	}

	@Override
	protected Class getType() {
		return aClass;
	}
}
