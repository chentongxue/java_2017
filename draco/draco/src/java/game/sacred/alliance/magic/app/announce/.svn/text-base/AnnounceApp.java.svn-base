package sacred.alliance.magic.app.announce;

import java.util.Map;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.SysAnnouncement;

public interface AnnounceApp extends Service{
	/**
	 * 插入系统广播
	 * @param ment
	 * @throws ServiceException
	 */
	public SysAnnouncement insertAnnounce(SysAnnouncement ment)throws ServiceException;
	
	/**
	 * 删除系统广播
	 * @param ment
	 * @throws ServiceException
	 */
	public void deleAnnounce(int id)throws ServiceException;
	
	/**
	 * 更新系统广播
	 * @param ment
	 * @throws ServiceException
	 */
	public void updateAccounce(int id,String content,long startTime,long endTime,int timeGap,int state)throws ServiceException;
	
	/**
	 * 根据ID查询广播
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	public SysAnnouncement getAnnounce(int id);
	
	/**
	 * 获得所有系统广播
	 * @return
	 * @throws ServiceException
	 */
	public Map<Integer,SysAnnouncement> getAnnounceMap();
	
	/**
	 * 关闭，启用系统广播
	 * @param id
	 * @param state
	 * @throws ServiceException
	 */
	public void operAnnouncement(int id,int state)throws ServiceException;
	
	public void publish();
	
	public Result reload();
	
}
