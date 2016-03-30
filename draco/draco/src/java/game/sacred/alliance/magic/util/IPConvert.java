package sacred.alliance.magic.util;

public class IPConvert {

	public static long ipToLong(String strIP){
		//将127.0.0.1 形式的IP地址转换成10进制整数
		long[] ip = new long[4];
		int position1 = strIP.indexOf(".");
		int position2 = strIP.indexOf(".", position1 + 1);
		int position3 = strIP.indexOf(".", position2 + 1);
		ip[0] = Long.parseLong(strIP.substring(0, position1));
		ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(strIP.substring(position3 + 1));
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	public static String longToIP(long longIP){
		//将10进制整数形式转换成127.0.0.1形式的IP地址
		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf(longIP >>> 24));//直接右移24位
		sb.append(".");
		//将高8位置0，然后右移16位
		sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf(longIP & 0x000000FF));
		return sb.toString();
	}
	
	public static boolean isValidIPAddress(String ip){
		return true ;
	}
	
	public static void main(String[] args){
//         System.out.println("IP地址的各种表现形式：\r\n");
//         System.out.print("32位二进制形式：");
//         System.out.println(Long.toBinaryString(3526601384L));
//         System.out.print("十进制形式：");
//         System.out.println(ipToLong("210.51.170.168"));
//         System.out.print("普通形式：");
//         System.out.println(longToIP(3526601384L));
    }
}
