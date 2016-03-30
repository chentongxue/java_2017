package sacred.alliance.magic.app.attri.calct;

import java.util.HashMap;
import java.util.Map;

import sacred.alliance.magic.vo.AbstractRole;

public class CalctManagerImpl implements CalctManager {

	private  Map<Integer,Calct> calctMap = new HashMap<Integer,Calct>();
	
	public Map<Integer, Calct> getCalctMap() {
		return calctMap;
	}

	public void setCalctMap(Map<Integer, Calct> calctMap) {
		this.calctMap = calctMap;
	}

	@Override
	public Calct<AbstractRole> getCalct(AbstractRole role) {
		return calctMap.get(role.getRoleType().getType());
	}
}
