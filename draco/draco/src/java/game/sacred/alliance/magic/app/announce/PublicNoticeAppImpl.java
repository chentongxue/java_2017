package sacred.alliance.magic.app.announce;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.PublicNoticeType;
import sacred.alliance.magic.dao.BaseDAO;
import sacred.alliance.magic.domain.PublicNotice;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class PublicNoticeAppImpl implements PublicNoticeApp {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private BaseDAO baseDAO;
	private Lock lock = new ReentrantLock();
	private Map<PublicNoticeType, PublicNotice> noticeMap = new HashMap<PublicNoticeType, PublicNotice>();
	
	@Override
	public void start(){
		this.initNoticeMap();
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void stop() {
		
	}
	/**
	 * 查询所有公告，并初始化
	 */
	private void initNoticeMap(){
		List<PublicNotice> list = this.baseDAO.selectAll(PublicNotice.class);
		if(Util.isEmpty(list)){
			return;
		}
		for(PublicNotice notice : list){
			if(null == notice){
				continue;
			}
			//容错
			if(null == notice.getUpdateTime()){
				notice.setUpdateTime(new Date());
			}
			PublicNoticeType noticeType = PublicNoticeType.get(notice.getNoticeType());
			if(null == noticeType){
				continue;
			}
			this.noticeMap.put(noticeType, notice);
		}
	}
	
	@Override
	public boolean addNotice(PublicNoticeType noticeType, String title, String content, String color) {
		try{
			this.lock.lock();
			if(null == noticeType){
				return false;
			}
			Date now = new Date();
			PublicNotice notice = this.noticeMap.get(noticeType);
			//如果公告存在，则修改；否则添加新公告。
			if(null != notice){
				notice.setTitle(title);
				notice.setContent(content);
				notice.setUpdateTime(now);
				notice.setColor(color);
				this.baseDAO.update(notice);
				return true;
			}
			notice = new PublicNotice();
			notice.setNoticeType(noticeType.getType());
			notice.setTitle(title);
			notice.setContent(content);
			notice.setUpdateTime(now);
			notice.setColor(color);
			this.baseDAO.insert(notice);
			this.noticeMap.put(noticeType, notice);
			return true;
		}catch(Exception e){
			this.logger.error("PublicNoticeApp.addNotice error:" + e);
			return false;
		}finally{
			this.lock.unlock();
		}
	}

	@Override
	public boolean deleteNotice(PublicNoticeType noticeType) {
		try{
			this.lock.lock();
			this.baseDAO.delete(PublicNotice.class, PublicNotice.NOTICETYPE, noticeType.getType());
			this.noticeMap.remove(noticeType);
			return true;
		}catch(Exception e){
			this.logger.error("PublicNoticeApp.deleteNotice error:" + e);
			return false;
		}finally{
			this.lock.unlock();
		}
	}

	@Override
	public PublicNotice getNotice(PublicNoticeType noticeType) {
		return this.noticeMap.get(noticeType);
	}

	@Override
	public Collection<PublicNotice> getAllPublicNotice() {
		return this.noticeMap.values();
	}

	@Override
	public boolean modifyNotice(PublicNoticeType noticeType, String title, String content, String color) {
		try{
			this.lock.lock();
			PublicNotice notice = this.getNotice(noticeType);
			if(null == notice){
				return false;
			}
			notice.setTitle(title);
			notice.setContent(content);
			notice.setColor(color);
			notice.setUpdateTime(new Date());
			this.baseDAO.update(notice);
			return true;
		}catch(Exception e){
			this.logger.error("PublicNoticeApp.modifyNotice error:" + e);
			return false;
		}finally{
			this.lock.unlock();
		}
	}

	@Override
	public boolean reload() {
		try{
			this.lock.lock();
			this.noticeMap.clear();
			this.initNoticeMap();
			return true;
		}catch(Exception e){
			this.logger.error("PublicNoticeApp.reload error:" + e);
			return false;
		}finally{
			this.lock.unlock();
		}
	}

	public BaseDAO getBaseDAO() {
		return baseDAO;
	}

	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}

	
	public boolean isNew(RoleInstance role){
		if(Util.isEmpty(this.noticeMap)){
			return false;
		}
		PublicNotice sysNotice = this.noticeMap.get(PublicNoticeType.System_Notice.getType());
		Date lastUpdateTime = null;//公告最后修改时间
		if(null != sysNotice){
			lastUpdateTime = sysNotice.getUpdateTime();
		}
		if(null == lastUpdateTime){
			return false;
		}
		Date roleOffTime = role.getLastOffTime();//最后一次下线时间
		if(null != roleOffTime && lastUpdateTime.before(roleOffTime)){
			return false;
		}
		return true ;
	}
	
}
