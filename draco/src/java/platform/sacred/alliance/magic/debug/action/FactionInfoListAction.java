package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.union.domain.Union;
import com.game.draco.debug.message.item.UnionInfoItem;
import com.game.draco.debug.message.request.C10061_FactionInfoListReqMessage;
import com.game.draco.debug.message.response.C10061_FactionInfoListRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;

public class FactionInfoListAction extends ActionSupport<C10061_FactionInfoListReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10061_FactionInfoListReqMessage reqMsg) {
		C10061_FactionInfoListRespMessage resp = new C10061_FactionInfoListRespMessage();
		try{
			//公会名称参数，如果为空表示查询所有公会
			String name = reqMsg.getName();
			Collection<Union> list;
			if(Util.isEmpty(name)){
				list = GameContext.getUnionApp().getUnionMap().values();
			}else{
				list = GameContext.getUnionApp().getUnionListByName(name);
			}
			if(Util.isEmpty(list)){
				return resp;
			}
			List<UnionInfoItem> rtList = new ArrayList<UnionInfoItem>();
			for(Union union : list){
				if(null == union){
					continue;
				}
				UnionInfoItem item = new UnionInfoItem();
				item.setUnionId(union.getUnionId());
				item.setUnionName(union.getUnionName());
				item.setUnionLevel(union.getUnionLevel());
				item.setLeaderId(union.getLeaderId());
				item.setLeaderName(union.getLeaderName());
				item.setCreateDate(new Date(union.getCreateTime()));
				item.setUnionDesc(union.getUnionDesc());
				item.setMemberNum(union.getUnionMemberList().size());
				item.setMaxMemberNum(GameContext.getUnionDataApp().getUnionUpgrade(union.getUnionLevel()).getMaxMemberNum());
				item.setPopularity(union.getPopularity());
				item.setMaxPopularity(GameContext.getUnionApp().getUnionDataMaxPopualrity(union.getUnionLevel()));
//				item.setProgress(union.getProgress());
				item.setMinProgress(GameContext.getUnionInstanceApp().getUnionKillBossRecord(union.getUnionId()).size());
				item.setMaxProgress(GameContext.getUnionDataApp().getActivityMaxBossNum());
				item.setCamp(union.getUnionCamp());
				rtList.add(item);
			}
			this.sortUnionList(rtList);
			resp.setUnionList(rtList);
			return resp;
		}catch(Exception e){
			this.logger.error("FactionInfoListAction error: ", e);
			return resp;
		}
	}
	
	private void sortUnionList(List<UnionInfoItem> factionInfoList){
		Collections.sort(factionInfoList, new Comparator<UnionInfoItem>() {
			public int compare(UnionInfoItem o1, UnionInfoItem o2) {
				int comparison =  o2.getUnionLevel() - o1.getUnionLevel();
				if(comparison == 0){
					comparison =  o2.getPopularity() - o1.getPopularity();
				}
				return comparison;
			}
		});
	}

}
