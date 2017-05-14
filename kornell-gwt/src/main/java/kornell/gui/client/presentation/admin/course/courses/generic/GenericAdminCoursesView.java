package kornell.gui.client.presentation.admin.course.courses.generic;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TextBox;
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
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.entity.Course;
import kornell.core.entity.EnrollmentState;
import kornell.core.to.CourseTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.presentation.admin.course.course.AdminCoursePlace;
import kornell.gui.client.presentation.admin.course.course.AdminCourseView;
import kornell.gui.client.presentation.admin.course.courses.AdminCoursesView;
import kornell.gui.client.util.AsciiUtils;
import kornell.gui.client.util.view.KornellPagination;

public class GenericAdminCoursesView extends Composite implements AdminCoursesView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminCoursesView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private PlaceController placeCtrl;
	final CellTable<Course> table;
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

	Tab adminsTab;
	FlowPanel adminsPanel;
	private AdminCourseView view;

	public GenericAdminCoursesView(final KornellSession session, final EventBus bus, final PlaceController placeCtrl, final ViewFactory viewFactory) {
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		table = new CellTable<Course>();
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
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		for (int i = 0; table.getColumnCount() > 0;) {
			table.removeColumn(i);
		}

		table.addColumn(new TextColumn<Course>() {
			@Override
			public String getValue(Course course) {
				return course.getCode();
			}
		}, "Código");

		table.addColumn(new TextColumn<Course>() {
			@Override
			public String getValue(Course course) {
				return course.getTitle();
			}
		}, "Nome");

		table.addColumn(new TextColumn<Course>() {
			@Override
			public String getValue(Course course) {
				return course.getDescription();
			}
		}, "Descrição");

		table.addColumn(new TextColumn<Course>() {
			@Override
			public String getValue(Course course) {
				return course.getContentSpec().toString();
			}
		}, "Tipo");

		List<HasCell<Course, ?>> cells = new LinkedList<HasCell<Course, ?>>();
		cells.add(new EnrollmentActionsHasCell("Gerenciar", getManageCourseDelegate(EnrollmentState.enrolled)));

		CompositeCell<Course> cell = new CompositeCell<Course>(cells);
		table.addColumn(new Column<Course, Course>(cell) {
			@Override
			public Course getValue(Course course) {
				return course;
			}
		}, "Ações");
		
		// Add a selection model to handle user selection.
		final SingleSelectionModel<Course> selectionModel = new SingleSelectionModel<Course>();
		table.setSelectionModel(selectionModel);
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						Course selected = selectionModel.getSelectedObject();
						if (selected != null) {
							placeCtrl.goTo(new AdminCoursePlace(selected.getUUID()));
						}
					}
				});
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		pagination = new KornellPagination(table, presenter);
	}

	private Delegate<Course> getManageCourseDelegate(final EnrollmentState state) {
		return new Delegate<Course>() {
			@Override
			public void execute(Course course) {
				placeCtrl.goTo(new AdminCoursePlace(course.getUUID()));
			}
		};
	}

	@SuppressWarnings("hiding")
  private class EnrollmentActionsActionCell<Course> extends ActionCell<Course> {
		
		public EnrollmentActionsActionCell(String message, Delegate<Course> delegate) {
			super(message, delegate);
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, Course value, NativeEvent event, ValueUpdater<Course> valueUpdater) {
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

	private class EnrollmentActionsHasCell implements HasCell<Course, Course> {
		private EnrollmentActionsActionCell<Course> cell;

		public EnrollmentActionsHasCell(String text, Delegate<Course> delegate) {
			final String actionName = text;
			cell = new EnrollmentActionsActionCell<Course>(text, delegate) {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, Course object, SafeHtmlBuilder sb) {
						SafeHtml html = SafeHtmlUtils.fromTrustedString(buildButtonHTML(actionName));
						sb.append(html);
				}
				
				private String buildButtonHTML(String actionName){
					Button btn = new Button();
					btn.setSize(ButtonSize.SMALL);
					btn.setTitle(actionName);
					if("Gerenciar".equals(actionName)){
						btn.setIcon(IconType.COG);
						btn.addStyleName("btnAction");
					}
					btn.addStyleName("btnIconSolo");
					return btn.toString();
				}
			};
		}

		@Override
		public Cell<Course> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<Course, Course> getFieldUpdater() {
			return null;
		}

		@Override
		public Course getValue(Course object) {
			return object;
		}
	}


	@Override
	public void setCourses(List<CourseTO> courses, Integer count, Integer searchCount) {
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

		List<Course> courseList = new ArrayList<>();
		for (CourseTO to : courses) {
			courseList.add(to.getCourse());
		}
		
		pagination.setRowData(courseList, StringUtils.isSome(presenter.getSearchTerm()) ? searchCount : count);
	
		initTable();
		adminHomePanel.setVisible(true);
	}


}