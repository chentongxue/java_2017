package sacred.alliance.magic.app.recoup;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class RecoupAppImpl implements RecoupApp{
	private final static String CAT = "," ;
	private final static Logger logger = LoggerFactory.getLogger(RecoupAppImpl.class);
	private Map<String,Recoup> recoupMap = new HashMap<String,Recoup>();

	@Override
	public void receiveRecoup(RoleInstance role) {
		try {
			if (Util.isEmpty(recoupMap)) {
				return;
			}
			for (Recoup recoup : recoupMap.values()) {
				this.receiveRecoup(role, recoup);
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
	
	private void receiveRecoup(RoleInstance role,Recoup r){
		if (null == r) {
			return;
		}
		try {
			if (!r.canReceive(role)) {
				return;
			}
			String key = String.valueOf(r.getId());
			if (role.getReceiveRecoupSet().contains(key)) {
				return;
			}
			// 发邮件
			//!!!!! 游戏里面没有bindmoney，这里bindmoney为钻石
			GameContext.getMailApp().sendMail(role.getRoleId(), r.getTitle(), r.getContext(),
					r.getSenderName(), OutputConsumeType.recoup_mail.getType(),
					r.getBindMoney(),r.getGameMoney(), r.getGoodsList());
			
			role.getReceiveRecoupSet().add(key);
		}catch(Exception ex){
			logger.error("recoup error,recoupId=" + r.getId() + " roleId=" + role.getRoleId(),ex);
		}
	}
	
	@Override
	public int onLogin(RoleInstance role, Object context) {
		if(null == role.getReceiveRecoupSet()){
			role.setReceiveRecoupSet(new HashSet<String>());
		}
		if(Util.isEmpty(role.getReceiveRecoup())){
			return 1;
		}
		String[] strs = role.getReceiveRecoup().split(CAT);
		for(String s : strs){
			role.getReceiveRecoupSet().add(s);
		}
		return 1;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			if (Util.isEmpty(role.getReceiveRecoupSet())) {
				role.setReceiveRecoup("");
				return 1;
			}
			StringBuffer buffer = new StringBuffer("");
			String cat = "";
			for (Iterator<String> it = role.getReceiveRecoupSet().iterator(); it
					.hasNext();) {
				String id = it.next();
				Recoup r = this.recoupMap.get(id);
				if (null == r || !r.inTime()) {
					// 已经被存在或者过期
					it.remove();
					continue;
				}
				buffer.append(cat).append(id);
				cat = CAT;
			}
			role.setReceiveRecoup(buffer.toString());
		}catch(Exception ex){
			logger.error("",ex);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean reload() {
		List<Recoup> list = null;
		try {
			list = GameContext.getBaseDAO().selectAll(Recoup.class);
		} catch (Exception ex) {
			logger.error("", ex);
			return false ;
		}
		if(Util.isEmpty(list)){
			recoupMap.clear();
			return true;
		}
		this.recoupMap = this.initRecoup(list);
		return true ;
	}

	@Override
	public void setArgs(Object arg0) {
		
	}
	
	private Map<String,Recoup> initRecoup(List<Recoup> list){
		Map<String,Recoup> map = new HashMap<String,Recoup>();
		for(Recoup r : list){
			r.init();
			map.put(String.valueOf(r.getId()), r);
		}
		return map ;
	}

	@Override
	public void start() {
		List<Recoup> list = GameContext.getBaseDAO().selectAll(Recoup.class);
		if(Util.isEmpty(list)){
			this.recoupMap.clear();
			return ;
		}
		this.recoupMap = this.initRecoup(list);
	}

	@Override
	public void stop() {
		
	}

	@Override
	public void deleteRecoup(int id) {
		GameContext.getBaseDAO().delete(Recoup.class, Recoup.ID, id);
		this.recoupMap.remove(String.valueOf(id));
	}

	@Override
	public void insertRecoup(Recoup recoup) {
		if(null == recoup){
			return ;
		}
		//获得goodsInfo
		recoup.appendGoodsInfo();
		Recoup rec = GameContext.getBaseDAO().insert(recoup);
		this.recoupMap.put(String.valueOf(rec.getId()), rec);
	}

	@Override
	public Collection<Recoup> getAllRecoup() {
		return this.recoupMap.values();
	}

}
