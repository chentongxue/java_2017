package sacred.alliance.magic.app.ai.movement;

import com.game.draco.message.response.C0211_NpcWalkSynchRespMessage;

import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

public class Traveller {

	AbstractRole traveller;
	
	public Traveller(AbstractRole traveller){
		this.traveller = traveller;
	}
	
	public AbstractRole getTraveller(){
		return traveller;
	}
	
	public Point getPosition(){
		return new Point(traveller.getMapId(), traveller.getMapX(), traveller.getMapY());
	}

	public float speed(){
		//return traveller.getSpeedType().getSpeed(traveller.getRoleType());
		return traveller.getSpeed();
	}
	
	public void relocation(String mapId, int x, int y, long moveTime){
		traveller.getBehavior().move(x, y, moveTime);
	}
	
	public int getPositionX(){
		return traveller.getMapX();
	}
	
	public int getPositionY(){
		return traveller.getMapY();
	}

	public void moveTo(int destx, int desty, long travelTime,double speed) {
		
		/**
		 * 发送消息给客户端，NPC要开始移动了
		 */
		//traveller.getBehavior().moveTo(destx, desty, MovementFlagType.MOVEMENTFLAG_RUN_MODE);
		//traveller.getBehavior().move(destx, desty);
		
		C0211_NpcWalkSynchRespMessage resp1 = new C0211_NpcWalkSynchRespMessage();
        resp1.setNpcId(traveller.getIntRoleId());
        //resp1.setNowX((short)traveller.getMapX());
        //resp1.setNowY((short)traveller.getMapY());
        resp1.setTargetX((short)destx);
        resp1.setTargetY((short)desty);
        //resp1.setSpeed((short)speed);
        resp1.setMoveTime((int)travelTime);
        if(RoleType.NPC == traveller.getRoleType()  
        		&& null != traveller.getRebornPoint()
        		&& destx == traveller.getRebornPoint().getX() 
        	    && desty == traveller.getRebornPoint().getY()){
        	//NPC GOHOME 消息全地图广播
        	traveller.getMapInstance().broadcastMap(traveller, resp1);
        	return ;
        }
        traveller.getMapInstance().broadcastScreenMap(traveller, resp1);
	}
}
