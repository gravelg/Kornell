package kornell.gui.client.util.view.table;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

public interface PaginationPresenter<E> extends IsWidget{
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
	int getTotalRowCount();
	int getCount();
	List<E> getRowData();
}
