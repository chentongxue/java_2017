package sacred.alliance.magic.vo;

import java.util.List;

import sacred.alliance.magic.base.PathType;


public class Path {

	private PathType pathType;
	
	private List<Point> pathNode;

	public PathType getPathType() {
		return pathType;
	}

	public void setPathType(PathType pathType) {
		this.pathType = pathType;
	}

	public List<Point> getPathNode() {
		return pathNode;
	}

	public void setPathNode(List<Point> pathNode) {
		this.pathNode = pathNode;
	}
	
	public Path(){
		
	}
	
	public Path(PathType pathType,List<Point> pathNode){
		this.pathType = pathType ;
		this.pathNode = pathNode ;
	}
}
