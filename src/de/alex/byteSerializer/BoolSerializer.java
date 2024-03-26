package de.alex.byteSerializer;

import java.nio.ByteBuffer;

public class BoolSerializer extends TypeSerializer{
	@Override
	protected void serialize(Object object, ByteBuffer buffer) {
		Boolean object1 = (Boolean) object;
		if(object1==null)buffer.put((byte)0);
		if(object1)buffer.put((byte)1);
		if(!object1)buffer.put((byte)2);
	}

	@Override
	protected Object deserialize(ByteBuffer buffer) {
		byte b = buffer.get();
		if(b==1)return true;
		if(b==2)return false;
		return null;
	}

	@Override
	protected Class getType() {
		return Boolean.class;
	}
}
