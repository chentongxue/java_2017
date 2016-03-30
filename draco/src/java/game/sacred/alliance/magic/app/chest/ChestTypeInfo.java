package sacred.alliance.magic.app.chest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

public @Data class ChestTypeInfo {
	private final static int DEFAULT_BIND_TYPE = BindingType.template.getType() ;
	private Map<Integer,Integer> wightMap = new HashMap<Integer,Integer>();
	private Map<Integer,GoodsOperateBean> goodsBeanMap = new HashMap<Integer,GoodsOperateBean>();
	
	public void init(){
		if(this.openGoodsNum <=0){
			this.openGoodsNum = 1 ;
		}
		this.init(goodsId1, goodsNum1, prob1, bind1);
		this.init(goodsId2, goodsNum2, prob2, bind2);
		this.init(goodsId3, goodsNum3, prob3, bind3);
		this.init(goodsId4, goodsNum4, prob4, bind4);
		this.init(goodsId5, goodsNum5, prob5, bind5);
		this.init(goodsId6, goodsNum6, prob6, bind6);
		this.init(goodsId7, goodsNum7, prob7, bind7);
		this.init(goodsId8, goodsNum8, prob8, bind8);
		this.init(goodsId9, goodsNum9, prob9, bind9);
		this.init(goodsId10, goodsNum10, prob10, bind10);
		
		this.init(goodsId11, goodsNum11, prob11, bind11);
		this.init(goodsId12, goodsNum12, prob12, bind12);
		this.init(goodsId13, goodsNum13, prob13, bind13);
		this.init(goodsId14, goodsNum14, prob14, bind14);
		this.init(goodsId15, goodsNum15, prob15, bind15);
		this.init(goodsId16, goodsNum16, prob16, bind16);
		this.init(goodsId17, goodsNum17, prob17, bind17);
		this.init(goodsId18, goodsNum18, prob18, bind18);
		this.init(goodsId19, goodsNum19, prob19, bind19);
		this.init(goodsId20, goodsNum20, prob20, bind20);
		
		this.checkBuff(buffId);
	}
	
	private void checkBuff(short buffId){
		if(buffId <=0){
			return ;
		}
		Buff buff = GameContext.getBuffApp().getBuff(buffId);
		if(null != buff){
			return ;
		}
		Log4jManager.checkFail();
		Log4jManager.CHECK.error("ChestTypeInfo config error ,buff not exist,buffId=" + buffId);
	}
	
	
	private void init(int goodsId,int goodsNum,int prob,int bind){
		if(goodsId <= 0 || goodsNum <=0 || prob <=0 ){
			return ;
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == gb){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("ChestTypeInfo config error ,goods not exist,goodsId=" + goodsId);
		}
		this.wightMap.put(goodsId, prob);
		this.goodsBeanMap.put(goodsId, new GoodsOperateBean(goodsId,goodsNum,bind));
	}
	
	
	public List<GoodsOperateBean> getGoodsList(){
		List<GoodsOperateBean> list = new ArrayList<GoodsOperateBean>();
		Set<Integer> goodsIdSet = Util.getWeightCalct(this.openGoodsNum, wightMap);
		for(Integer goodsId : goodsIdSet){
			GoodsOperateBean bean = goodsBeanMap.get(goodsId);
			if(null == bean){
				continue ;
			}
			list.add(bean);
		}
		return list ;
	}
	
	private short chestType;
	private String chestName;
	private byte progressTime;
	private int resId;
	private boolean broadcast;
	private short buffId ;
    private String eventKey ;
	
	private int openGoodsNum;

	private int goodsId1;
	private int goodsNum1;
	private int prob1;
	private int bind1 = DEFAULT_BIND_TYPE ;
	private int goodsId2;
	private int goodsNum2;
	private int prob2;
	private int bind2 = DEFAULT_BIND_TYPE ;
	private int goodsId3;
	private int goodsNum3;
	private int prob3;
	private int bind3 = DEFAULT_BIND_TYPE ;
	private int goodsId4;
	private int goodsNum4;
	private int prob4;
	private int bind4 = DEFAULT_BIND_TYPE ;
	private int goodsId5;
	private int goodsNum5;
	private int prob5;
	private int bind5 = DEFAULT_BIND_TYPE ;
	private int goodsId6;
	private int goodsNum6;
	private int prob6;
	private int bind6 = DEFAULT_BIND_TYPE ;
	private int goodsId7;
	private int goodsNum7;
	private int prob7;
	private int bind7 = DEFAULT_BIND_TYPE ;
	private int goodsId8;
	private int goodsNum8;
	private int prob8;
	private int bind8 = DEFAULT_BIND_TYPE ;
	private int goodsId9;
	private int goodsNum9;
	private int prob9;
	private int bind9 = DEFAULT_BIND_TYPE ;
	private int goodsId10;
	private int goodsNum10;
	private int prob10;
	private int bind10 = DEFAULT_BIND_TYPE ;
	

	private int goodsId11;
	private int goodsNum11;
	private int prob11;
	private int bind11 = DEFAULT_BIND_TYPE ;
	private int goodsId12;
	private int goodsNum12;
	private int prob12;
	private int bind12 = DEFAULT_BIND_TYPE ;
	private int goodsId13;
	private int goodsNum13;
	private int prob13;
	private int bind13 = DEFAULT_BIND_TYPE ;
	private int goodsId14;
	private int goodsNum14;
	private int prob14;
	private int bind14 = DEFAULT_BIND_TYPE ;
	private int goodsId15;
	private int goodsNum15;
	private int prob15;
	private int bind15 = DEFAULT_BIND_TYPE ;
	private int goodsId16;
	private int goodsNum16;
	private int prob16;
	private int bind16 = DEFAULT_BIND_TYPE ;
	private int goodsId17;
	private int goodsNum17;
	private int prob17;
	private int bind17 = DEFAULT_BIND_TYPE ;
	private int goodsId18;
	private int goodsNum18;
	private int prob18;
	private int bind18 = DEFAULT_BIND_TYPE ;
	private int goodsId19;
	private int goodsNum19;
	private int prob19;
	private int bind19 = DEFAULT_BIND_TYPE ;
	private int goodsId20;
	private int goodsNum20;
	private int prob20;
	private int bind20 = DEFAULT_BIND_TYPE ;
}
