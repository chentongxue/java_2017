package com.easter.test.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sacred.alliance.magic.codec.impl.AutoLoadMessageMapping;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.RTSI;

public class MessageTest {
	
	public static void main(String[] args){
		AutoLoadMessageMapping maping = new AutoLoadMessageMapping();
		List<String> pkgList = new ArrayList<String>();
		pkgList.add("com.game.draco.message.request");
		pkgList.add("com.game.draco.message.response");
		pkgList.add("com.game.draco.message.push");
		pkgList.add("com.game.draco.debug.message.request");
		pkgList.add("com.game.draco.debug.message.response");
		maping.setPkgList(pkgList);
		System.out.println("********************************");
		System.out.println("The class name is different with it's commandId:");
		List<Short> list = new ArrayList<Short>();
		//自动加载指定package下面的Message
		Set<Class> clazzSet = RTSI.findClass(pkgList, Message.class);
		for(Class clazz : clazzSet){
			Message msg;
			try {
				msg = (Message)clazz.newInstance();
				short cmdId = msg.getCommandId();
				if(cmdId == 0){
					System.out.println("commandId = 0, " + msg.toString());
				}
				list.add(msg.getCommandId());
				//判断类名和命令字是否一致
				int abs_cmdId = Math.abs(cmdId);
				
				String pre = "C" + abs_cmdId;
				if(abs_cmdId < 10){
					pre = "C000" + abs_cmdId;
				}else if(abs_cmdId < 100){
					pre = "C00" + abs_cmdId;
				}else if(abs_cmdId < 1000){
					pre = "C0" + abs_cmdId;
				}
				String pre_info = pre + cmdId;
				String name = msg.getClass().getSimpleName();
				if(!name.startsWith(pre)){
					System.out.println("class: " + name + ", cmdId = " + cmdId);
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		System.out.println("********************************");
		Collections.sort(list, new Comparator<Short>(){
			@Override
			public int compare(Short arg0, Short arg1) {
				if(Math.abs(arg0) < Math.abs(arg1)){
					return -1;
				}
				if(Math.abs(arg0) > Math.abs(arg1)){
					return 1;
				}
				return 0;
			}
		});
		Set<Short> set = new HashSet<Short>();
		StringBuffer buff = new StringBuffer();
		for(Short cmdId : list){
			//System.out.println(cmdId);//打印所有命令字
			if(set.contains(cmdId)){
				buff.append(cmdId).append("\n");
			}
			set.add(cmdId);
		}
		System.out.println("---------------------------------");
		System.out.println("The repeat commandId such as: ");
		System.out.println(buff.toString());
		System.out.println("---------------------------------");
	}
	
}
