package de.alex.byteSerializer;

import java.nio.ByteBuffer;

public class IntSerializer extends TypeSerializer{
	@Override
	protected void serialize(Object object, ByteBuffer buffer) {
		buffer.putInt(((Integer) object));
	}

	@Override
	protected Object deserialize(ByteBuffer buffer) {
		return buffer.getInt();
	}

	@Override
	protected Class getType() {
		return Integer.class;
	}
}
