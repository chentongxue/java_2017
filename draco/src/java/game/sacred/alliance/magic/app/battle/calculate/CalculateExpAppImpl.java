package sacred.alliance.magic.app.battle.calculate;

import java.util.List;

import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.RateType;
import sacred.alliance.magic.domain.Rate;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;

public class CalculateExpAppImpl implements CalculateExpApp {
	private static final float P_EXP = 10000f ;
    private static final float TEAM_MEMBER_2_RATE = 0.67f ;
    private static final float TEAM_MEMBER_3_RATE = 0.56f ;
    private static final float TEAM_MEMBER_4_RATE = 0.50f ;

	private static final Rate SYSTEM_EXP_NULL_RATE = new Rate();
	
	//private final Logger logger = LoggerFactory.getLogger(this.getClass());

	
	/*
	    单人获得经验是100%，2人组队每人获得67%，3人组队每人获得56%，4人组队每人获得50%的经验

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
			//判断地图是否野外地图
			AttributeType at = AttributeType.exp ;
			if(this.isDefaultLogicMap(currentRole)){
				at = AttributeType.expHook ;
			}
			GameContext.getUserAttributeApp().changeAttribute(currentRole, at , OperatorType.Add, realExp,OutputConsumeType.monster_fall);
			currentRole.getBehavior().notifyAttribute();
		}
	}
	
	/**
	 * 判断当前是否野外地图
	 * @param role
	 * @return
	 */
	private boolean isDefaultLogicMap(RoleInstance role){
		MapInstance mapInstance = role.getMapInstance() ;
		if(null == mapInstance){
			return false ;
		}
		return mapInstance.mapLogicType() == MapLogicType.defaultLogic.getType();
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
		int npcExp =  dieNpc.getNpc().getExp();
        int teamSize = members.size() ;
		return (int)(npcExp*this.getTeamExpRate(teamSize));
	}


    private  float getTeamExpRate(int size){
        switch(size){
            case 1:
                return 1 ;
            case 2:
                return TEAM_MEMBER_2_RATE ;
            case 3:
                return TEAM_MEMBER_3_RATE ;
            default:
                return TEAM_MEMBER_4_RATE ;
        }
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
