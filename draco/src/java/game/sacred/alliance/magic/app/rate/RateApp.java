package sacred.alliance.magic.app.rate;

import java.util.Collection;
import java.util.Date;

import sacred.alliance.magic.base.RateType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.Rate;

public interface RateApp {
	
	public Rate getRateByType(RateType rateType);
	
	public boolean reload();
	
	public Collection<Rate> getRateList();
	
	public Result addRate(int type, Date startTime, Date endTime, int rate, int rate1);
	
	public Result updateRate(int type, Date startTime, Date endTime, int rate, int rate1);
	
	public Result deleteRate(int type);
	
}
