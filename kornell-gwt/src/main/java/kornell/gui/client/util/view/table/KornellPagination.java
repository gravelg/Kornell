package kornell.gui.client.util.view.table;

import java.util.List;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.Pagination;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

@SuppressWarnings({"rawtypes", "unchecked"})
public class KornellPagination<T> extends Pagination{
	
	private PaginationPresenter presenter;
	private int maxPaginationTabs = 10;
	private int totalRowCount = 0;
	private CellTable table;


	public KornellPagination(CellTable table, PaginationPresenter presenter) {
		this.presenter = presenter;
		this.table = table;
	}

	private void updatePagination(List rowData){
		clear();
		table.setRowData(rowData);
		int pageSize = Integer.parseInt(presenter.getPageSize());
		int pageNumber = Integer.parseInt(presenter.getPageNumber());
    	int navLinkCount = (totalRowCount+pageSize-1)/pageSize;
    	setVisible(navLinkCount > 1);
    	setAlignment("centered");
    	
    	if(navLinkCount < maxPaginationTabs){
    		for(int i = 1; i <= navLinkCount; i++){
    			add(createNavLink(i, i==pageNumber));
    		}
    	} else if(pageNumber <= (maxPaginationTabs / 2)){
    		for(int i = 1; i <= maxPaginationTabs; i++){
    			add(createNavLink(i, i==pageNumber));
    		}
    		add(createNavLink(">", navLinkCount));
    	} else if(pageNumber > navLinkCount - (maxPaginationTabs / 2)){
    		add(createNavLink("<", 1));
    		for(int i = (navLinkCount - maxPaginationTabs) + 1; i <= navLinkCount; i++){
    			add(createNavLink(i, i==pageNumber));
    		}
    	} else {
    		add(createNavLink("<", 1));
    		for(int i = pageNumber - (maxPaginationTabs / 2); i <= pageNumber + (maxPaginationTabs / 2); i++){
    			add(createNavLink(i, i==pageNumber));
    		}
    		add(createNavLink(">", navLinkCount));
    	}
	}
	
	private NavLink createNavLink(int i, boolean active) {
		return createNavLink(""+i, i, active);
	}
	
	private NavLink createNavLink(String text, int pageNumber) {
		return createNavLink(text, pageNumber, false);
	}

	private NavLink createNavLink(String text, int pageNumber, boolean active) {
		NavLink navLink = new NavLink();
		navLink.setText(text);
		navLink.setName(""+pageNumber);
		navLink.setActive(active);
		if(pageNumber > 0){
			navLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					IconAnchor thisThing = (IconAnchor) event.getSource();
					presenter.setPageNumber(thisThing.getName().trim());
			    	presenter.updateData();
				}
			});
		}
		return navLink;
	}
	
	public void setRowData(List<T> rowData, int totalRowCount) {
		this.totalRowCount = totalRowCount;
		updatePagination(rowData);
	}
	
}
