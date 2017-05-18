package kornell.gui.client.presentation.admin.courseversion.courseversions.generic;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import java.util.LinkedList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseDetailsEntityType;
import kornell.core.entity.CourseVersion;
import kornell.core.error.KornellErrorTO;
import kornell.core.to.CourseVersionTO;
import kornell.core.to.CourseVersionsTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.presentation.admin.common.ConfirmModalView;
import kornell.gui.client.presentation.admin.course.course.AdminCoursePlace;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionPlace;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionPresenter;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionView;
import kornell.gui.client.presentation.admin.courseversion.courseversions.AdminCourseVersionsView;
import kornell.gui.client.util.AsciiUtils;
import kornell.gui.client.util.ClientProperties;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.KornellPagination;

public class GenericAdminCourseVersionsView extends Composite implements AdminCourseVersionsView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminCourseVersionsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private PlaceController placeCtrl;
	final CellTable<CourseVersionTO> table;
	private KornellPagination pagination;
	private AdminCourseVersionsView.Presenter presenter;
	private FormHelper formHelper = GWT.create(FormHelper.class);
	private TextBox txtSearch;
	private Button btnSearch;
	private Timer updateTimer, refreshTableTimer;
	private boolean canPerformAction = true;

	@UiField
	FlowPanel adminHomePanel;
	@UiField
	FlowPanel courseVersionsPanel;
	@UiField
	FlowPanel courseVersionsWrapper;
	@UiField
	FlowPanel createVersionPanel;
	@UiField
	Button btnAddCourseVersion;
	
	ConfirmModalView confirmModal;

	Tab adminsTab;
	FlowPanel adminsPanel;
	private AdminCourseVersionView view;
	private AdminCourseVersionPresenter adminCourseVersionPresenter;
	private KornellSession session;
	private EventBus bus;

	public GenericAdminCourseVersionsView(final KornellSession session, final EventBus bus, final PlaceController placeCtrl, final ViewFactory viewFactory) {
		this.placeCtrl = placeCtrl;
		this.session = session;
		this.bus = bus;
		this.confirmModal = viewFactory.getConfirmModalView();
		initWidget(uiBinder.createAndBindUi(this));
		table = new CellTable<CourseVersionTO>();
		btnAddCourseVersion.setText("Criar Nova Versão");


		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						if(createVersionPanel.getWidgetCount() > 0){
							createVersionPanel.clear();
						}
						courseVersionsPanel.setVisible(true);
						courseVersionsWrapper.setVisible(true);
						view = null;
					}
				});

		btnAddCourseVersion.setVisible(session.isInstitutionAdmin());
		btnAddCourseVersion.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (session.isInstitutionAdmin()) {
					courseVersionsPanel.setVisible(false);
					courseVersionsWrapper.setVisible(false);
					adminCourseVersionPresenter = viewFactory.getAdminCourseVersionPresenter();
					view = adminCourseVersionPresenter.getView();
					view.setPresenter(adminCourseVersionPresenter);
					view.init();
					createVersionPanel.add(view);
				}
			}
		});

		updateTimer = new Timer() {
			@Override
			public void run() {
				filter();
			}
		};

		refreshTableTimer = new Timer() {
			@Override
			public void run() {
				refreshTable();
			}
		};
		
		
		// Create a data provider.
	    AsyncDataProvider<CourseVersionTO> dataProvider = new AsyncDataProvider<CourseVersionTO>() {
	      @Override
	      protected void onRangeChanged(HasData<CourseVersionTO> display) {
	    	  scheduleRefreshTable();
	      }
	    };

	    // Connect the list to the data provider.
	    dataProvider.addDataDisplay(table);
	}

	private void scheduleRefreshTable() {   
        if(table.getColumnSortList().size() > 0){
	    	table.setVisible(false);
	    	pagination.setVisible(false);
			bus.fireEvent(new ShowPacifierEvent(true));
        }
		
		refreshTableTimer.cancel();
		refreshTableTimer.schedule(200);
	}

	private void refreshTable() {
	    final ColumnSortList sortList = table.getColumnSortList();	        
	    if(sortList.size() > 0){
			final String orderBy = sortList.get(0).getColumn().getDataStoreName();
			final String asc = ""+sortList.get(0).isAscending();
			presenter.setOrderBy(orderBy);
			presenter.setAsc(asc);
			session.courseVersions().get(presenter.getPageSize(), presenter.getPageNumber(), presenter.getSearchTerm(), orderBy, asc, new Callback<CourseVersionsTO>() {
	  			@Override
	  			public void ok(CourseVersionsTO to) {
	  				pagination.setRowData(to.getCourseVersionTOs(), StringUtils.isSome(presenter.getSearchTerm()) ? to.getSearchCount() : to.getCount());
		        	table.setVisible(true);
		        	pagination.setVisible(to.getCount() > to.getPageSize());
					bus.fireEvent(new ShowPacifierEvent(false));

					ClientProperties.set(presenter.getClientPropertyName("orderBy"), orderBy);
					ClientProperties.set(presenter.getClientPropertyName("asc"), asc);
	  			}
	  		});
	    }
	}

	private void scheduleFilter() {
		updateTimer.cancel();
		updateTimer.schedule(500);
	}

	private void filter() {
		String newSearchTerm = AsciiUtils.convertNonAscii(txtSearch.getText().trim()).toLowerCase();
		if(!presenter.getSearchTerm().equals(newSearchTerm)){
			presenter.setPageNumber("1");
			presenter.setSearchTerm(newSearchTerm);
			presenter.updateData();
		}
	}

	private void initSearch() {
		if (txtSearch == null) {
			txtSearch = new TextBox();
			txtSearch.addStyleName("txtSearch");
			txtSearch.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					scheduleFilter();
				}
			});
			txtSearch.addKeyUpHandler(new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent event) {
					scheduleFilter();
				}
			});
			txtSearch.addValueChangeHandler(new ValueChangeHandler<String>() {

				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					scheduleFilter();

				}
			});
			btnSearch = new Button("Pesquisar");
			btnSearch.setSize(ButtonSize.MINI);
			btnSearch.setIcon(IconType.SEARCH);
			btnSearch.addStyleName("btnNotSelected btnSearch");
			btnSearch.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					scheduleFilter();
				}
			});
		}
		txtSearch.setValue(presenter.getSearchTerm());
		txtSearch.setTitle("insira o nome da turma, da versão ou do curso");
	}
	
	private void initTable() {
		
		table.addStyleName("adminCellTable");
		table.addStyleName("courseVersionsCellTable");
		table.addStyleName("lineWithoutLink");
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		table.setWidth("100%", true);
		
		for (int i = 0; table.getColumnCount() > 0;) {
			table.removeColumn(i);
		}


		List<HasCell<CourseVersionTO, ?>> cellsC = new LinkedList<HasCell<CourseVersionTO, ?>>();
		cellsC.add(new CourseVersionLinkHasCell(CourseDetailsEntityType.COURSE.toString(), getGoToCourseDelegate()));
		CompositeCell<CourseVersionTO> cellC = new CompositeCell<CourseVersionTO>(cellsC);
        Column<CourseVersionTO, CourseVersionTO> courseColumn = new Column<CourseVersionTO, CourseVersionTO>(cellC) {
			@Override
			public CourseVersionTO getValue(CourseVersionTO courseVersionTO) {
				return courseVersionTO;
			}
		};	
	    courseColumn.setSortable(true);
	    courseColumn.setDataStoreName("c.title");
		table.setColumnWidth(courseColumn, "20%");
		table.addColumn(courseColumn, "Curso");
		

		List<HasCell<CourseVersionTO, ?>> cellsCV = new LinkedList<HasCell<CourseVersionTO, ?>>();
		cellsCV.add(new CourseVersionLinkHasCell(CourseDetailsEntityType.COURSE_VERSION.toString(), getGoToCourseVersionDelegate()));
		CompositeCell<CourseVersionTO> cellCV = new CompositeCell<CourseVersionTO>(cellsCV);
        Column<CourseVersionTO, CourseVersionTO> versionColumn = new Column<CourseVersionTO, CourseVersionTO>(cellCV) {
			@Override
			public CourseVersionTO getValue(CourseVersionTO courseVersionTO) {
				return courseVersionTO;
			}
		};	
	    versionColumn.setSortable(true);
	    versionColumn.setDataStoreName("cv.name");
		table.setColumnWidth(versionColumn, "20%");
		table.addColumn(versionColumn, "Versão");
		

		TextColumn<CourseVersionTO> distributionPrefixColumn = new TextColumn<CourseVersionTO>() {
			@Override
			public String getValue(CourseVersionTO courseVersionTO) {
				return courseVersionTO.getCourseVersion().getDistributionPrefix();
			}
		};		
	    distributionPrefixColumn.setSortable(true);
	    distributionPrefixColumn.setDataStoreName("cv.distributionPrefix");
		table.setColumnWidth(distributionPrefixColumn, "20%");
		table.addColumn(distributionPrefixColumn, "Prefixo de Distribuição");
		

		TextColumn<CourseVersionTO> statusColumn = new TextColumn<CourseVersionTO>() {
			@Override
			public String getValue(CourseVersionTO courseVersionTO) {
				return courseVersionTO.getCourseVersion().isDisabled() ? "Desativada" : "Ativa";
			}
		};		
	    statusColumn.setSortable(true);
	    statusColumn.setDataStoreName("cv.disabled");
		table.setColumnWidth(statusColumn, "10%");
		table.addColumn(statusColumn, "Status");
		
		
		TextColumn<CourseVersionTO> creationDateColumn = new TextColumn<CourseVersionTO>() {
			@Override
			public String getValue(CourseVersionTO courseVersionTO) {
				return formHelper.dateToString(courseVersionTO.getCourseVersion().getVersionCreatedAt());
			}
		};		
	    creationDateColumn.setSortable(true);
	    creationDateColumn.setDataStoreName("cv.versionCreatedAt");
		table.setColumnWidth(creationDateColumn, "10%");
		table.addColumn(creationDateColumn, "Criada em");
		
		TextColumn<CourseVersionTO> classesColumn = new TextColumn<CourseVersionTO>() {
			@Override
			public String getValue(CourseVersionTO courseVersionTO) {
				return "" + courseVersionTO.getCourseClassesCount();
			}
		};
		table.setColumnWidth(classesColumn, "10%");
		table.addColumn(classesColumn, "Turmas");

		List<HasCell<CourseVersionTO, ?>> cells = new LinkedList<HasCell<CourseVersionTO, ?>>();
		cells.add(new CourseVersionActionsHasCell("Gerenciar", getManageCourseVersionDelegate()));
		cells.add(new CourseVersionActionsHasCell("Duplicar", getDuplicateCourseVersionDelegate()));
		cells.add(new CourseVersionActionsHasCell("Excluir", getDeleteCourseVersionDelegate()));

		CompositeCell<CourseVersionTO> cell = new CompositeCell<CourseVersionTO>(cells);
		Column<CourseVersionTO, CourseVersionTO> actionsColumn = new Column<CourseVersionTO, CourseVersionTO>(cell) {
			@Override
			public CourseVersionTO getValue(CourseVersionTO courseVersionTO) {
				return courseVersionTO;
			}
		};
		table.setColumnWidth(actionsColumn, "10%");
		table.addColumn(actionsColumn, "Ações");
		
		
	    table.addColumnSortHandler(new AsyncHandler(table));

		Column<CourseVersionTO, ?> column;
		int width = 0;
		boolean showWarning = false;
		for (int i = 0; i < table.getColumnCount(); i++) {
			column = table.getColumn(i);
			if(presenter.getOrderBy().equals(column.getDataStoreName())){
			    table.getColumnSortList().push(new ColumnSortInfo(column, presenter.getAsc() == "true"));
			}
			if(table.getColumnWidth(column) == null){
				showWarning = true;;
			} else {
				String widthStr = table.getColumnWidth(column).split("%")[0];
				width += (Integer.parseInt(widthStr));
			}
		}
		if(showWarning || width != 100){
			GWT.log("Error with columns config: " + width);
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		pagination = new KornellPagination(table, presenter);
	}

	private Delegate<CourseVersionTO> getManageCourseVersionDelegate() {
		return new Delegate<CourseVersionTO>() {
			@Override
			public void execute(CourseVersionTO courseVersion) {
				placeCtrl.goTo(new AdminCourseVersionPlace(courseVersion.getCourseVersion().getUUID()));
			}
		};
	}

	private Delegate<CourseVersionTO> getDeleteCourseVersionDelegate() {
		return new Delegate<CourseVersionTO>() {

			@Override
			public void execute(CourseVersionTO courseVersionTO) {
				if(canPerformAction){
					canPerformAction = false;

					confirmModal.showModal(
							"Tem certeza que deseja excluir a versão \"" + courseVersionTO.getCourseVersion().getName() + "\"?", 
							new com.google.gwt.core.client.Callback<Void, Void>() {
						@Override
						public void onSuccess(Void result) {
							bus.fireEvent(new ShowPacifierEvent(true));
							session.courseVersion(courseVersionTO.getCourseVersion().getUUID()).delete(new Callback<CourseVersion>() {	
								@Override
								public void ok(CourseVersion to) {
									canPerformAction = true;
									bus.fireEvent(new ShowPacifierEvent(false));
									KornellNotification.show("Versão excluída com sucesso.");
									presenter.updateData();
								}
								
								@Override
								public void internalServerError(KornellErrorTO error){
									canPerformAction = true;
									bus.fireEvent(new ShowPacifierEvent(false));
									KornellNotification.show("Erro ao tentar excluir a versão.", AlertType.ERROR);
								}
							});
						}
						@Override
						public void onFailure(Void reason) {
							canPerformAction = true;
						}
					});
				}
			}
		};
	}

	private Delegate<CourseVersionTO> getDuplicateCourseVersionDelegate() {
		return new Delegate<CourseVersionTO>() {

			@Override
			public void execute(CourseVersionTO courseVersionTO) {
				if(canPerformAction){
					canPerformAction = false;

					confirmModal.showModal(
							"Tem certeza que deseja duplicar a versão \"" + courseVersionTO.getCourseVersion().getName() + "\"?", 
							new com.google.gwt.core.client.Callback<Void, Void>() {
						@Override
						public void onSuccess(Void result) {
							bus.fireEvent(new ShowPacifierEvent(true));
							session.courseVersion(courseVersionTO.getCourseVersion().getUUID()).copy(new Callback<CourseVersion>() {	
								@Override
								public void ok(CourseVersion courseVersion) {
									canPerformAction = true;
									bus.fireEvent(new ShowPacifierEvent(false));
									KornellNotification.show("Versão duplicada com sucesso.");
									placeCtrl.goTo(new AdminCourseVersionPlace(courseVersion.getUUID()));
								}
								
								@Override
								public void internalServerError(KornellErrorTO error){
									canPerformAction = true;
									bus.fireEvent(new ShowPacifierEvent(false));
									KornellNotification.show("Erro ao tentar duplicar a versão.", AlertType.ERROR);
								}
								
								@Override
								public void conflict(KornellErrorTO error){
									canPerformAction = true;
									bus.fireEvent(new ShowPacifierEvent(false));
									KornellNotification.show("Erro ao tentar duplicar a versão. Verifique se já existe uma versão com o nome \"" + courseVersionTO.getCourseVersion().getName() + "\" (2).", AlertType.ERROR, 5000);
								}
							});
						}
						@Override
						public void onFailure(Void reason) {
							canPerformAction = true;
						}
					});
				}
			}
		};
	}

	private Delegate<CourseVersionTO> getGoToCourseDelegate() {
		return new Delegate<CourseVersionTO>() {

			@Override
			public void execute(CourseVersionTO courseVersionTO) {
				placeCtrl.goTo(new AdminCoursePlace(courseVersionTO.getCourseTO().getCourse().getUUID()));
			}
		};
	}

	private Delegate<CourseVersionTO> getGoToCourseVersionDelegate() {
		return new Delegate<CourseVersionTO>() {
			@Override
			public void execute(CourseVersionTO courseVersionTO) {
				placeCtrl.goTo(new AdminCourseVersionPlace(courseVersionTO.getCourseVersion().getUUID()));
			}
		};
	}

	@SuppressWarnings("hiding")
	private class CourseVersionActionsActionCell<CourseVersionTO> extends ActionCell<CourseVersionTO> {
		
		public CourseVersionActionsActionCell(String message, Delegate<CourseVersionTO> delegate) {
			super(message, delegate);
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, CourseVersionTO value, NativeEvent event, ValueUpdater<CourseVersionTO> valueUpdater) {
			event.stopPropagation();
			event.preventDefault();
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
			if (CLICK.equals(event.getType())) {
				EventTarget eventTarget = event.getEventTarget();
				if (!Element.is(eventTarget)) {
					return;
				}
				if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget))) {
					// Ignore clicks that occur outside of the main element.
					onEnterKeyDown(context, parent, value, event, valueUpdater);
				}
			}
		}
	}


	@Override
	public void setCourseVersions(List<CourseVersionTO> courseVersionTOs, Integer count, Integer searchCount) {
		courseVersionsWrapper.clear();
		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("400");
		panel.add(table);

		final ListBox pageSizeListBox = new ListBox();
		// pageSizeListBox.addItem("1");
		// pageSizeListBox.addItem("10");
		pageSizeListBox.addStyleName("pageSizeListBox");
		pageSizeListBox.addItem("20");
		pageSizeListBox.addItem("50");
		pageSizeListBox.addItem("100");
		pageSizeListBox.setSelectedValue(presenter.getPageSize());
		pageSizeListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (pageSizeListBox.getValue().matches("[0-9]*")){
					presenter.setPageNumber("1");
					presenter.setPageSize(pageSizeListBox.getValue());
					presenter.updateData();
				}
			}
		});

		initSearch();
		FlowPanel tableTools = new FlowPanel();
		tableTools.addStyleName("marginTop25");
		tableTools.add(txtSearch);
		tableTools.add(btnSearch);
		tableTools.add(pageSizeListBox);
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				txtSearch.setFocus(true);
			}
		});
		
		courseVersionsWrapper.add(tableTools);
		courseVersionsWrapper.add(panel);
		courseVersionsWrapper.add(pagination);

		pagination.setRowData(courseVersionTOs, StringUtils.isSome(presenter.getSearchTerm()) ? searchCount : count);
	
		initTable();
		adminHomePanel.setVisible(true);
	}

	private class CourseVersionActionsHasCell implements HasCell<CourseVersionTO, CourseVersionTO> {
		private CourseVersionActionsActionCell<CourseVersionTO> cell;

		public CourseVersionActionsHasCell(String text, Delegate<CourseVersionTO> delegate) {
			final String actionName = text;
			cell = new CourseVersionActionsActionCell<CourseVersionTO>(text, delegate) {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, CourseVersionTO object, SafeHtmlBuilder sb) {
					if(!"Excluir".equals(actionName) || object.getCourseClassesCount() == 0){
						SafeHtml html = SafeHtmlUtils.fromTrustedString(buildButtonHTML(actionName));
						sb.append(html);
					} else {
						sb.appendEscaped("");
					}
				}
				
				private String buildButtonHTML(String actionName){
					Button btn = new Button();
					btn.setSize(ButtonSize.SMALL);
					btn.setTitle(actionName);
					if("Gerenciar".equals(actionName)){
						btn.setIcon(IconType.COG);
						btn.addStyleName("btnAction");
					} else if ("Excluir".equals(actionName)){
						btn.setIcon(IconType.TRASH);
						btn.addStyleName("btnNotSelected");
					} else if ("Duplicar".equals(actionName)){
						btn.setIcon(IconType.COPY);
						btn.addStyleName("btnNotSelected");
					}
					btn.addStyleName("btnIconSolo");
					return btn.toString();
				}
			};
		}

		@Override
		public Cell<CourseVersionTO> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<CourseVersionTO, CourseVersionTO> getFieldUpdater() {
			return null;
		}

		@Override
		public CourseVersionTO getValue(CourseVersionTO object) {
			return object;
		}
	}

	private class CourseVersionLinkHasCell implements HasCell<CourseVersionTO, CourseVersionTO> {
		private CourseVersionActionsActionCell<CourseVersionTO> cell;

		public CourseVersionLinkHasCell(String text, Delegate<CourseVersionTO> delegate) {
			final String actionName = text;
			cell = new CourseVersionActionsActionCell<CourseVersionTO>(text, delegate) {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, CourseVersionTO courseVersionTO, SafeHtmlBuilder sb) {
					if(!"Excluir".equals(actionName) || courseVersionTO.getCourseClassesCount() == 0){
						SafeHtml html = SafeHtmlUtils.fromTrustedString(buildButtonHTML(actionName, courseVersionTO));
						sb.append(html);
					} else {
						sb.appendEscaped("");
					}
				}
				
				private String buildButtonHTML(String text, CourseVersionTO courseVersionTO) {
					Anchor anchor = new Anchor();
					if(CourseDetailsEntityType.COURSE.toString().equals(text)){
						anchor.setText(courseVersionTO.getCourseTO().getCourse().getTitle());
					} else if(CourseDetailsEntityType.COURSE_VERSION.toString().equals(text)){
						anchor.setText(courseVersionTO.getCourseVersion().getName());
					} 
					return anchor.toString();
				}
			};
		}

		@Override
		public Cell<CourseVersionTO> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<CourseVersionTO, CourseVersionTO> getFieldUpdater() {
			return null;
		}

		@Override
		public CourseVersionTO getValue(CourseVersionTO object) {
			return object;
		}
	}

}