package sacred.alliance.magic.app.rate;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

import sacred.alliance.magic.base.RateType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.Rate;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

public class RateAppImpl implements RateApp,Service{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Integer, Rate> rateMap = new HashMap<Integer, Rate>();
	
	@Override
	public boolean reload() {
		try{
			List<Rate> rateList = this.selectAll();
			if(Util.isEmpty(rateList)){
				this.rateMap.clear();
				return true ;
			}
			Map<Integer, Rate> result = new HashMap<Integer, Rate>();
			for(Rate rate : rateList){
				result.put(rate.getType(), rate);
			}
			this.rateMap = result ;
			rateList.clear();
			return true ;
		}catch(Exception ex){
			logger.error("",ex);
			return false ;
		}
		
	}
	
	private List<Rate> selectAll() {
		return GameContext.getBaseDAO().selectAll(Rate.class);
	}

	@Override
	public Rate getRateByType(RateType rateType) {
		return rateMap.get(rateType.getType());
	}

	@Override
	public Collection<Rate> getRateList() {
		return this.rateMap.values();
	}
	
	@Override
	public void setArgs(Object args) {
		
	}

	@Override
	public void start() {
		if(this.reload()){
			return ;
		}
		Log4jManager.CHECK.error("load rate failure");
		Log4jManager.checkFail();
	}

	@Override
	public void stop() {
		
	}

	@Override
	public Result addRate(int type, Date startTime, Date endTime, int rate, int rate1) {
		Result result = new Result();
		try{
			RateType rateType = RateType.get(type);
			if(null == rateType){
				return result.setInfo(GameContext.getI18n().getText(TextId.RATE_TYPE_NOT_EXISTS));
			}
			if(this.rateMap.containsKey(type)){
				return result.setInfo(GameContext.getI18n().getText(TextId.RATE_EXISTS));
			}
			Rate rt = new Rate();
			rt.setType(type);
			rt.setStartTime(startTime);
			rt.setEndTime(endTime);
			rt.setRate(rate);
			rt.setRate1(rate1);
			GameContext.getBaseDAO().insert(rt);
			this.rateMap.put(type, rt);
			return result.success();
		}catch(Exception e){
			this.logger.error("RateApp.addRate error:" + e);
			return result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
		}
	}

	@Override
	public Result updateRate(int type, Date startTime, Date endTime, int rate, int rate1) {
		Result result = new Result();
		try{
			Rate rt = this.rateMap.get(type);
			if(null == rt){
				return result.setInfo(GameContext.getI18n().getText(TextId.RATE_NOT_EXISTS));
			}
			rt.setType(type);
			rt.setStartTime(startTime);
			rt.setEndTime(endTime);
			rt.setRate(rate);
			rt.setRate1(rate1);
			GameContext.getBaseDAO().update(rt);
			return result.success();
		}catch(Exception e){
			this.logger.error("RateApp.updateRate error:" + e);
			return result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
		}
	}

	@Override
	public Result deleteRate(int type) {
		Result result = new Result();
		try{
			if(!this.rateMap.containsKey(type)){
				return result.setInfo(GameContext.getI18n().getText(TextId.RATE_NOT_EXISTS));
			}
			GameContext.getBaseDAO().delete(Rate.class, "type", type);
			this.rateMap.remove(type);
			return result.success();
		}catch(Exception e){
			this.logger.error("RateApp.deleteRate error:" + e);
			return result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
		}
	}
}