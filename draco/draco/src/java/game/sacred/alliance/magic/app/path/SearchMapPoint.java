package sacred.alliance.magic.app.path;

import sacred.alliance.magic.app.map.point.JumpMapPoint;

/**
 * 自动寻路封装属性类
 * @author Wang.K
 *
 */
public class SearchMapPoint 
{
	private JumpMapPoint point;//跳转点对象
	private SearchMapPoint fatherPoint;//父节点
	
	public SearchMapPoint(JumpMapPoint point,SearchMapPoint fatherPoint){
		this.point = point;
		this.fatherPoint = fatherPoint;
	}
	
	
	public JumpMapPoint getPoint() {
		return point;
	}
	public void setPoint(JumpMapPoint point) {
		this.point = point;
	}
	public SearchMapPoint getFatherPoint() {
		return fatherPoint;
	}
	public void setFatherPoint(SearchMapPoint fatherPoint) {
		this.fatherPoint = fatherPoint;
	}
}
