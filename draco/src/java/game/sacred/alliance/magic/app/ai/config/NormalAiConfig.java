
package sacred.alliance.magic.app.ai.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.game.draco.GameContext;
import com.game.draco.app.npc.type.NpcActionType;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.vo.Skill;

import lombok.Data;
import sacred.alliance.magic.app.ai.AiSkillConfig;
import sacred.alliance.magic.app.ai.BossAction;
import sacred.alliance.magic.util.Util;


public @Data class NormalAiConfig implements AiConfig {
    /**仇恨列表 此字段不是从配置文件获取,在init中设置*/
    private Set<String> hatredMonsterSet = new HashSet<String>();
    /**技能MAP 此字段不是从配置文件获取,在init中设置*/
    private Map<Integer, Integer> skillMap = new HashMap<Integer, Integer>();
    private AiSkillConfig[] skillConfigs = null;
    private String id;
    private int actionId = NpcActionType.ROOT.getType();
    /**是否主动怪物*/
    private boolean initiative;
    /**警戒距离(像素)*/
    private int alertArea;
    /**是否追逐*/
    private boolean chase;
    /**追击距离(像素)*/
    private int chaseArea;
    /**追击思考距离(像素)*/
    private int thinkArea ;
    //**************** 呼救 ****************//
    /**被攻击呼救（绝对值）*/
    private int shoutWorth;
    /**被攻击呼救（百分比）*/
    private float shoutRate;
    
    /**呼叫半径(像素)*/
    private int shoutArea;
    //**************** 逃跑 ****************//
    /**逃跑血量*/
    private int fleeWorth;
    /**逃跑百分比*/
    private float fleeRate;
    private String hatredMonsterID1;
    private String hatredMonsterID2;
    private String hatredMonsterID3;
    private String hatredMonsterID4;
    
    private String actionLinks ;
    //bossAction组
    private BossAction[][] bossActions ;
    
    //**************** 技能 ****************//
    /**普通技能ID*/
    private int normalSkill;
    private int normalskilllevel;
    
    //其他技能
    private float hpRate1 ;
    private int skill1 ;
    private int level1 ;
    private String shout1;

    private float hpRate2 ;
    private int skill2 ;
    private int level2 ;
    private String shout2;
    
    private float hpRate3 ;
    private int skill3 ;
    private int level3 ;
    private String shout3;
    
    private float hpRate4 ;
    private int skill4 ;
    private int level4 ;
    private String shout4;
    
    private float hpRate5 ;
    private int skill5 ;
    private int level5 ;
    private String shout5;
   
    /**警戒范围内有目标血量小于*/
   /* private double targethplessRate;
    private int skill3;
    private int skilllevel3;
    private int globalCoolDown;*/
    
    private String deathShout;
    private byte deathMusicId;
    /**
     * NPC刷怪配置
     */
    private int deathRefreshGroup;
    
    /**
     * 出生喊话
     */
    private String birthShout;
    
    /**
     * 死亡释放技能ID
     */
    private int deathSkillId;
    
    /**
     * buffId
     */
    private short bornBuffId;

	public void init() {
        this.addhatredMonster(hatredMonsterID1);
        this.addhatredMonster(hatredMonsterID2);
        this.addhatredMonster(hatredMonsterID3);
        this.addhatredMonster(hatredMonsterID4);
        if(null != this.bossActions){
        	//boss技能
        	for(BossAction[] bas : this.bossActions){
        		for(BossAction ba : bas){
        			this.skillMap.putAll(ba.getSkillMap());
        		}
        	}
        	this.thinkArea = getMinRange(skillMap);
        	return ;
        }
        //普通技能
        this.putSkillMap(normalSkill, normalskilllevel);
        this.putSkillMap(skill1, level1);
        this.putSkillMap(skill2, level2);
        this.putSkillMap(skill3, level3);
        this.putSkillMap(skill4, level4);
        this.putSkillMap(skill5, level5);
        //死亡释放的技能
        this.putSkillMap(deathSkillId, 1);
        //最小攻击距离
        this.thinkArea = getMinRange(skillMap);
        
        List<AiSkillConfig> list = new ArrayList<AiSkillConfig>();
        this.addSkillConfig(list, hpRate1, skill1, level1, shout1);
        this.addSkillConfig(list, hpRate2, skill2, level2, shout2);
        this.addSkillConfig(list, hpRate3, skill3, level3, shout3);
        this.addSkillConfig(list, hpRate4, skill4, level4, shout4);
        this.addSkillConfig(list, hpRate5, skill5, level5, shout5);
        if(0 == list.size()){
        	this.skillConfigs = new AiSkillConfig[0];
        	return ;
        }
        //对list进行排序,hpRate降序排列
        Collections.sort(list, new Comparator<AiSkillConfig>(){
			@Override
			public int compare(AiSkillConfig a1, AiSkillConfig a2) {
				if(a1.getHpRate() == a2.getHpRate()){
					return 0 ;
				}
				if(a1.getHpRate()>a2.getHpRate()){
					return -1 ;
				}
				return 1 ;
			}
        });
        this.skillConfigs =  new AiSkillConfig[list.size()];
        int index = 0 ;
        for(AiSkillConfig cfg : list){
        	skillConfigs[index++] = cfg ;
        }
        list.clear();
        list = null ;
    }
	
	
	private void addSkillConfig(List<AiSkillConfig> list,float hpRate,int skill,int level,String shout){
		if(skill <=0){
			return ;
		}
		AiSkillConfig cfg = new AiSkillConfig();
		cfg.setHpRate(hpRate);
		cfg.setSkill(skill);
		cfg.setShout(shout);
		cfg.setLevel(Math.max(1, level));
		list.add(cfg);
	}
	
	private void addhatredMonster(String id){
		if(!Util.isEmpty(id)){
			hatredMonsterSet.add(id);
		}
	}
	
	private void putSkillMap(int skill,int lv){
		if(skill >0){
			skillMap.put(skill, Math.max(lv, 1));
		}
	}
	
	private int getMinRange(Map<Integer, Integer> skillMap) {
		int minRange = Integer.MAX_VALUE;
		for(int skillId : skillMap.keySet()) {
    		int skillLevel = skillMap.get(skillId);
    		Skill skill = GameContext.getSkillApp().getSkill((short)skillId);
    		if(null == skill) {
    			continue;
    		}
    		SkillDetail sd = skill.getSkillDetail(skillLevel);
    		if(null == sd) {
    			continue;
    		}
    		int skillMinRange = sd.getMaxUseRange();
    		if(minRange < skillMinRange) {
    			continue;
    		}
    		minRange = skillMinRange;
    	}
		return minRange;
	}
	
}
