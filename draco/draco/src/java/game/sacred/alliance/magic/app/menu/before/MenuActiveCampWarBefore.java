package sacred.alliance.magic.app.menu.before;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.menu.MenuBefore;
import sacred.alliance.magic.app.menu.MenuIdType;

public class MenuActiveCampWarBefore extends MenuBefore{

	public MenuActiveCampWarBefore() {
		super(MenuIdType.Active_CampWar);
	}

	
	@Override
	public void execute(JobExecutionContext paramJobExecutionContext)
			throws JobExecutionException {
		//对阵营战进行分组
		//否则用户提前进入面板的时候看到信息有问题
		GameContext.getCampWarApp().initCampMatchGroup();
		super.execute(paramJobExecutionContext);
	}
	
	@Override
	protected byte getMenuCountNotify(MenuIdType menuType) {
		
		return 0;
	}

}
