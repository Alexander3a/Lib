package de.alex.byteSerializer;

import java.nio.ByteBuffer;

public class ShortSerializer extends TypeSerializer{
	@Override
	protected void serialize(Object object, ByteBuffer buffer) {
		buffer.putShort((Short) object);
	}

	@Override
	protected Object deserialize(ByteBuffer buffer) {
		return buffer.getShort();
	}

	@Override
	protected Class getType() {
		return Short.class;
	}
}
