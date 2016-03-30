package sacred.alliance.magic.app.battle.calculate;

import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.RateType;
import sacred.alliance.magic.domain.Rate;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

public class CalculateExpAppImpl implements CalculateExpApp {
	private static final int P_1 = 6 ;
	private static final int P_2 = 15 ;
	private static final float P_3 = 0.08f ;
	private static final float TEAM_EXP_RATE = 0.4f ;// 0.15f ;
	private static final float P_EXP = 10000f ;
	private static final Rate SYSTEM_EXP_NULL_RATE = new Rate();
	
	//private final Logger logger = LoggerFactory.getLogger(this.getClass());

	
	/*【组队经验奖励百分比】=40%
	玩家获得的基础分配经验=【怪物经验】*【玩家等级】/（【玩家等级】+【队友等级和】）*（1+（【队伍人数】-1）*（【组队经验奖励百分比】+【其他加成】））*（1+【双倍经验】）
	注：【其他加成】数值来源于其他系统的加成，如【情侣加成】，【师徒加成】，【好友加成】等。【组队】占40%，【情侣】占30%，【师徒】占30%，【好友】占30%。
	*/
	
	/*
	 【等级参数1】=6，【等级参数2】=15，【惩罚参数2】=8%。
判定1：
若：【玩家等级】<【怪物等级】-【等级参数2】，则【最终获得经验】=1。
否则进入判定2
判定2：
若：【怪物等级】-【等级参数2】<=【玩家等级】<【怪物等级】-【等级参数1】，则【最终获得经验】=【基础分配经验】*（1-（【怪物等级】-【等级参数1】-【玩家等级】）*【惩罚参数2】）
否则进入判定3
判定3：
若：【怪物等级】-【等级参数1】<=【玩家等级】<=【怪物等级】+【等级参数1】，则【最终获得经验】=【基础分配经验】
否则进入判定4
判定4：
若：【怪物等级】+【等级参数1】<【玩家等级】<【怪物等级】+【等级参数2】，则【最终获得经验】=【基础分配经验】*（1-（【玩家等级】-【等级参数1】-【怪物等级】）*【惩罚参数2】）
否则【最终获得经验】=1。 
	 */
	public void calculateExp(RoleInstance role, NpcInstance dieNpc) {
		List<AbstractRole> members = GameContext.getTeamApp().getTeamMembersInSameMap(role); // 队伍活着的人数
		if(null == members || 0 == members.size()){
			return ;
		}
		//Team team = role.getTeam();
		//boolean haveTeam = (null != team) && team.getPlayerNum() > 1 ;
		for (AbstractRole m : members) {
			RoleInstance currentRole = (RoleInstance)m;
			int exp = this.getExp(currentRole, dieNpc, members);
			if(exp <=0){
				continue ;
			}
			//计算自己的经验倍率等
			//获得经验 × （ 1 + 普通因素之和 ） × （ 1 + 超额因素之和 ）= 获得经验 × （ 1 + 个人普通因素之和 + 服务器普通系数） × （ 1 + 超额因素之和 + 服务器超额系数 ）
			Rate sysRate = this.getSystemExpRate(currentRole);
			float commonRate = 1+(currentRole.get(AttributeType.expAddRate) + sysRate.getRate())/P_EXP ;
			float nonCommonRate = 1+(currentRole.get(AttributeType.expMultRate) + sysRate.getRate1())/P_EXP ;
			float fexp = exp *commonRate*nonCommonRate;
			if(fexp<=0){
				continue ;
			}
			int realExp = Integer.MAX_VALUE ;
			if(fexp< Integer.MAX_VALUE){
				realExp = (int)fexp ;
			}
			GameContext.getUserAttributeApp().changeAttribute(currentRole, AttributeType.exp, OperatorType.Add, realExp,OutputConsumeType.monster_fall);
			currentRole.getBehavior().notifyAttribute();
		}
	}
	
	private Rate getSystemExpRate(AbstractRole role){
		Rate rate = GameContext.getRateApp().getRateByType(RateType.system_exp_award);
		if(null == rate || !rate.inTime()){
			//为空或者过期
			return SYSTEM_EXP_NULL_RATE ;
		}
		return rate ;
	}
	
	private float getOtherExpRate(RoleInstance role){
		//TODO:
		return 0 ;
	}
	
	private int calExp(RoleInstance role,NpcInstance dieNpc,List<AbstractRole> members){
		int roleLevel = role.getLevel();
		int npcLevel = dieNpc.getNpc().getLevel();
		//【玩家等级】<【怪物等级】-【等级参数2】，则【最终获得经验】=1。
		if(roleLevel < npcLevel -P_2){
			return 1 ;
		}
		int npcExp =  dieNpc.getNpc().getExp();
		int totalLevel = 0 ;
		for(AbstractRole m : members){
			totalLevel += m.getLevel();
		}
		//float teamRate = haveTeam?TEAM_EXP_RATE:0 ;
		float baseExp = npcExp/(float)totalLevel *roleLevel * (1 + ((members.size() -1) *(TEAM_EXP_RATE+this.getOtherExpRate(role))));
		//【怪物等级】-【等级参数2】<=【玩家等级】<【怪物等级】-【等级参数1】，
		//则【最终获得经验】=【基础分配经验】*（1-（【怪物等级】-【等级参数1】-【玩家等级】）*【惩罚参数2】）
		if(npcLevel-P_2<= roleLevel && (roleLevel<npcLevel-P_1)){
			return (int)(baseExp*(1-(npcLevel-P_1-roleLevel)*P_3)) ;
		}
		//【怪物等级】-【等级参数1】<=【玩家等级】<=【怪物等级】+【等级参数1】，则【最终获得经验】=【基础分配经验】
		if (npcLevel-P_1<= roleLevel && roleLevel<= npcLevel + P_1){
			return (int)baseExp ;
		}
		//【怪物等级】+【等级参数1】<【玩家等级】<【怪物等级】+【等级参数2】，
		//则【最终获得经验】=【基础分配经验】*（1-（【玩家等级】-【等级参数1】-【怪物等级】）*【惩罚参数2】）

		if(npcLevel + P_1 < roleLevel && roleLevel< npcLevel + P_2){
			return (int)(baseExp*(1-(roleLevel-P_1-npcLevel)*P_3)) ;
		}
		return 1 ;
	}
	
	private int getExp(RoleInstance role,NpcInstance dieNpc,List<AbstractRole> members){
		/*int roleLevel = role.getLevel();
		RoleLevelup roleLevelup = GameContext.getAttriApp().getLevelup(roleLevel, CareerType.getType(role.getCareer()));
		if(null == roleLevelup){
			return 0;
		}*/
		int exp = this.calExp(role, dieNpc, members);
		if(exp <=0){
			return 0 ;
		}
		/*int maxExp = roleLevelup.getMaxExp();//当前等级最大经验
		int roleExp = role.getExp() + exp;
		if (roleExp > maxExp) {
			return maxExp - role.getExp();
		}
		*/
		return exp;
	}
	
	
	
	
}
