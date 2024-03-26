package de.alex.byteSerializer;

import java.nio.ByteBuffer;

public class StringSerializer extends TypeSerializer{


	@Override
	protected void serialize(Object object, ByteBuffer buffer) {
		byte[] bytes = ((String) object).getBytes();
		buffer.putShort((short) bytes.length);
		buffer.put(bytes);
	}

	@Override
	protected Object deserialize(ByteBuffer buffer) {
		int size = buffer.getShort();
		byte[] outString = new byte[size];
		buffer.get(outString);
		return new String(outString);
	}

	@Override
	protected Class getType() {
		return String.class;
	}
}
