package kornell.gui.client.util.view.table;

import java.util.List;

import kornell.gui.client.util.ClientProperties;

public abstract class PaginationPresenterImpl<E> {
	private static String ORDER_BY = "orderBy";
	private static String ASC = "asc";
	private static String PAGE_SIZE = "pageSize";
	private static String PAGE_NUMBER = "pageNumber";
	
	private static String DEFAULT_ASC = "true";
	private static String DEFAULT_PAGE_SIZE = "20";
	private static String DEFAULT_PAGE_NUMBER = "1";
	private static String DEFAULT_SEARCH_TERM = "";
	
	protected String pageSize;
	protected String pageNumber;
	protected String searchTerm;
	protected String asc;
	protected String orderBy;

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void setAsc(String asc) {
		this.asc = asc;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public String getAsc() {
		return asc;
	}

	public void initializeProperties(String defaultOrderBy) {
		String orderByProperty = ClientProperties.get(getClientPropertyName(ORDER_BY));
		String ascProperty = ClientProperties.get(getClientPropertyName(ASC));
		String pageSizeProperty = ClientProperties.get(getClientPropertyName(PAGE_SIZE));
		String pageNumberProperty = ClientProperties.get(getClientPropertyName(PAGE_NUMBER));
		
		this.orderBy = orderByProperty != null ? orderByProperty : defaultOrderBy;
		this.asc = ascProperty != null ? ascProperty : DEFAULT_ASC;
		this.pageSize = pageSizeProperty != null ? pageSizeProperty : DEFAULT_PAGE_SIZE;
		this.pageNumber = pageNumberProperty != null ? pageNumberProperty : DEFAULT_PAGE_NUMBER;
		this.searchTerm = DEFAULT_SEARCH_TERM;
	}

	public void updateProperties() {
		ClientProperties.set(getClientPropertyName(ORDER_BY), getOrderBy());
		ClientProperties.set(getClientPropertyName(ASC), getAsc());
		ClientProperties.set(getClientPropertyName(PAGE_SIZE), getPageSize());
		ClientProperties.set(getClientPropertyName(PAGE_NUMBER), getPageNumber());
	}

	public abstract int getTotalRowCount();

	public abstract int getCount();

	public abstract String getClientPropertyName(String string);

	public abstract List<E> getRowData();

	public abstract void updateData();
}
