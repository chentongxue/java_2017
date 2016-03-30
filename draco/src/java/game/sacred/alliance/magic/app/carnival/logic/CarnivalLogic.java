package sacred.alliance.magic.app.carnival.logic;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.type.MailSendRoleType;

import sacred.alliance.magic.app.carnival.CarnivalReward;
import sacred.alliance.magic.app.carnival.CarnivalRule;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.CondCompareType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.domain.CarnivalRankInfo;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class CarnivalLogic {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected static final int RANK_SIZE = 3;
	protected static final byte ALL_REWARD_RANK = -1;
	
	/**
	 * 获取排行
	 * @param carnivalRule
	 * @param itemId
	 * @return
	 */
	public abstract List<CarnivalRankInfo> getCarnivalRank(CarnivalRule carnivalRule, int itemId);
	
	/**
	 * 获取满足条件的人员列表
	 * @param carnivalRule
	 * @param itemId
	 * @return
	 */
	protected abstract Collection<CarnivalRankInfo> getCarnivalReward(CarnivalRule carnivalRule, int itemId);
	
	protected boolean isMeet(CarnivalRule carnivalRule, int value){
		return CondCompareType.isMeet(carnivalRule.getConditionCompareType()
				, value, carnivalRule.getMinValue()
				, carnivalRule.getMaxValue(), carnivalRule.getCondOrValueList());
	}
	
	public void printRewardLog(int id, String roleId, String roleName) {
		try{
			StringBuffer sb = new StringBuffer();
			sb.append(id);
			sb.append(Cat.pound);
			sb.append(roleId);
			sb.append(Cat.pound);
			sb.append(roleName);
			Log4jManager.CARNIVAL_ALL_REWARD.info(sb.toString());
		}catch(Exception e){
			logger.error("saveRoleSummonLog error:",e);
		}
	}
	
	protected static enum RewardType {
		all((byte)0),
		rank((byte)1),
		;
		private final byte type ;
		
		RewardType(byte type){
			this.type = type ;
		}
		public byte getType() {
			return type;
		}
	}
	
	/**
	 * 活动结束发奖
	 * @param itemId
	 * @param itemName
	 * @param carnivalRule
	 * @param rankRewardRole
	 */
	public void reward(int itemId, CarnivalRule carnivalRule, Collection<CarnivalRankInfo> rankRewardRole){
		rewardRank(itemId, rankRewardRole);//给符合排名的发奖
		rewardMeetCondition(itemId, carnivalRule);//给符合条件的发奖
	}
	
	/**
	 * 给符合条件的人发奖
	 * @param itemId
	 * @param carnivalRule
	 */
	protected void rewardMeetCondition(int itemId, CarnivalRule carnivalRule) {
		try{
			Collection<CarnivalRankInfo> allReward = this.getCarnivalReward(carnivalRule, itemId);
			if(Util.isEmpty(allReward)) {
				return;
			}
			this.reward(allReward, itemId, RewardType.all);
		}catch(Exception e){
			logger.error("rewardMeetCondition error:",e);
		}
	}
	
	/**
	 * 给符合排名的人发奖
	 * @param itemId
	 * @param allRewardRole
	 * @param rewardType
	 */
	protected void rewardRank(int itemId, Collection<CarnivalRankInfo> rankReward){
		try{
			if(Util.isEmpty(rankReward)) {
				return;
			}
			this.reward(rankReward, itemId, RewardType.rank);
		}catch(Exception e){
			logger.error("rewardRank error:",e);
		}
	}
	
	/**
	 * 发奖
	 * @param allRewardRole
	 * @param itemId
	 * @param rewardType
	 */
	protected void reward(Collection<CarnivalRankInfo> rewardRole, int itemId, RewardType rewardType){
		for(CarnivalRankInfo carnivalRankInfo : rewardRole) {
			int rank = carnivalRankInfo.getRank();
			byte career = carnivalRankInfo.getCareer();
			CarnivalReward carnivalReward = GameContext.getCarnivalApp().getCarnivalReward(itemId, rank, career);
			sendMail(carnivalRankInfo.getTargetId(), carnivalReward);
			printLog(carnivalRankInfo.getTargetId(), rewardType.getType(), itemId);
		}
	}
	
	/**
	 * 发邮件(奖励)
	 * @param roleId
	 * @param goodsList
	 */
	private void sendMail(String roleId,CarnivalReward carnivalReward){
		OutputConsumeType ocType = OutputConsumeType.carnival_reward;
		GameContext.getMailApp().sendMail(roleId, carnivalReward.getMailTitle(), 
				carnivalReward.getMailContent(), 
				MailSendRoleType.System.getName(),
				ocType.getType(),
				0,0, carnivalReward.getGoodsList());
	}
	
	/**
	 * 打印日志 id#roleId#type(奖励类型 0:符合条件 1:排名奖励)
	 * @param roleId
	 * @param type
	 */
	public void printLog(String roleId, byte type, int itemId) {
		try{
			StringBuffer sb = new StringBuffer();
			sb.append("");
			sb.append(Cat.pound);
			sb.append(roleId);
			sb.append(Cat.pound);
			sb.append(type);
			Log4jManager.CARNIVAL_ALREADY_REWARD.info(sb.toString());
		}catch(Exception e){
			logger.error("printLog error:",e);
		}
	}
	
	protected int getAttri(RoleInstance role, AttributeType attriType){
		if(attriType == AttributeType.battleScore){
			return GameContext.getAttriApp().getEffectBattleScore(role);
		}
		return role.get(attriType);
	}
}
