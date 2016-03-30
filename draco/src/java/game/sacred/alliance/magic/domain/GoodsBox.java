package sacred.alliance.magic.domain;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsBaseBoxItem;
import com.game.draco.message.item.GoodsBaseItem;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.Peshe;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.ProbabilityMachine;


public @Data class GoodsBox extends GoodsBase {
	private final static int DEFAULT_BIND_TYPE = BindingType.template.getType() ;
	private int keyId; 
	private int mustid1;
	private int number1;
	private int bind1 = DEFAULT_BIND_TYPE;
	private int mustid2;
	private int number2;
	private int bind2 = DEFAULT_BIND_TYPE;
	private int mustid3;
	private int number3;
	private int bind3 = DEFAULT_BIND_TYPE;
	private int mustid4;
	private int number4;
	private int bind4 = DEFAULT_BIND_TYPE;
	private int mustid5;
	private int number5;
	private int bind5 = DEFAULT_BIND_TYPE;
	private int mustid6;
	private int number6;
	private int bind6 = DEFAULT_BIND_TYPE;
	private int mustid7;
	private int number7;
	private int bind7 = DEFAULT_BIND_TYPE;
	private int mustid8;
	private int number8;
	private int bind8 = DEFAULT_BIND_TYPE;
	private int mustid9;
	private int number9;
	private int bind9 = DEFAULT_BIND_TYPE;
	private int mustid10;
	private int number10;
	private int bind10 = DEFAULT_BIND_TYPE;
	private int mustid11;
	private int number11;
	private int bind11 = DEFAULT_BIND_TYPE;
	private int mustid12;
	private int number12;
	private int bind12 = DEFAULT_BIND_TYPE;

	private int randomId1;
	private int randomNum1;
	private int prob1;
	private int randomBind1 = DEFAULT_BIND_TYPE;
	
	private int randomId2;
	private int randomNum2;
	private int prob2;
	private int randomBind2 = DEFAULT_BIND_TYPE;
	
	private int randomId3;
	private int randomNum3;
	private int prob3;
	private int randomBind3 = DEFAULT_BIND_TYPE;
	
	private int randomId4;
	private int randomNum4;
	private int prob4;
	private int randomBind4 = DEFAULT_BIND_TYPE;
	
	private int randomId5;
	private int randomNum5;
	private int prob5;
	private int randomBind5 = DEFAULT_BIND_TYPE;
	
	private int randomId6;
	private int randomNum6;
	private int prob6;
	private int randomBind6 = DEFAULT_BIND_TYPE;
	
	private int randomId7;
	private int randomNum7;
	private int prob7;
	private int randomBind7 = DEFAULT_BIND_TYPE;
	
	private int randomId8;
	private int randomNum8;
	private int prob8;
	private int randomBind8 = DEFAULT_BIND_TYPE;
	
	private int randomId9;
	private int randomNum9;
	private int prob9;
	private int randomBind9 = DEFAULT_BIND_TYPE;
	
	private int randomId10;
	private int randomNum10;
	private int prob10;
	private int randomBind10 = DEFAULT_BIND_TYPE;
	
	private int randomId11;
	private int randomNum11;
	private int prob11;
	private int randomBind11 = DEFAULT_BIND_TYPE;
	
	private int randomId12;
	private int randomNum12;
	private int prob12;
	private int randomBind12 = DEFAULT_BIND_TYPE;
	
	private int randomId13;
	private int randomNum13;
	private int prob13;
	private int randomBind13 = DEFAULT_BIND_TYPE;
	
	private int randomId14;
	private int randomNum14;
	private int prob14;
	private int randomBind14 = DEFAULT_BIND_TYPE;
	
	private int randomId15;
	private int randomNum15;
	private int prob15;
	private int randomBind15 = DEFAULT_BIND_TYPE;
	
	private int randomId16;
	private int randomNum16;
	private int prob16;
	private int randomBind16 = DEFAULT_BIND_TYPE;
	
	private int randomId17;
	private int randomNum17;
	private int prob17;
	private int randomBind17 = DEFAULT_BIND_TYPE;
	
	private int randomId18;
	private int randomNum18;
	private int prob18;
	private int randomBind18 = DEFAULT_BIND_TYPE;
	
	private int randomId19;
	private int randomNum19;
	private int prob19;
	private int randomBind19 = DEFAULT_BIND_TYPE;
	
	private int randomId20;
	private int randomNum20;
	private int prob20;
	private int randomBind20 = DEFAULT_BIND_TYPE;

    private int randomId21;
    private int randomNum21;
    private int prob21;
    private int randomBind21 = DEFAULT_BIND_TYPE;

    private int randomId22;
    private int randomNum22;
    private int prob22;
    private int randomBind22 = DEFAULT_BIND_TYPE;

    private int randomId23;
    private int randomNum23;
    private int prob23;
    private int randomBind23 = DEFAULT_BIND_TYPE;

    private int randomId24;
    private int randomNum24;
    private int prob24;
    private int randomBind24 = DEFAULT_BIND_TYPE;

    private int randomId25;
    private int randomNum25;
    private int prob25;
    private int randomBind25 = DEFAULT_BIND_TYPE;

    private int randomId26;
    private int randomNum26;
    private int prob26;
    private int randomBind26 = DEFAULT_BIND_TYPE;

    private int randomId27;
    private int randomNum27;
    private int prob27;
    private int randomBind27 = DEFAULT_BIND_TYPE;

    private int randomId28;
    private int randomNum28;
    private int prob28;
    private int randomBind28 = DEFAULT_BIND_TYPE;

    private int randomId29;
    private int randomNum29;
    private int prob29;
    private int randomBind29 = DEFAULT_BIND_TYPE;

    private int randomId30;
    private int randomNum30;
    private int prob30;
    private int randomBind30 = DEFAULT_BIND_TYPE;


	
	
	private int silverMoney;// 银币
	private int goldMoney;// 金币，人民币
	private int potential;// 潜能
	
	private int needGoodsGridCount = 0; //所需物品栏格子数
	private List<GoodsOperateBean> mustList = new ArrayList<GoodsOperateBean>();
	private List<Peshe> randomPesheList = new ArrayList<Peshe>();


	
	
	public List<GoodsOperateBean> getGoodsList(){
		List<GoodsOperateBean> list = new ArrayList<GoodsOperateBean>();
		if(!Util.isEmpty(this.mustList)){
            for(GoodsOperateBean bean : this.mustList){
                list.add(bean.clone()) ;
            }
        }
		Peshe peshe = this.getPeshe(this.randomPesheList);
		if(peshe != null){
			list.add(new GoodsOperateBean(peshe.getGoodsId(),peshe.getNum(),peshe.getBind()));
		}
		return list;
	}
	
	private  Peshe getPeshe(List<Peshe> pesheList){
		if(null == pesheList || 0 == pesheList.size()){
			return null;
		}
		int sumGon = 0;
		for(Peshe item : pesheList){
			sumGon += item.getGon();
		}
		if(0 == sumGon){
			return null;
		}
		int random = ProbabilityMachine.randomIntWithoutZero(sumGon);
		int overlapCount = 0; 
		for(Peshe it : pesheList){
			if((overlapCount< random) && (random<= (overlapCount+it.getGon()))){
				if(it.getGoodsId() != 0){
					return it;
				}
				break;
			}
			overlapCount += it.getGon();
		}
		return null;
	}
	
	
	@Override
	public void init(Object initData) {
		this.constructMustMap();
		this.constructRandomPesheList();
		this.calculateNeedGoodsGridCount();
	}
	
	
	private void constructMustMap(){
		this.filterMap(this.mustid1, this.number1, this.bind1,this.mustList);
		this.filterMap(this.mustid2, this.number2, this.bind2,this.mustList);
		this.filterMap(this.mustid3, this.number3, this.bind3,this.mustList);
		this.filterMap(this.mustid4, this.number4, this.bind4,this.mustList);
		this.filterMap(this.mustid5, this.number5, this.bind5,this.mustList);
		this.filterMap(this.mustid6, this.number6, this.bind6,this.mustList);
		this.filterMap(this.mustid7, this.number7, this.bind7,this.mustList);
		this.filterMap(this.mustid8, this.number8, this.bind8,this.mustList);
		this.filterMap(this.mustid9, this.number9, this.bind9,this.mustList);
		this.filterMap(this.mustid10, this.number10, this.bind10,this.mustList);
		this.filterMap(this.mustid11, this.number11, this.bind11,this.mustList);
		this.filterMap(this.mustid12, this.number12, this.bind12,this.mustList);
	}
	
	
	
	private void constructRandomPesheList(){
		this.filterPesh(this.randomId1, this.randomNum1, this.prob1, this.randomBind1,this.randomPesheList);
		this.filterPesh(this.randomId2, this.randomNum2, this.prob2, this.randomBind2,this.randomPesheList);
		this.filterPesh(this.randomId3, this.randomNum3, this.prob3, this.randomBind3,this.randomPesheList);
		this.filterPesh(this.randomId4, this.randomNum4, this.prob4, this.randomBind4,this.randomPesheList);
		this.filterPesh(this.randomId5, this.randomNum5, this.prob5, this.randomBind5,this.randomPesheList);
		this.filterPesh(this.randomId6, this.randomNum6, this.prob6, this.randomBind6,this.randomPesheList);
		this.filterPesh(this.randomId7, this.randomNum7, this.prob7, this.randomBind7,this.randomPesheList);
		this.filterPesh(this.randomId8, this.randomNum8, this.prob8, this.randomBind8,this.randomPesheList);
		this.filterPesh(this.randomId9, this.randomNum9, this.prob9, this.randomBind9,this.randomPesheList);
		this.filterPesh(this.randomId10, this.randomNum10, this.prob10, this.randomBind10,this.randomPesheList);
		this.filterPesh(this.randomId11, this.randomNum11, this.prob11, this.randomBind11,this.randomPesheList);
		this.filterPesh(this.randomId12, this.randomNum12, this.prob12, this.randomBind12,this.randomPesheList);
		this.filterPesh(this.randomId13, this.randomNum13, this.prob13, this.randomBind13,this.randomPesheList);
		this.filterPesh(this.randomId14, this.randomNum14, this.prob14, this.randomBind14,this.randomPesheList);
		this.filterPesh(this.randomId15, this.randomNum15, this.prob15, this.randomBind15,this.randomPesheList);
		this.filterPesh(this.randomId16, this.randomNum16, this.prob16, this.randomBind16,this.randomPesheList);
		this.filterPesh(this.randomId17, this.randomNum17, this.prob17, this.randomBind17,this.randomPesheList);
		this.filterPesh(this.randomId18, this.randomNum18, this.prob18, this.randomBind18,this.randomPesheList);
		this.filterPesh(this.randomId19, this.randomNum19, this.prob19, this.randomBind19,this.randomPesheList);
		this.filterPesh(this.randomId20, this.randomNum20, this.prob20, this.randomBind20,this.randomPesheList);

        this.filterPesh(this.randomId21, this.randomNum21, this.prob21, this.randomBind21,this.randomPesheList);
        this.filterPesh(this.randomId22, this.randomNum22, this.prob22, this.randomBind22,this.randomPesheList);
        this.filterPesh(this.randomId23, this.randomNum23, this.prob23, this.randomBind23,this.randomPesheList);
        this.filterPesh(this.randomId24, this.randomNum24, this.prob24, this.randomBind24,this.randomPesheList);
        this.filterPesh(this.randomId25, this.randomNum25, this.prob25, this.randomBind25,this.randomPesheList);
        this.filterPesh(this.randomId26, this.randomNum26, this.prob26, this.randomBind26,this.randomPesheList);
        this.filterPesh(this.randomId27, this.randomNum27, this.prob27, this.randomBind27,this.randomPesheList);
        this.filterPesh(this.randomId28, this.randomNum28, this.prob28, this.randomBind28,this.randomPesheList);
        this.filterPesh(this.randomId29, this.randomNum29, this.prob29, this.randomBind29,this.randomPesheList);
        this.filterPesh(this.randomId30, this.randomNum30, this.prob30, this.randomBind30,this.randomPesheList);
	}

	
	
	private void calculateNeedGoodsGridCount(){
		int needCount;
		for(GoodsOperateBean bean : this.mustList){
			int goodsId = bean.getGoodsId();
			int num = bean.getGoodsNum();
			GoodsBase base = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(base == null){
				continue;
			}
			int maxOverlapCount = base.getOverlapCount();
			needCount = num / maxOverlapCount;
			if((num % maxOverlapCount) > 0){
				needCount ++;
			}
			this.needGoodsGridCount += needCount;
		}
		if(this.randomPesheList.size() > 0){
			this.needGoodsGridCount ++ ;
		}
	}
	
	
	private void checkGoods(int goodsId){
		//验证物品ID是否存在
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == gb){
			Log4jManager.CHECK.error("goods not exist(GoodsBox),goodsId=" + goodsId + " goodsBoxId=" + this.id);
			Log4jManager.checkFail();
		}
	}
	
	private void filterMap(int goodsId, int num,int bind,List<GoodsOperateBean> mustList){
		if (0 >= goodsId || 0>= num) {
			return;
		}
		
		for(GoodsOperateBean bean : mustList){
			if(goodsId == bean.getGoodsId() 
					&& bind == bean.getBindType().getType()){
				bean.setGoodsNum(bean.getGoodsNum() + num);
				return ;
			}
		}
		mustList.add(new GoodsOperateBean(goodsId,num,bind));
		//验证物品是否存在
		this.checkGoods(goodsId);
	}
	
	private void filterPesh(int goodsId, int num, int prob, int bind,List<Peshe> list){
		if(0 >= goodsId || 0 >= num){
			return ;
		}
		list.add(new Peshe(goodsId,num,bind,prob));
		//验证物品是否存在
		this.checkGoods(goodsId);
	}
	
	
	@Override
	public List<AttriItem> getAttriItemList() {
		return null; 
	}

	


	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseBoxItem item = new GoodsBaseBoxItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setSecondType(secondType);
		item.setLvLimit((byte)lvLimit);
		item.setDesc(Util.replace(desc));
		return item;
	}
	
}
