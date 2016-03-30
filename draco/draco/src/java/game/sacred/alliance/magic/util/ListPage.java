package sacred.alliance.magic.util;

import java.util.List;

public class ListPage<T> {

	    private int page = 1; // 当前页
	    public int totalPages = 0; // 总页数
	    private int pageRecorders;// 每页5条数据
	    private int totalRows = 0; // 总数据数
	    private int pageStartRow = 0;// 每页的起始数
	    private int pageEndRow = 0; // 每页显示数据的终止数
	    private boolean hasNextPage = false; // 是否有下一页
	    private boolean hasPreviousPage = false; // 是否有前一页
	    private List<T> list;

	    public ListPage(List<T> list, int pageRecorders) {
	        this.init(list, pageRecorders);// 通过对象集，记录总数划分
	    }
	    
	    /**
	     * 初始化list，并告之该list每页的记录数
	     * @param list
	     * @param pageRecorders
	     */
	    private void init(List<T> list, int pageRecorders) {
	        this.pageRecorders = pageRecorders;
	        this.list = list;
	        this.totalRows = list.size();
	        this.hasPreviousPage = false;
	        if ((this.totalRows % this.pageRecorders) == 0) {
	        	this.totalPages = this.totalRows / this.pageRecorders;
	        } else {
	        	this.totalPages = this.totalRows / this.pageRecorders + 1;
	        }
	        this.hasNextPage = this.page < this.totalPages;
	        this.pageStartRow = 0;
	        this.pageEndRow = Math.min(this.totalRows, this.pageRecorders);
	    }

	    // 判断要不要分页
	    public boolean isNext() {
	        return list.size() > 5;
	    }

	    public void setHasPreviousPage(boolean hasPreviousPage) {
	        this.hasPreviousPage = hasPreviousPage;
	    }

	    public List<T> getNextPage() {
	    	this.page = this.page + 1;
	        this.disposePage();
	        return this.getObjects(this.page);
	    }

	    /**
	     * 处理分页
	     */
	    private void disposePage() {
	        if (this.page == 0) {
	        	this. page = 1;
	        }
	        this.hasPreviousPage = this.page - 1 > 0;
	        this.hasNextPage = this.page < this.totalPages;
	    }

	    public List<T> getPreviousPage() {
	    	this.page = this.page - 1;
	    	this.hasPreviousPage = this.page - 1 > 0;
	        this.hasNextPage = this.page < this.totalPages;
	        return getObjects(this.page);
	    }

	    /** *//**
	     * 获取第几页的内容
	     * 
	     * @param page
	     * @return
	     */
	    public List<T> getObjects(int page) {
	    	this.page = page > 0 ? page : 1;
	        this.disposePage();
	        if (this.page * this.pageRecorders < this.totalRows) {// 判断是否为最后一页
	        	this.pageEndRow = this.page * this.pageRecorders;
	            this.pageStartRow = this.pageEndRow - this.pageRecorders;
	        } else {
	        	this.pageEndRow = this.totalRows;
	        	this.pageStartRow = this.pageRecorders * (this.totalPages - 1);
	        }

	        List<T> objects = null;
	        if (!this.list.isEmpty()) {
	            objects = this.list.subList(this.pageStartRow, this.pageEndRow);
	        }
	        return objects;
	    }
	    
	    public ListPageDisplay<T> getObjectsDsiplay(int currPage){
	    	ListPageDisplay<T> result = new ListPageDisplay<T>();
	    	result.setTotalPages(this.getTotalPages());
	    	result.setList(this.getObjects(currPage));
	    	return result;
	    }

	    public List<T> getFistPage() {
	        if (this.isNext()) {
	            return list.subList(0, pageRecorders);
	        } else {
	            return list;
	        }
	    }

	    public boolean isHasNextPage() {
	        return hasNextPage;
	    }

	    public void setHasNextPage(boolean hasNextPage) {
	        this.hasNextPage = hasNextPage;
	    }

	    public List<T> getList() {
	        return list;
	    }

	    public void setList(List<T> list) {
	        this.list = list;
	    }

	    public int getPage() {
	        return page;
	    }

	    public void setPage(int page) {
	        this.page = page;
	    }

	    public int getPageEndRow() {
	        return pageEndRow;
	    }

	    public void setPageEndRow(int pageEndRow) {
	        this.pageEndRow = pageEndRow;
	    }

	    public int getPageRecorders() {
	        return pageRecorders;
	    }

	    public void setPageRecorders(int pageRecorders) {
	        this.pageRecorders = pageRecorders;
	    }

	    public int getPageStartRow() {
	        return pageStartRow;
	    }

	    public void setPageStartRow(int pageStartRow) {
	        this.pageStartRow = pageStartRow;
	    }

	    public int getTotalPages() {
	        return totalPages;
	    }

	    public void setTotalPages(int totalPages) {
	        this.totalPages = totalPages;
	    }

	    public int getTotalRows() {
	        return totalRows;
	    }

	    public void setTotalRows(int totalRows) {
	        this.totalRows = totalRows;
	    }

	    public boolean isHasPreviousPage() {
	        return hasPreviousPage;
	    }
	    
	}
