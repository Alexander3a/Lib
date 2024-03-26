package de.alex.byteSerializer;

import java.nio.ByteBuffer;

public class ByteSerializer extends TypeSerializer{
	@Override
	protected void serialize(Object object, ByteBuffer buffer) {
		int startPosition = buffer.position();
		buffer.putShort(Short.MAX_VALUE); //as a placeholder
		buffer.putShort(((short) ((byte[]) object).length));//useless
		buffer.put(((byte[]) object));
		Buffer.putSizeIn(startPosition,buffer);
	}

	@Override
	protected Object deserialize(ByteBuffer buffer) {
		int size = (int)buffer.getShort();
		int arraySize = buffer.getShort();
		byte[] out = new byte[arraySize];
		buffer.get(out);
		return out;
	}

	@Override
	protected Class getType() {
		return byte[].class;
	}
}
