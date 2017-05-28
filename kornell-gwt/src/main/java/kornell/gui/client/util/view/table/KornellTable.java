package kornell.gui.client.util.view.table;

import java.util.List;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

import kornell.core.util.StringUtils;

public class KornellTable<T> extends CellTable<T>{
	
	private PaginationPresenter<T> presenter;
	private KornellPagination<T> pagination;
	private KornellTableTools<T> tableTools;
	private Timer refreshTableTimer;
	
	public KornellTable(PaginationPresenter<T> presenter, String cssClassName) {
		this.presenter = presenter;
		
		pagination = new KornellPagination<T>(this, presenter);	
		tableTools = new KornellTableTools<T>(presenter);	

		refreshTableTimer = new Timer() {
			@Override
			public void run() {
				refreshTable();
			}
		};
		
		// Create a data provider.
	    AsyncDataProvider<T> dataProvider = new AsyncDataProvider<T>() {
	      @Override
	      protected void onRangeChanged(HasData<T> display) {
	    	  scheduleRefreshTable();
	      }
	    };

	    // Connect the list to the data provider.
	    dataProvider.addDataDisplay(this);
		
		this.addStyleName("adminCellTable");
		if(StringUtils.isSome(cssClassName)){
			this.addStyleName(cssClassName);
		}
		this.addStyleName("lineWithoutLink");
		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		this.setWidth("100%", true);
		
		for (int i = 0; this.getColumnCount() > 0;) {
			this.removeColumn(i);
		}
	}

	private void scheduleRefreshTable() {   
        if(this.getColumnSortList().size() > 0){
	    	this.setVisible(false);
	    	pagination.setVisible(false);
        }
		
		refreshTableTimer.cancel();
		refreshTableTimer.schedule(200);
	}

	private void refreshTable() {
        final ColumnSortList sortList = this.getColumnSortList();	        
        if(sortList.size() > 0){
			presenter.setOrderBy(sortList.get(0).getColumn().getDataStoreName());
			presenter.setAsc(""+sortList.get(0).isAscending());
			presenter.setSearchTerm(tableTools.getSearchTerm());
			presenter.updateData();
        }
	}
	
	public void resetSearchTerm(){
		if(tableTools != null){
			tableTools.resetSearchTerm();
		}
	}

	public void build(FlowPanel wrapper, List<T> rowData) {
		wrapper.add(tableTools);
		wrapper.add(this);
		wrapper.add(pagination);
		
		pagination.setRowData(rowData, presenter.getTotalRowCount());
    	pagination.setVisible(presenter.getTotalRowCount() > Integer.parseInt(presenter.getPageSize()));
    	
    	tableTools.refresh();
    	
    	this.setVisible(true);
	}
	
	public void initColumn(String title, Integer width, String sortField, boolean defaultSortAscending, Column<T, ?> column){	
		if(StringUtils.isSome(sortField)){
			column.setSortable(true);
			column.setDataStoreName(sortField);
			column.setDefaultSortAscending(defaultSortAscending);
		}
		if(width > 0){
			this.setColumnWidth(column, width + "%");
		}
		this.addColumn(column, title);
	}
	
	public void initColumn(String title, Integer width, String sortField, Column<T, ?> column){	
		initColumn(title, width, sortField, true, column);
	}
	
	public void initColumn(String title, Integer width, Column<T, ?> column){	
		initColumn(title, width, null, column);
	}
	
	public void initColumn(String title, Column<T, ?> column){	
		initColumn(title, 0, null, column);
	}
	
	public void onColumnSetupFinished() {			
		this.addColumnSortHandler(new AsyncHandler(this));

		Column<T, ?> column;
		int width = 0;
		boolean showWarning = false;
		for (int i = 0; i < this.getColumnCount(); i++) {
			column = this.getColumn(i);
			if(presenter.getOrderBy().equals(column.getDataStoreName())){
				this.getColumnSortList().push(new ColumnSortInfo(column, presenter.getAsc() == "true"));
			}
			if(this.getColumnWidth(column) == null){
				showWarning = true;
			} else {
				String widthStr = this.getColumnWidth(column).split("%")[0];
				width += (Integer.parseInt(widthStr));
			}
		}
		if(showWarning || width != 100){
			GWT.log("Error with columns config: " + width);
		}	    
	}

	
}
