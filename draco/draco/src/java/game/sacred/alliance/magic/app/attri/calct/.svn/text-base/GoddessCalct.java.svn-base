package sacred.alliance.magic.app.attri.calct;

import java.util.Map;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;

import com.game.draco.GameContext;
import com.game.draco.app.goddess.config.GoddessLevelup;
import com.game.draco.app.goddess.domain.RoleGoddess;

public class GoddessCalct extends DefaultCalct<RoleGoddess> {

	@Override
	protected void autoUpgrade(RoleGoddess role) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void changeExp(RoleGoddess role, AttriItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void changeLevel(RoleGoddess role, AttriItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getByLevelup(RoleGoddess goddess, AttributeType attriType) {
		GoddessLevelup lp = GameContext.getGoddessApp().getGoddessLevelup(goddess.getGoddessId(), goddess.getLevel());
		if(null == lp) {
			return 0;
		}
		return (int)(lp.getAttriValue(attriType) * GameContext.getGoddessApp().getGoddessWeakRate(goddess));
	}

	@Override
	protected Map<Byte, AttriItem> getByMultAdvanced(RoleGoddess role) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int getFormulaCalct(RoleGoddess role, AttributeType attriType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void otherEffect(RoleGoddess role, AttriBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bornAtrri(RoleGoddess role) {
		// TODO Auto-generated method stub
		
	}

}
