package sacred.alliance.magic.app.faction;

import java.text.MessageFormat;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 公会工资
 */
public @Data class FactionSalary {
	
	private int factionLevel;
	
	private int roleLevelMin;
	
	private int roleLevelMax;
	
	private int goodsId;
	
	private int goodsNum;
	
	private int maxCounts = 1;

	/**
	 * 判断是否可以领工资
	 * @return
	 */
	public Result canSalaryReceive(RoleInstance role){
		Result result = new Result();
		if(maxCounts <= 0) {
			return result.failure();
		}
		if(role.getFactionSalaryCount() >= maxCounts) {
			String str =  GameContext.getI18n().messageFormat(TextId.Faction_SALARY_MAX_COUNT, maxCounts);
			return result.setInfo(str);
		}
		return result.success();
	}
}
