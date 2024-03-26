package de.alex.byteSerializer;

import java.nio.ByteBuffer;

public class LongSerializer extends TypeSerializer{
	@Override
	protected void serialize(Object object, ByteBuffer buffer) {
		buffer.putLong(((Long) object));
	}

	@Override
	protected Object deserialize(ByteBuffer buffer) {
		return buffer.getLong();
	}

	@Override
	protected Class getType() {
		return Long.class;
	}
}
