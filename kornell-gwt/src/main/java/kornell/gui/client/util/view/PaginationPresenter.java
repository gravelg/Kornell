package kornell.gui.client.util.view;

import com.google.gwt.user.client.ui.IsWidget;

public interface PaginationPresenter extends IsWidget{
	String getPageSize();
	void setPageSize(String pageSize);
	String getPageNumber();
	void setPageNumber(String pageNumber);
	String getSearchTerm();
	void setSearchTerm(String searchTerm);
	void updateData();
	String getOrderBy();
	void setOrderBy(String dataStoreName);
	String getAsc();
	void setAsc(String ascending);
	String getClientPropertyName(String string);
}
