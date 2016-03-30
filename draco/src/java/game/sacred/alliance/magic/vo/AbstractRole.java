package sacred.alliance.magic.vo;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.ai.Ai;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.base.StateType;
import sacred.alliance.magic.base.SwitchLineType;
import sacred.alliance.magic.domain.RolePayRecord;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;
import com.game.draco.app.buff.BuffLostType;
import com.game.draco.app.buff.BuffTimeType;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.google.common.collect.Lists;

public abstract class AbstractRole extends RoleEntity{
	private final Object isDeathLock = new byte[0];
	protected static final Logger logger = LoggerFactory.getLogger(AbstractRole.class);
	private byte[] stateLock = new byte[0] ;

	public AbstractRole(){
		
	}
	
	private int speed ; 
	/*****/
	private int curHP;// 当前生命值
	private int maxHP;// 生命值上限
	private short state;//状态
	protected int exp;// 经验
	
	private byte campId ;//阵营
	
	//private Direction dir;
	private byte dir = Direction.DOWN.getType();

	protected int level;// 级别

	private String mapId;

	private int mapX;

	private int mapY;

	private RoleEffect roleEffect = new RoleEffect();

	protected Queue<BuffStat> receiveBuff = new ConcurrentLinkedQueue<BuffStat>();
	
	protected Map<Short,RoleSkillStat> skillMap = new HashMap<Short,RoleSkillStat>();

	private AbstractRole target;

	private Ai ai;

	private Point rebornPoint;

	private Path walkPath;

	private MapInstance mapInstance;
	
	private HatredTarget hatredTarget = new HatredTarget(this);

	private AbstractRoleBehavior behavior;
	
	//分线id
	private int lineId = -1;
	private SwitchLineType switchLineType = SwitchLineType.Automatic;
	
    //作用是如果玩家死亡则不再发死亡通知
	private AtomicBoolean hasSendDeathMsg = new AtomicBoolean(false);
	
	protected int maxExp;
	
	/***
	 * 上次技能使用时间
	 * 考虑cd
	 */
	private long lastSkillProcessTime ;
	
	/**
	 * 上次使用技能时间
	 * 不考虑cd
	 */
	@Getter @Setter private long lastUseSkillTime ;
	
	private long netDelayTime = 0;
	
	private int skillMsgIndex = 0;
	//是否改变目标，如果不改变目标则发送不带targetId的消息
	private AtomicBoolean changeTarget = new AtomicBoolean(false);

	private int topExp;// 当前级最高经验值
	private long totalExp;
	
	//缓存角色属性
	private Map<Byte,AttriItem> attriCache = new HashMap<Byte,AttriItem>();
	
	private int color = -1;
	private byte zoom;
	
	private Date createTime; //创建时间
	
	public int getLoseHp() {
		return getMaxHP() - getCurHP();
	}
	
	public long getTotalExp() {
		return totalExp;
	}

	public void setTotalExp(long totalExp) {
		this.totalExp = totalExp;
	}
	
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
	public byte getZoom() {
		return zoom;
	}

	public void setZoom(byte zoom) {
		this.zoom = zoom;
	}

	/** 
	 * role 所属伤害系
	 * @return
	 */
	public abstract byte getAttriSeriesId();
	
	/**
	 * role 伤害系的档
	 * @return
	 */
	public abstract byte getAttriGearId();
	
	public RolePayRecord getRolePayRecord() {
		return null;
	}
	/**
	 * 在出生,复活情况下修改curHP,不能调用此方法,必须调用setCurHP
	 * @param enumValue
	 * @param value
	 * @return
	 */
	public boolean set(byte enumValue,int value){
		AttributeType attriType = AttributeType.get(enumValue);
		switch(attriType){
			case loseHp:
				return true ;
		//1.高级属性
			case curHP:
				{	
					synchronized(isDeathLock){
						if(this.isDeath()){
							//已经死亡,不能再修改
							return false ;
						}
						if (value > this.getMaxHP()) {
							value = this.getMaxHP();
						}
						this.setCurHP(value); 
						return true;
					}
				}
			case exp:
				{
//					if (value > this.getMaxExp()) {
//						value = this.getMaxExp();
//					}
					this.setExp(value); return true;
				}
			
			case maxHP:this.setMaxHP(value); return true;

			case speed:this.setSpeed(value); return true;
			case state:this.setState((byte)value); return true;
			case maxExp:this.setMaxExp(value); return true;
			case level:this.setLevel(value);return true;
			case expChange: return true;
			case lq :
				return true ;
			case petAtk:
				return true;
			default:  this.roleEffect.set(enumValue, value) ;return true;
		}
	}
	
	public int get(byte typeValue){
		AttributeType attriType = AttributeType.get(typeValue);
		return get(attriType);
	}
	
	public int get(AttributeType attriType){
		if(null == attriType){
			return 0 ;
		}
		switch(attriType){
			case curHP:return this.getCurHP();
			case exp:return this.getExp();
			case speed:return this.getSpeed(); 
			case state:return this.getState();
					
			case maxHP:return this.getMaxHP();
			case maxExp:return this.getMaxExp();
			
			case level:return this.getLevel();
			case expChange:return 0 ;
			case loseHp : return this.getLoseHp() ;
			case lq : return 0;
			default: return this.getRoleEffect().get(attriType);
		}
	}
	
	/**获得目标的势力关系*/
    public ForceRelation getForceRelation(AbstractRole target) {
    	if (null == this.getMapInstance()) {
			return this.getCampId() == target.getCampId() ? ForceRelation.friend
					: ForceRelation.enemy;
		}
		return this.getMapInstance().getForceRelation(this,target);
    }

	public RoleEffect getRoleEffect() {
		return roleEffect;
	}

	public void setRoleEffect(RoleEffect roleEffect) {
		this.roleEffect = roleEffect;
	}

	public int getLevel() {
		if(level <=0){
			this.level = 1 ;
		}
		return level;
	}

	public void setLevel(int level) {
		if(level <=0){
			this.level = 1 ;
		}
		this.level = level;
	}

	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public int getMapX() {
		return mapX;
	}

	public void setMapX(int mapX) {
		this.mapX = mapX;
	}

	public int getMapY() {
		return mapY;
	}

	public void setMapY(int mapY) {
		this.mapY = mapY;
	}
	
	public Queue<BuffStat> getReceiveBuffCopy(){
		return receiveBuff;
	}

	public void setReceiveBuff(Collection<BuffStat> receiveBuff) {
		this.receiveBuff.clear();
		this.receiveBuff.addAll(receiveBuff);
	}
	
	public void delAllBuffStat(){
		//this.receiveBuff.clear();
		for (BuffStat stat : getReceiveBuffCopy()) {
			GameContext.getUserBuffApp().delBuffStat(this, stat,
					false);
		}
	}
	
	public void delAllBuffStat(BuffLostType type) {
		for (BuffStat stat : getReceiveBuffCopy()) {
			Buff buff = stat.getBuff();
			if(BuffLostType.offlineLost == type && !buff.isOfflineLost()){
				continue;
			}else if(BuffLostType.dieLost == type && !buff.isDieLost()) {
				continue;
			}else if(BuffLostType.transLost == type && !buff.isTransLost()){
				continue;
			}else if(BuffLostType.exitInsLost == type && !buff.isExitInsLost()){
				continue;
			}else if(BuffLostType.swtichHero == type && buff.isSwitchOn()){
				continue;
			}
			GameContext.getUserBuffApp().delBuffStat(this, stat,
					false);
		}
	}
	
	public void delBuffStat(BuffStat receiveBuff){
		this.receiveBuff.remove(receiveBuff);
	}

	public void addBuffStat(BuffStat buffStat) {
		if(null == buffStat || null == buffStat.getBuff()){
			return ;
		}
		//非持久buff不需要添加到用户buff列表
		if(buffStat.getBuff().getTimeType() != BuffTimeType.continued){
			return ;
		}
		this.receiveBuff.add(buffStat);
	}
	
	
	public BuffStat getBuffStat(short buffId) {
		for (BuffStat buffStat : receiveBuff) {
			if (buffId == buffStat.getBuffId()){
				return buffStat;
			}
		}
		return null;
	}
	
	public RoleSkillStat getSkillStat(short skillId) {
		return this.skillMap.get(skillId);
	}
	
	public boolean hasLearnSkill(short skillId) {
		return null != this.getSkillStat(skillId);
	}
	
	public boolean hasBuff(short buffId) {
		return null != this.getBuffStat(buffId);
	}
	
	public boolean hasBuff(short buffId,AbstractRole caster) {
		return null != this.getBuffStat(buffId,caster);
	}
	
	public int getBuffLevel(short buffId){
		BuffStat stat = this.getBuffStat(buffId);
		return (null == stat)?0:stat.getBuffLevel();
	}
	
	public int getBuffLevel(short buffId,AbstractRole caster){
		BuffStat stat = this.getBuffStat(buffId,caster);
		return (null == stat)?0:stat.getBuffLevel();
	}
	
	public BuffStat getBuffStat(short buffId,AbstractRole caster){
		for (BuffStat buffStat : receiveBuff) {
			if (buffId == buffStat.getBuffId() 
					&& null != buffStat.getCaster()
					&& buffStat.getCaster().getRoleId().equals(caster.getRoleId())){
				return buffStat ;
			}
		}
		return null ;
	}

	public void addSkillStat(RoleSkillStat stat) {
		skillMap.put(stat.getSkillId(), stat);
	}
	
	public void delSkillStat(short skillId) {
		skillMap.remove(skillId);
	}

	public int getExp() {
		return exp;
	}

	
	/**登陆时ibatis调用和初始化***/
	public void setExp(int exp) {
		this.exp = Util.maxZero(exp);
	}
	
	public int getTopExp() {
		return topExp;
	}

	public void setTopExp(int topExp) {
		this.topExp = topExp;
	}
	
	public Ai getAi() {
		return ai;
	}

	public void setAi(Ai ai) {
		this.ai = ai;
	}


	public Point getRebornPoint() {
		return rebornPoint;
	}

	public void setRebornPoint(Point rebornPoint) {
		this.rebornPoint = rebornPoint;
	}

	public byte getDir() {
		return dir;
	}

	public void setDir(byte dir) {
		this.dir = dir;
	}


	public MapInstance getMapInstance() {
		return mapInstance;
	}

	public void setMapInstance(MapInstance mapInstance) {
		this.mapInstance = mapInstance;
	}

	public AbstractRoleBehavior getBehavior() {
		return behavior;
	}

	public void setBehavior(AbstractRoleBehavior behavior) {
		this.behavior = behavior;
	}

	public Point getCurrentPoint() {
		return new Point(mapId, mapX, mapY);
	}

	public Path getWalkPath() {
		return walkPath;
	}

	public void setWalkPath(Path walkPath) {
		this.walkPath = walkPath;
	}

	public AbstractRole getTarget() {
		return target;
	}

	public void setTarget(AbstractRole target) {
		if(target == null) {
			this.target = target;
			return;
		}
		if(this.target == null || !this.target.getRoleId().equals(target.getRoleId())) {
			changeTarget.compareAndSet(false, true);
		}
		this.target = target;
	}

	public boolean isDeath() {
		synchronized(isDeathLock){
			return (this.curHP <= 0);
		}
	}

	public int getCurHP() {
		return curHP;
	}

	public void setCurHP(int curHP) {
		synchronized (isDeathLock) {
			if(curHP < 0) curHP = 0;
			this.curHP = curHP;
		}
	}


	public int getMaxHP() {
		return maxHP;
	}

	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
		if(this.curHP > this.maxHP){
			this.curHP = this.maxHP;
		}
	}


	public short getState() {
		
		return state;
	}
	
	/*
	public static void addState(RoleInstance role,StateType st){
		System.out.println("");
		System.out.println("***********************************");
		System.out.println("==== state=" + role.getState());
		System.out.println("==== add state=" + st+ " " + st.getType());
		role.addState(st);
		System.out.println("==== state=" + role.getState());
		System.out.println("***********************************");
		System.out.println("");
	}
	
	public static void removeState(RoleInstance role,StateType st){
		System.out.println("");
		System.out.println("***********************************");
		System.out.println("==== state=" + role.getState());
		System.out.println("==== remove state=" + st+ " " + st.getType());
		role.removeState(st);
		System.out.println("==== state=" + role.getState());
		System.out.println("***********************************");
		System.out.println("");
	}
	
	public static void main(String[] args){
		RoleInstance role = new RoleInstance();
		
		addState(role,StateType.coma);
		addState(role,StateType.disarm);
		addState(role,StateType.coma);
		
		removeState(role,StateType.coma);
		removeState(role,StateType.coma);
		removeState(role,StateType.disarm);
	}
*/
	
	public void setState(byte state) {
		//stat不是通过set方法修改的,而是通过addState方法,所以下面的代码必须注释
		//this.state = state;
	}
	
	public boolean inState(StateType state){
		synchronized (stateLock) {
			if(null == state){
				return false;
			}
			return (this.state & state.getCode())!=0;
		}
	}
	
	public void clearState(){
		synchronized (stateLock){
			this.state = (byte)0 ;
		}
	}
	
	public void addState(StateType state){
		synchronized (stateLock){
			if(null == state){
				return ;
			}
		
			this.state = (short)(this.state | state.getCode());
			
		}
	}
	
	public void removeState(StateType state){
		synchronized (stateLock){
			if(null == state || !inState(state)){
				return ;
			}
			this.state = (short)(this.state ^ state.getCode());
		}
	}
	

	@Override
	public boolean equals(Object other){
		if(null == other || !(other instanceof AbstractRole)){
			return false ;
		}
		AbstractRole ar = (AbstractRole)other ;
		return ar.getRoleId().equals(this.getRoleId());
	}
	
	@Override
	public int hashCode(){
		return this.getIntRoleId() ;
	}
	
	/**
	 * 系攻击值=atk + 系攻击值
	 * @param type
	 * @return
	 */
	public int getAtkAttrDisplay(AttributeType type) {
		return this.get(type) + this.get(AttributeType.atk);
	}
	
	/**
	 * 系抵抗值=rit + 系抵抗值
	 * @param type
	 * @return
	 */
	public int getRitAttrDisplay(AttributeType type) {
		return this.get(type) + this.get(AttributeType.rit);
	}
	
	public AtomicBoolean getHasSendDeathMsg() {
		return hasSendDeathMsg;
	}

	public int getMaxExp() {
		return maxExp;
	}

	public void setMaxExp(int maxExp) {
		this.maxExp = maxExp;
	}

	public long getLastSkillProcessTime() {
		return lastSkillProcessTime;
	}

	public void setLastSkillProcessTime(long lastSkillProcessTime) {
		this.lastSkillProcessTime = lastSkillProcessTime;
	}
	
	public int getSkillLevel(short skillId) {
		RoleSkillStat skillStat = this.getSkillStat(skillId);
		if (null == skillStat) {
			return 0;
		}
		return skillStat.getSkillLevel();
	}
	
	public int getSkillEffectLevel(short skillId){
		RoleSkillStat skillStat = this.getSkillStat(skillId);
		if (null == skillStat) {
			return 0;
		}
		return skillStat.getSkillLevel() + skillStat.getAddSkillLevel();
	}
	

	public HatredTarget getHatredTarget() {
		return hatredTarget;
	}

	public long getNetDelayTime() {
		return netDelayTime;
	}

	public void setNetDelayTime(long netDelayTime) {
		this.netDelayTime = netDelayTime;
	}

	public int getSkillMsgIndex() {
		return skillMsgIndex;
	}

	public void setSkillMsgIndex(int skillMsgIndex) {
		this.skillMsgIndex = skillMsgIndex;
	}

	

	public AtomicBoolean getChangeTarget() {
		return changeTarget;
	}

	public int getLineId() {
		return lineId;
	}

	public void setLineId(int lineId) {
		this.lineId = lineId;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		if(speed < 0){
			speed = 0;
			return ;
		}
		this.speed = speed;
	}

	public Map<Byte, AttriItem> getAttriCache() {
		return attriCache;
	}

	public Map<Short, RoleSkillStat> getSkillMap() {
		return skillMap;
	}

	public Map<Short, RoleSkillStat> getCurrentSkillMap() {
		return this.getSkillMap();
	}
	public void setSkillMap(Map<Short, RoleSkillStat> skillMap) {
		this.skillMap = skillMap;
	}
	
	
	public abstract String getRoleName() ;

	public byte getCampId() {
		return campId;
	}

	public void setCampId(byte campId) {
		this.campId = campId;
	}

	public SwitchLineType getSwitchLineType() {
		return switchLineType;
	}

	public void setSwitchLineType(SwitchLineType switchLineType) {
		this.switchLineType = switchLineType;
	}
	
	public Point getCopyBeforePoint(){
		return null ;
	}
	
	public void setCopyBeforePoint(String mapId, int x, int y){
	}
	
	public short selectSkillId() {
    	Map<Short, RoleSkillStat> skillsMap = this.getSkillMap();
    	if(Util.isEmpty(skillsMap)){
    		return 0 ;
    	}
    	
    	Comparator<RoleSkillStat> roleSkillStatComparator = new Comparator<RoleSkillStat>(){
    		@Override
    		public int compare(RoleSkillStat s1, RoleSkillStat s2) {
    			//1.出战
    			if(s1.getSkillId() > s2.getSkillId()){
    				return -1;
    			}
    			if(s1.getSkillId() < s2.getSkillId()){
    				return 1;
    			}
    			return 0;
    		}
    	} ;
    	
    	List<RoleSkillStat> list = Lists.newArrayList();
    	list.addAll(skillsMap.values());
    	//排序
		Collections.sort(list,roleSkillStatComparator);
    	
    	for(RoleSkillStat skillStat : list) {
    		if(null == skillStat) {
    			continue;
    		}
    		Skill skill = GameContext.getSkillApp().getSkill(skillStat.getSkillId());
    		if(null == skill) {
    			continue;
    		}
    		if(!skill.isActiveSkill()) {
    			continue;
    		}
    		SkillApplyResult con = skill.condition(this);
    		if(SkillApplyResult.SUCCESS == con){
				return skill.getSkillId();
			}
    	}
    	return 0 ;
    }

	public abstract AbstractRole getMasterRole();

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
