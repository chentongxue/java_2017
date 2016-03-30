package sacred.alliance.magic.app.announce;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.onlinecenter.OnlineCenter;
import sacred.alliance.magic.base.AnnouncementType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.dao.BaseDAO;
import sacred.alliance.magic.domain.SysAnnouncement;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;

public class AnnounceAppImpl implements AnnounceApp {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Lock lock = new ReentrantLock();
	private Map<Integer, SysAnnouncement> sysMap = new LinkedHashMap<Integer, SysAnnouncement>();
	private BaseDAO baseDAO;
	
	/**系统广播列表*/
	private List<SystemBroadcast> sysList = new ArrayList<SystemBroadcast>();

	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}
	
	/**定时任务(系统广播)*/
	public void systemBroadcast(){
		try{
			OnlineCenter ol = GameContext.getOnlineCenter() ;
			if(null == ol){
				return ;
			}
			if (ol.onlineUserSize() <= 0) {
				// 没有在线用户无需发布
				return;
			}
			Date now = new Date();
			for(SystemBroadcast sys : sysList){
				if (sys.canPublish(now)) {
					GameContext.getChatApp().sendSysMessage(ChatSysName.System,
							ChannelType.getChannelType(sys.getChannelType()),
							sys.getContent(), null, null);
				}
				
			}
		}catch(Exception e){
			logger.error("",e);
		}
		
	}
	
	/**加载系统广播excel*/
	private void loadBroadcast(){
		String fileName = "";
		String sheetName = "";
		String sourceFile = "";
		try {
			String path = GameContext.getPathConfig().getXlsPath();
			fileName = XlsSheetNameType.sys_broadcast.getXlsName();
			sheetName = XlsSheetNameType.sys_broadcast.getSheetName();
			sourceFile = path + fileName;
			sysList = XlsPojoUtil.sheetToList(sourceFile, sheetName,SystemBroadcast.class);
			if(Util.isEmpty(sysList)){
				return ;
			}
			for(SystemBroadcast sys : sysList){
				sys.init();
				if((sys.getEndTime() != null || sys.getStartTime() != null) && (sys.getRelativeStartTime() != 0 || sys.getRelativeEndTime() != 0)){
					Log4jManager.CHECK.error("load SystemBroadcast error :At the same time have value");
					Log4jManager.checkFail();
				}
			}
		}catch(Exception e){
			Log4jManager.CHECK.error("load SystemBroadcast error",e);
			Log4jManager.checkFail();
		}
	}

	public void publish() {
		boolean haveOnlineUser = true;
		// 将一分钟一次
		if (GameContext.getOnlineCenter().onlineUserSize() <= 0) {
			// 没有在线用户无需发布
			haveOnlineUser = false;
		}
		lock.lock();
		try {
			Date now = new Date();
			for (Iterator<Map.Entry<Integer, SysAnnouncement>> it = sysMap
					.entrySet().iterator(); it.hasNext();) {
				Map.Entry<Integer, SysAnnouncement> entry = it.next();
				SysAnnouncement entity = entry.getValue();
				if (this.canRemove(entity, now)) {
					// 为了性能此处不删除库
					//为了GM工具能查询到广播信息，此处不在remove
					//it.remove();
					continue;
				}
				if (!entity.canPublish(now)) {
					continue;
				}
				try {
					if(haveOnlineUser){
						GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_System, entity.getContent(), null, null);
					}
				} catch (Exception e) {
					logger.error("announce publish error:", e);
				}
			}
		} finally {
			lock.unlock();
		}
	}

	private boolean canRemove(SysAnnouncement entity, Date now) {
		return now.getTime() > entity.getEndTime().getTime();
	}
	
	@Override
	public void start(){
		this.loadBroadcast();
		try {
			List<SysAnnouncement> list = this.selectAllAnnounce();
			if (!Util.isEmpty(list)) {
				Date now = new Date();
				for(SysAnnouncement annou : list){
					
					if(canDelete(annou)){
						baseDAO.delete(SysAnnouncement.class, "id", annou.getId());
						continue;
					}
					
					annou.checkIndex(now);
					sysMap.put(annou.getId(), annou);
				}
			}
		} catch (ServiceException e) {
			this.logger.error("select all announce error: " + e);
		}
	}
	
	/**
	 * 判断是否可以删除，不是GM添加的并且已经过期才可以删除
	 * @param annou
	 * @return
	 */
	private boolean canDelete(SysAnnouncement annou) {
		if(annou.getAnnounceType() != AnnouncementType.SYS.getType()){
			return false;
		}
//		if(!canRemove(annou, new Date())){
//			return false;
//		}
		return true;
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}


	@Override
	public void stop() {
		
	}
	
	/**
	 * 从数据库中读取所有广播
	 * @throws ServiceException
	 */
	private List<SysAnnouncement> selectAllAnnounce() throws ServiceException {
		return baseDAO.selectAll(SysAnnouncement.class);
	}

	@Override
	public void deleAnnounce(int id) throws ServiceException {
		lock.lock();
		try {
			baseDAO.delete(SysAnnouncement.class, "id", id);
			sysMap.remove(id);
		} catch (Exception e) {
			throw new ServiceException("deleAnnounce error:", e);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Map<Integer, SysAnnouncement> getAnnounceMap(){
		return this.sysMap;
	}

	@Override
	public SysAnnouncement insertAnnounce(SysAnnouncement ment) throws ServiceException {
		lock.lock();
		try {
			SysAnnouncement annou = baseDAO.insert(ment);
			if (annou != null) {
				annou.checkIndex(new Date());
				sysMap.put(annou.getId(), annou);
			}
			return annou;
		} catch (Exception e) {
			throw new ServiceException("insertAnnounce error:", e);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public SysAnnouncement getAnnounce(int id) {
		return this.sysMap.get(id);
	}

	@Override
	public void updateAccounce(int id, String content, long startTime,
			long endTime, int timeGap, int state) throws ServiceException {
		lock.lock();
		try {
			SysAnnouncement annou = sysMap.get(id);
			if (annou == null) {
				return;
			}
			annou.setContent(content);
			annou.setStartTime(new Date(startTime));
			annou.setEndTime(new Date(endTime));
			annou.setTimeGap(timeGap);
			annou.setState(state);
			baseDAO.update(annou);
			annou.checkIndex(new Date());
			sysMap.put(id, annou);
		} catch (Exception e) {
			throw new ServiceException("updateAccounce error:", e);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void operAnnouncement(int id, int state) throws ServiceException {
		lock.lock();
		try {
			SysAnnouncement annou = sysMap.get(id);
			if (annou == null) {
				return;
			}
			annou.setState(state);
			baseDAO.update(annou);
			sysMap.put(id, annou);
		} catch (Exception e) {
			throw new ServiceException("", e);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Result reload(){
		Result result = new Result();
		lock.lock();
		try{
			List<SysAnnouncement> list = this.selectAllAnnounce();
			this.sysMap.clear();
			if(null != list){
				for(SysAnnouncement annou : list){
					sysMap.put(annou.getId(), annou);
				}
			}
			return result.success();
		}catch (Exception e) {
			this.logger.error("Announcement reLoad error:" + e);
			return result.setInfo(GameContext.getI18n().getText(TextId.Announce_Reload_Failure));
		}finally{
			lock.unlock();
		}
	}


	public List<SystemBroadcast> getSysList() {
		return sysList;
	}


	public void setSysList(List<SystemBroadcast> sysList) {
		this.sysList = sysList;
	}
	
}
