package sacred.alliance.magic.app.goods.behavior.result;


public class PutSomeGoodsResult extends GoodsResult{
//	
//	private Map<Integer, Integer> putFailureMap ;
//	private Map<Integer, Integer> putSuccessMap ;
//	
//	
//	public void addSuccessMap(int goodsId, int goodsNum){
//		if(goodsId <= 0 || goodsNum <= 0){
//			return ;
//		}
//		if(putSuccessMap == null){
//			putSuccessMap = new HashMap<Integer, Integer>();
//		}
//		putSuccessMap.put(goodsId, goodsNum);
//	}
//	
//	
//	
//
//	public Map<Integer, Integer> getPutSuccessMap() {
//		return putSuccessMap;
//	}
//	public PutSomeGoodsResult setResult(byte ret) {
//		this.result = ret;
//		return this;
//	}
//	public PutSomeGoodsResult setInfo(String info) {
//		this.info = info;
//		return this;
//	}
//	public void addFailureMap(int goodsId, int num){
//		if(goodsId <= 0 || num <= 0){
//			return ;
//		}
//		if(putFailureMap == null){
//			putFailureMap = new HashMap<Integer, Integer>();
//		}
//		this.putFailureMap.put(goodsId, num);
//	}
//	
//	public void addFailureMap(Map<Integer, Integer> map){
//		if(putFailureMap == null){
//			putFailureMap = new HashMap<Integer, Integer>();
//		}
//		this.putFailureMap.putAll(map);
//	}
//	
//	public Map<Integer, Integer> getPutFailureMap() {
//		return putFailureMap;
//	}
//	
//	public PutSomeGoodsResult success(){
//		this.result = Result.SUCCESS;
//		this.info = "";
//		return this;
//	}
//	
//	public PutSomeGoodsResult failure(){
//		this.result = Result.FAIL;
//		return this;
//	}
}
