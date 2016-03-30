package sacred.alliance.magic.app.invite;

import java.util.List;

import sacred.alliance.magic.vo.RoleInstance;

public interface InviteApp {

	public InviteConfig getInviteConfig() ;
	
	public List<ActivatedReward> getActivatedRewardList() ;
	
	public String getSharedInfo(RoleInstance role,String code) ;
	

}
