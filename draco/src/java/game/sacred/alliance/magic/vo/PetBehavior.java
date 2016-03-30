package sacred.alliance.magic.vo;

import sacred.alliance.magic.base.AttrFontColorType;
import sacred.alliance.magic.base.AttrFontSizeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;

import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.skill.vo.SkillContext;

public class PetBehavior extends AbstractRoleBehavior<RolePet> {
	private RolePet role;

	public PetBehavior(RolePet t) {
		super(t);
		role = t;
	}

	@Override
	public void addCumulateEvent(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEvent(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSelfFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSelfFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, SkillContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTargetFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, AbstractRole targetRole) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTargetFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, AbstractRole targetRole,
			SkillContext context) {
		RoleInstance master = role.getRole();
		if(null == master) {
			return ;
		}
		master.getBehavior().addTargetFont(sizeType, AttrFontColorType.Pet_Attack, value, targetRole, context);
	}

	@Override
	public void autoLevelup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeMap(Point targetPoint) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeNetLink() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void death(AbstractRole attacker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterMap() throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitMap() throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean inSelfEyes(AbstractRole abstractRole) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void notifyAttrFont() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyBattleAttrIncome(NpcInstance dieNpc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyForceType() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyNpcMsg(String content) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyPosition(Message resp) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessage(Message message, int awaitMillis) {
		RoleInstance master = role.getRole();
		if(null == master) {
			return ;
		}
		
		master.getBehavior().sendMessage(message);
	}

	@Override
	public void stopMove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSelfFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, SkillContext context,
			AbstractRole attack) {
		// TODO Auto-generated method stub
		
	}

}
