package kornell.gui.client.presentation.admin.course.courses.generic;

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
import kornell.core.entity.Course;
import kornell.core.entity.CourseDetailsEntityType;
import kornell.core.error.KornellErrorTO;
import kornell.core.to.CourseTO;
import kornell.core.to.CoursesTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.presentation.admin.common.ConfirmModalView;
import kornell.gui.client.presentation.admin.course.course.AdminCoursePlace;
import kornell.gui.client.presentation.admin.course.course.AdminCourseView;
import kornell.gui.client.presentation.admin.course.courses.AdminCoursesView;
import kornell.gui.client.util.AsciiUtils;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.KornellPagination;

public class GenericAdminCoursesView extends Composite implements AdminCoursesView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminCoursesView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private PlaceController placeCtrl;
	final CellTable<CourseTO> table;
	private AdminCoursesView.Presenter presenter;
	private KornellPagination pagination;
	private TextBox txtSearch;
	private Button btnSearch;
	private Timer updateTimer;

	@UiField
	FlowPanel adminHomePanel;
	@UiField
	FlowPanel coursesPanel;
	@UiField
	FlowPanel coursesWrapper;
	@UiField
	FlowPanel createCoursePanel;
	@UiField
	Button btnAddCourse;
	
	ConfirmModalView confirmModal;

	Tab adminsTab;
	FlowPanel adminsPanel;
	private AdminCourseView view;
	private boolean canPerformAction = true;
	private KornellSession session;
	private EventBus bus;
	private List<kornell.core.to.CourseTO> courseTOs;

	public GenericAdminCoursesView(final KornellSession session, final EventBus bus, final PlaceController placeCtrl, final ViewFactory viewFactory) {
		this.placeCtrl = placeCtrl;
		this.session = session;
		this.bus = bus;
		this.confirmModal = viewFactory.getConfirmModalView();
		initWidget(uiBinder.createAndBindUi(this));
		table = new CellTable<CourseTO>();
		btnAddCourse.setText("Criar Novo Curso");

		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						if(createCoursePanel.getWidgetCount() > 0){
							createCoursePanel.clear();
						}
						coursesPanel.setVisible(true);
						coursesWrapper.setVisible(true);
						view = null;
					}
				});

		btnAddCourse.setVisible(session.isInstitutionAdmin());
		btnAddCourse.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (session.isInstitutionAdmin()) {
					coursesPanel.setVisible(false);
					coursesWrapper.setVisible(false);
					view = viewFactory.getAdminCourseView();
					view.setPresenter(viewFactory.getAdminCoursePresenter());
					view.init();
					createCoursePanel.add(view);
				}
			}
		});

		updateTimer = new Timer() {
			@Override
			public void run() {
				filter();
			}
		};
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
		table.addStyleName("coursesCellTable");
		table.addStyleName("lineWithoutLink");
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		for (int i = 0; table.getColumnCount() > 0;) {
			table.removeColumn(i);
		}

		TextColumn<CourseTO> codeColumn = new TextColumn<CourseTO>() {
			@Override
			public String getValue(CourseTO courseTO) {
				return courseTO.getCourse().getCode();
			}
		};		
	    codeColumn.setSortable(true);
	    codeColumn.setDataStoreName("c.code");
		table.addColumn(codeColumn, "Código");
		

		List<HasCell<CourseTO, ?>> cellsC = new LinkedList<HasCell<CourseTO, ?>>();
		cellsC.add(new CourseLinkHasCell(CourseDetailsEntityType.COURSE.toString(), getGoToCourseDelegate()));
		CompositeCell<CourseTO> cellC = new CompositeCell<CourseTO>(cellsC);
        Column<CourseTO, CourseTO> titleColumn = new Column<CourseTO, CourseTO>(cellC) {
			@Override
			public CourseTO getValue(CourseTO courseTO) {
				return courseTO;
			}
		};	
	    titleColumn.setSortable(true);
	    titleColumn.setDataStoreName("c.title");
		table.addColumn(titleColumn, "Curso");
		

		TextColumn<CourseTO> descriptionColumn = new TextColumn<CourseTO>() {
			@Override
			public String getValue(CourseTO courseTO) {
				String description = courseTO.getCourse().getDescription();
				if(description.length() > 100){
					description = description.substring(0, 100) + " ...";
				}
				return description;
			}
		};		
	    descriptionColumn.setSortable(true);
	    descriptionColumn.setDataStoreName("c.description");
		table.addColumn(descriptionColumn, "Descrição");
		

		TextColumn<CourseTO> typeColumn = new TextColumn<CourseTO>() {
			@Override
			public String getValue(CourseTO courseTO) {
				return courseTO.getCourse().getContentSpec().toString();
			}
		};		
	    typeColumn.setSortable(true);
	    typeColumn.setDataStoreName("c.contentSpec");
		table.addColumn(typeColumn, "Tipo");
		
		
		table.addColumn(new TextColumn<CourseTO>() {
			@Override
			public String getValue(CourseTO courseTO) {
				return "" + courseTO.getCourseVersionsCount();
			}
		}, "Versões");
		

		List<HasCell<CourseTO, ?>> cells = new LinkedList<HasCell<CourseTO, ?>>();
		cells.add(new CourseActionsHasCell("Gerenciar", getGoToCourseDelegate()));
		cells.add(new CourseActionsHasCell("Excluir", getDeleteCourseDelegate()));
		CompositeCell<CourseTO> cell = new CompositeCell<CourseTO>(cells);
		table.addColumn(new Column<CourseTO, CourseTO>(cell) {
			@Override
			public CourseTO getValue(CourseTO courseTO) {
				return courseTO;
			}
		}, "Ações");
		
		
		// Create a data provider.
	    AsyncDataProvider<CourseTO> dataProvider = new AsyncDataProvider<CourseTO>() {
	      @Override
	      protected void onRangeChanged(HasData<CourseTO> display) {
	        final ColumnSortList sortList = table.getColumnSortList();	        
	        if(sortList.size() > 0){
	        	table.setVisible(false);
	        	pagination.setVisible(false);
				presenter.setOrderBy(sortList.get(0).getColumn().getDataStoreName());
				presenter.setAsc(sortList.get(0).isAscending());
				bus.fireEvent(new ShowPacifierEvent(true));
	    		session.courses().get(true, presenter.getPageSize(), presenter.getPageNumber(), presenter.getSearchTerm(), sortList.get(0).getColumn().getDataStoreName(), sortList.get(0).isAscending(), new Callback<CoursesTO>() {
	      			@Override
	      			public void ok(CoursesTO to) {
	      				courseTOs = to.getCourses();
	      				pagination.setRowData(courseTOs, StringUtils.isSome(presenter.getSearchTerm()) ? to.getSearchCount() : to.getCount());
	    	        	table.setVisible(true);
	    	        	pagination.setVisible(to.getCount() > to.getPageSize());
						bus.fireEvent(new ShowPacifierEvent(false));
	      			}
	      		});
	        }
	      }
	    };

	    // Connect the list to the data provider.
	    dataProvider.addDataDisplay(table);
	    table.addColumnSortHandler(new AsyncHandler(table));

		Column<CourseTO, ?> column;
		for (int i = 0; i < table.getColumnCount(); i++) {
			column = table.getColumn(i);
			if(presenter.getOrderBy().equals(column.getDataStoreName())){
			    table.getColumnSortList().push(column);
			    column.setDefaultSortAscending(presenter.getAsc());
			}
		}
	    
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		pagination = new KornellPagination(table, presenter);
	}

	private Delegate<CourseTO> getDeleteCourseDelegate() {
		return new Delegate<CourseTO>() {

			@Override
			public void execute(CourseTO courseTO) {
				if(canPerformAction){
					canPerformAction = false;

					confirmModal.showModal(
							"Tem certeza que deseja excluir o curso \"" + courseTO.getCourse().getTitle() + "\"?", 
							new com.google.gwt.core.client.Callback<Void, Void>() {
						@Override
						public void onSuccess(Void result) {
							bus.fireEvent(new ShowPacifierEvent(true));
							session.course(courseTO.getCourse().getUUID()).delete(new Callback<Course>() {	
								@Override
								public void ok(Course to) {
									canPerformAction = true;
									bus.fireEvent(new ShowPacifierEvent(false));
									KornellNotification.show("Curso excluído com sucesso.");
									presenter.updateData();
								}
								
								@Override
								public void internalServerError(KornellErrorTO error){
									canPerformAction = true;
									bus.fireEvent(new ShowPacifierEvent(false));
									KornellNotification.show("Erro ao tentar excluir o curso.", AlertType.ERROR);
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

	private Delegate<CourseTO> getGoToCourseDelegate() {
		return new Delegate<CourseTO>() {

			@Override
			public void execute(CourseTO courseTO) {
				placeCtrl.goTo(new AdminCoursePlace(courseTO.getCourse().getUUID()));
			}
		};
	}

	@SuppressWarnings("hiding")
	private class CourseActionsActionCell<CourseTO> extends ActionCell<CourseTO> {
		
		public CourseActionsActionCell(String message, Delegate<CourseTO> delegate) {
			super(message, delegate);
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, CourseTO value, NativeEvent event, ValueUpdater<CourseTO> valueUpdater) {
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
	public void setCourses(List<CourseTO> courseTOs, Integer count, Integer searchCount) {
		this.courseTOs = courseTOs;
		coursesWrapper.clear();
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
		
		coursesWrapper.add(tableTools);
		coursesWrapper.add(panel);
		coursesWrapper.add(pagination);
		
		pagination.setRowData(courseTOs, StringUtils.isSome(presenter.getSearchTerm()) ? searchCount : count);
	
		initTable();
		adminHomePanel.setVisible(true);
	}

	private class CourseActionsHasCell implements HasCell<CourseTO, CourseTO> {
		private CourseActionsActionCell<CourseTO> cell;

		public CourseActionsHasCell(String text, Delegate<CourseTO> delegate) {
			final String actionName = text;
			cell = new CourseActionsActionCell<CourseTO>(text, delegate) {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, CourseTO object, SafeHtmlBuilder sb) {
					if(!"Excluir".equals(actionName) || object.getCourseVersionsCount() == 0){
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
					}
					btn.addStyleName("btnIconSolo");
					return btn.toString();
				}
			};
		}

		@Override
		public Cell<CourseTO> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<CourseTO, CourseTO> getFieldUpdater() {
			return null;
		}

		@Override
		public CourseTO getValue(CourseTO object) {
			return object;
		}
	}

	private class CourseLinkHasCell implements HasCell<CourseTO, CourseTO> {
		private CourseActionsActionCell<CourseTO> cell;

		public CourseLinkHasCell(String text, Delegate<CourseTO> delegate) {
			final String actionName = text;
			cell = new CourseActionsActionCell<CourseTO>(text, delegate) {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, CourseTO courseTO, SafeHtmlBuilder sb) {
					if(!"Excluir".equals(actionName) || courseTO.getCourseVersionsCount() == 0){
						SafeHtml html = SafeHtmlUtils.fromTrustedString(buildButtonHTML(actionName, courseTO));
						sb.append(html);
					} else {
						sb.appendEscaped("");
					}
				}
				
				private String buildButtonHTML(String text, CourseTO courseTO) {
					Anchor anchor = new Anchor();
					if(CourseDetailsEntityType.COURSE.toString().equals(text)){
						anchor.setText(courseTO.getCourse().getTitle());
					} else if(CourseDetailsEntityType.COURSE_VERSION.toString().equals(text)){
						anchor.setText(courseTO.getCourse().getTitle());
					} 
					return anchor.toString();
				}
			};
		}

		@Override
		public Cell<CourseTO> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<CourseTO, CourseTO> getFieldUpdater() {
			return null;
		}

		@Override
		public CourseTO getValue(CourseTO object) {
			return object;
		}
	}


}