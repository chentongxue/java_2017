package sacred.alliance.magic.app.arena;

import java.util.Comparator;

import sacred.alliance.magic.app.arena.config.ArenaConfig;

public class ApplyInfoComparator implements Comparator<ApplyInfo>{
	private ArenaConfig config ;
	public ApplyInfoComparator(ArenaConfig config){
		this.config = config ;
	}
	@Override
	public int compare(ApplyInfo a1, ApplyInfo a2) {
		if(config.getLevels().length>0){
			if(a1.getLevel()>a2.getLevel()){
				return 1 ;
			}
			if(a1.getLevel()<a2.getLevel()){
				return -1 ;
			}
		}
		//正序
		if(a1.getScore()>a2.getScore()){
			return -1 ;
		}
		if(a1.getScore()<a2.getScore()){
			return 1 ;
		}
		return 0 ;
	}

}
