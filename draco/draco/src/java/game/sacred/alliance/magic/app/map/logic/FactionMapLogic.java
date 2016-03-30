//package sacred.alliance.magic.app.map.logic;
//
//import com.game.draco.GameContext;
//
//import sacred.alliance.magic.vo.AbstractRole;
//import sacred.alliance.magic.vo.Point;
//import sacred.alliance.magic.vo.RoleInstance;
//
///**
// * 馱頗桵華芞Logic
// * 
// * @author Wang.K
// * 
// */
//public class FactionMapLogic extends MapLogicAdaptor {
//
//	public FactionMapLogic(String mapId) {
//		super(mapId);
//	}
//
//	@Override
//	public boolean canEnter(AbstractRole role) {
//		boolean superValue = super.canEnter(role);
//		if (!superValue) {
//			return false;
//		}
//		return ((RoleInstance)role).hasUnion();
//	}
//
//	@Override
//	public void exit(AbstractRole role) {
//		
//	}
//
//	@Override
//	public void kill(AbstractRole killer, AbstractRole victim) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void update() {
//		
//	}
//
//	@Override
//	public Point rebornPoint(AbstractRole role) {
//		return GameContext.getRoleRebornApp().getRebornPointDetail(mapId, role).createPoint();
//	}
//
//	@Override
//	public boolean isIncrUserCopyTimes() {
//		return false;
//	}
//
//	@Override
//	public int indexOfCopyCluster() {
//		return 0;
//	}
//}
