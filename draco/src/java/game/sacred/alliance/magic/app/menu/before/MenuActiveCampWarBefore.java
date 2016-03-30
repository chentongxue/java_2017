package sacred.alliance.magic.app.menu.before;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import sacred.alliance.magic.app.menu.MenuBefore;
import sacred.alliance.magic.app.menu.MenuIdType;

import com.game.draco.GameContext;

public class MenuActiveCampWarBefore extends MenuBefore{

	public MenuActiveCampWarBefore() {
		super(MenuIdType.Active_CampWar);
	}

	@Override
	public void execute(JobExecutionContext paramJobExecutionContext)
			throws JobExecutionException {
		GameContext.getCampWarApp().initCampMatchGroup();
		super.execute(paramJobExecutionContext);
	}
	

}
