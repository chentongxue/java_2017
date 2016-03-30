package sacred.alliance.magic.util;

import java.io.UnsupportedEncodingException;

import org.apache.mina.core.buffer.IoBuffer;



public class ByteBufferTool {
	
	 public static void putUTF(IoBuffer buffer, String string) {
			try {
				if(null == string){
					buffer.putShort((short)0);
					return ;
				}
				byte[] data = string.getBytes("UTF-8");
				buffer.putShort((short) (data.length));
				buffer.put(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	 
	public static String getUTF(IoBuffer buffer) {
		int length = buffer.getShort();
		byte[] data = new byte[length];
		buffer.get(data);
		try {
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	 

	 public static  byte[] intToByte(int i) {
	       /* byte[] abyte0 = new byte[4];
	        abyte0[0] = (byte) (0xff & i);
	        abyte0[1] = (byte) ((0xff00 & i) >> 8);
	        abyte0[2] = (byte) ((0xff0000 & i) >> 16);
	        abyte0[3] = (byte) ((0xff000000 & i) >> 24);
	        return abyte0;*/
		 
		 byte[] result = new byte[4]; 
		 result[0] = (byte)((i >> 24) & 0xFF); 
		 result[1] = (byte)((i >> 16) & 0xFF); 
		 result[2] = (byte)((i >> 8) & 0xFF); 
		 result[3] = (byte)(i & 0xFF); 
		 return result; 

	    }

	 public static  int bytesToInt(byte[] bytes) {
	        /*int addr = bytes[0] & 0xFF;
	        addr |= ((bytes[1] << 8) & 0xFF00);
	        addr |= ((bytes[2] << 16) & 0xFF0000);
	        addr |= ((bytes[3] << 24) & 0xFF000000);
	        return addr;*/
		 
		 int offset = 0 ;
		 int value = 0;
	        for (int i = 0; i < 4; i++) {
	            int shift = (4 - 1 - i) * 8;
	            value += (bytes[i + offset] & 0x000000FF) << shift;
	        }
	        return value;
	    }
 

}
