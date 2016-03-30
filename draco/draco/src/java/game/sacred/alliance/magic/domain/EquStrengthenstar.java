package sacred.alliance.magic.domain;

import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.base.GoodsStrengthenType;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.google.common.collect.Maps;


public @Data class EquStrengthenstar implements KeySupport<String> {

	public String getKey(){
		return String.valueOf(level);
	}
	
	//当前强化等级
	private int level;
	//成功率
	private int hitProb;
	//失败率0
	private int remainProb;
	//失败率1
	private int failProb;
	//保底符id
	private int stoneId ; 
	//保底符数量
	private int stoneNum;
	//材料ID
	private int materialId ;
	//材料数目
	private int materialNum ;
	//所需花费的金钱
	private int silverMoney;
	//最大惩罚掉级数值
	private int maxRelegation;
	//强化成功广播信息
	private String broadcastInfo ;
	//显示成功率
	private int showHitProb ;
	
	
	/**
	 * 下面字段配置文件中不存在
	 */
	private GoodsBase materialGoods ;
	private GoodsBase stoneGoods ;
	
	//最大成功率100%=10000
	private static final int SUCCESS_PROB = 10000;
	//强化成功装备增加等级参数
	private static final int SUCCESS_ADD_LV = 1;
	//强化失败，惩罚减等级参数（不惩罚）
	private static final int REMAIN_ADD_LV = 0;
	//强化失败，降级惩罚标识（具体惩罚数值，由最大降级数决定，系统随机获得）
	private static final int FAIL_FLAG = -1;
	private Map<Integer,Integer> lockPropMap = Maps.newHashMap();
	
	public void init(){
		int allProb = this.hitProb + this.remainProb + this.failProb ;
		if(SUCCESS_PROB != allProb){
			//配置错误,不允许服务器启动
			Log4jManager.CHECK.error("GoodsStrengthenstar error: allProb!=" + SUCCESS_PROB + " level=" + level );
			Log4jManager.checkFail();
		}
		if(this.maxRelegation<0){
			this.maxRelegation = 0 ;
		}
		this.lockPropMap.put(SUCCESS_ADD_LV, this.hitProb);
		this.lockPropMap.put(REMAIN_ADD_LV, this.remainProb);
		this.lockPropMap.put(FAIL_FLAG, this.failProb);
		//构建广播信息中的未变动部分
		if(!Util.isEmpty(this.broadcastInfo)){
			String replace = this.broadcastInfo.replace(Wildcard.Strengthen_Level, String.valueOf(this.level));
			this.broadcastInfo = replace ;
		}
		
		GoodsBase material = GameContext.getGoodsApp().getGoodsBase(this.materialId);
		this.materialGoods = material ;
		
		GoodsBase stone = GameContext.getGoodsApp().getGoodsBase(this.stoneId);
		this.stoneGoods = stone ;
	}
	
	
	public String getBroadcastTips(RoleInstance role,int goodsId){
		if(Util.isEmpty(this.broadcastInfo)){
			return "" ;
		}
		return this.broadcastInfo.replace(Wildcard.Role_Name, role.getRoleName())
			.replace(Wildcard.GoodsName, Wildcard.getChatGoodsName(goodsId, ChannelType.Publicize_Personal));
	}
	
	
	
	/**
	 * 随机获得强化结果
	 * @return
	 */
	public int getStrengthenResult(GoodsStrengthenType st ){
		if(SUCCESS_PROB <=  this.hitProb){
			return SUCCESS_ADD_LV;
		}
		Integer addLv = Util.getWeightCalct(this.lockPropMap);
		if(null == addLv){
			return 0 ;
		}
		if(FAIL_FLAG != addLv){
			//没有降级
			return addLv ;
		}
		//降级情况
		if(st == GoodsStrengthenType.no_downgrade){
			//保底
			return 0 ;
		}
		if(this.maxRelegation <=1){
			return this.maxRelegation*-1 ;
		}
		return sacred.alliance.magic.util.Util.randomInt(1, this.maxRelegation)*-1;
	}
	
}
