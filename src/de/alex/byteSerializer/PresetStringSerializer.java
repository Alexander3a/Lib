package de.alex.byteSerializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class PresetStringSerializer extends TypeSerializer{
	private HashMap<Short,String> dict=new HashMap<>();
	@Override
	protected void serialize(Object object, ByteBuffer buffer) {
		if(dict.isEmpty())createDict();
		short val = 0;
		if(object instanceof String){
			if(dict.containsValue(((String) object))){
				Map.Entry<Short, String> shortStringEntry = dict.entrySet().stream().filter(x -> x.getValue().equals(((String) object))).findFirst().get();
				val=shortStringEntry.getKey();
			}else {
				System.out.println("[PresetStringSerializer] ["+new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"] "+"unable to serialize unknown string");
			}
		}else{
			throw new RuntimeException("non String passed to serialize");
		}
		buffer.putShort(val);
	}

	@Override
	protected Object deserialize(ByteBuffer buffer) {
		if(dict.isEmpty())createDict();
		short index = buffer.getShort();
		if(index!=0){
			if (dict.containsKey(index)) {
				return dict.get(index);
			}
		}
		return null;
	}
	private void createDict(){
		dict.clear();
		Set<String> serializerRegisteredNames = basicSerializer.getSerializerRegisteredNames();
		List<String> collect = new ArrayList<>(serializerRegisteredNames);
		collect.add("");
		collect.addAll(basicSerializer.dictStrings);
		for (String serializerRegisteredName : collect) {
			try {
				MessageDigest complete = MessageDigest.getInstance("MD5");
				complete.update(serializerRegisteredName.getBytes(StandardCharsets.UTF_8));
				ByteBuffer buffer = ByteBuffer.allocateDirect(Integer.BYTES);
				buffer.put(complete.digest(),0,buffer.limit());
				buffer.position(0);
				dict.put(buffer.getShort(),serializerRegisteredName);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}


	@Override
	protected Class getType() {
		return PresetStringSerializer.class;
	}
}
