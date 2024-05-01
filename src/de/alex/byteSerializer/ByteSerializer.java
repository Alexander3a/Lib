package de.alex.byteSerializer;

import java.nio.ByteBuffer;

public class ByteSerializer extends TypeSerializer{
	@Override
	protected void serialize(Object object, ByteBuffer buffer) {
		buffer.put((byte) object);
	}

	@Override
	protected Object deserialize(ByteBuffer buffer) {
		return buffer.get();
	}

	@Override
	protected Class getType() {
		return Byte.class;
	}
}
