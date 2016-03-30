package sacred.alliance.magic.ai.support;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

import sacred.alliance.magic.ai.npc.AbstractNpcAi;
import sacred.alliance.magic.util.RTSI;

public class NpcAiScriptSupport{
	
	private static final String NpcAiPkg = "sacred.alliance.magic.ai.npc";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void loadScript() {
		Set<Class> clazzSet = RTSI.findClass(NpcAiPkg, AbstractNpcAi.class);
		for(Class clazz : clazzSet){
			try {
				AbstractNpcAi ai = (AbstractNpcAi) clazz.newInstance();
				GameContext.getAiApp().registerAi(ai);
			} catch (InstantiationException e) {
				this.logger.error("", e);
			} catch (IllegalAccessException e) {
				this.logger.error("", e);
			}
		}
	}

}
