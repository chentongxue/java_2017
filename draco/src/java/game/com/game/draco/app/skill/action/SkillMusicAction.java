package com.game.draco.app.skill.action;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

import com.game.draco.GameContext;
import com.game.draco.message.item.SkillMusicItem;
import com.game.draco.message.request.C0307_SkillMusicReqMessage;
import com.game.draco.message.response.C0307_SkillMusicRespMessage;
import com.google.common.collect.Lists;


public class SkillMusicAction extends BaseAction<C0307_SkillMusicReqMessage>{

	@Override
	public Message execute(ActionContext context, C0307_SkillMusicReqMessage reqMsg) {
		C0307_SkillMusicRespMessage respMsg = new C0307_SkillMusicRespMessage() ;
		Map<Short,short []> configList = GameContext.getSkillApp().getAllSkillMusicConfig() ;
		if(null == configList){
			return respMsg ;
		}
		List<SkillMusicItem> musicList = Lists.newArrayList() ;
		for(Entry<Short,short []> music : configList.entrySet()){
			if(null == music.getValue() 
					|| 0 == music.getValue().length){
				continue ;
			}
			SkillMusicItem item = new SkillMusicItem();
			item.setSkillId(music.getKey());
			item.setMusics(music.getValue());
			musicList.add(item);
		}
		respMsg.setMusicList(musicList);
		return respMsg ;
	}

}
