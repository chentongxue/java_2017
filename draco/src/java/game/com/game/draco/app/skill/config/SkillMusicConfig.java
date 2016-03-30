//package com.game.draco.app.skill.config;
//
//import java.util.List;
//
//import lombok.Data;
//import sacred.alliance.magic.util.Initable;
//import sacred.alliance.magic.util.KeySupport;
//import sacred.alliance.magic.util.Log4jManager;
//import sacred.alliance.magic.util.Util;
//
//import com.google.common.collect.Lists;
//import com.google.common.primitives.Shorts;
//
//public @Data class SkillMusicConfig implements KeySupport<Short> ,Initable {
//
//	private short skillId ;
//	private String musics ;
//	
//	private short[] musicIds ;
//	
//	@Override
//	public Short getKey(){
//		return this.skillId ;
//	}
//
//	@Override
//	public void init() {
//		try {
//			String[] arr = Util.splitString(musics);
//			if (null == arr) {
//				return;
//			}
//			List<Short> shortList = Lists.newArrayList();
//			for (String s : arr) {
//				shortList.add(Short.parseShort(s.trim()));
//			}
//			this.musicIds = Shorts.toArray(shortList);
//		}catch(Exception ex){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("skill music config error,skillId=" + skillId ,ex);
//		}
//	}
//}
