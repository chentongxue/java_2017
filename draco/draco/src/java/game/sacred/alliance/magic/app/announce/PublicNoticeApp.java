package sacred.alliance.magic.app.announce;

import java.util.Collection;

import sacred.alliance.magic.base.PublicNoticeType;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.PublicNotice;
import sacred.alliance.magic.vo.RoleInstance;

public interface PublicNoticeApp extends Service{
	
	public boolean isNew(RoleInstance role) ;
	
	/** 重置公告 */
	public boolean reload();
	
	/**
	 * 添加公告
	 * 若存在，则修改之。
	 * @param type
	 * @param context
	 * @return
	 */
	public boolean addNotice(PublicNoticeType noticeType, String title, String content, String color);
	
	/**
	 * 修改公告
	 * @param type
	 * @param context
	 * @return
	 */
	public boolean modifyNotice(PublicNoticeType noticeType, String title, String content, String color);
	
	/**
	 * 删除公告
	 * @param type
	 * @return
	 */
	public boolean deleteNotice(PublicNoticeType noticeType);
	
	/**
	 * 获取公告
	 * @param type
	 * @return
	 */
	public PublicNotice getNotice(PublicNoticeType noticeType);
	
	/**
	 * 获取所有公告
	 * @return
	 */
	public Collection<PublicNotice> getAllPublicNotice();
	
}
