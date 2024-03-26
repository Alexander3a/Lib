package de.alex.byteSerializer;

import java.nio.*;

public class Buffer {
	public static void putSizeIn(int startPos,ByteBuffer buffer){
		int endPosition = buffer.position();
		buffer.position(startPos);
		buffer.putShort((short) (endPosition-startPos));
		buffer.position(endPosition);
	}
}