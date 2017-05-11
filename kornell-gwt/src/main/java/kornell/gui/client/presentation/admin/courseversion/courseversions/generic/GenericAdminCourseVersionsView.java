package kornell.gui.client.presentation.admin.courseversion.courseversions.generic;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

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
import kornell.core.entity.CourseVersion;
import kornell.core.entity.EnrollmentState;
import kornell.core.util.StringUtils;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionPlace;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionPresenter;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionView;
import kornell.gui.client.presentation.admin.courseversion.courseversions.AdminCourseVersionsView;
import kornell.gui.client.util.AsciiUtils;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.view.KornellPagination;

public class GenericAdminCourseVersionsView extends Composite implements AdminCourseVersionsView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminCourseVersionsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private PlaceController placeCtrl;
	final CellTable<CourseVersion> table;
	private KornellPagination pagination;
	private AdminCourseVersionsView.Presenter presenter;
	private FormHelper formHelper = GWT.create(FormHelper.class);
	private TextBox txtSearch;
	private Button btnSearch;
	private Timer updateTimer;

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

	Tab adminsTab;
	FlowPanel adminsPanel;
	private AdminCourseVersionView view;
	private AdminCourseVersionPresenter adminCourseVersionPresenter;

	public GenericAdminCourseVersionsView(final KornellSession session, final EventBus bus, final PlaceController placeCtrl, final ViewFactory viewFactory) {
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		table = new CellTable<CourseVersion>();
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

		table.addColumn(new TextColumn<CourseVersion>() {
			@Override
			public String getValue(CourseVersion courseVersion) {
				return courseVersion.getName();
			}
		}, "Nome");

//		table.addColumn(new TextColumn<CourseVersion>() {
//			@Override
//			public String getValue(CourseVersion courseVersion) {
//				return courseVersion.getContentSpec().toString();
//			}
//		}, "Tipo");

		table.addColumn(new TextColumn<CourseVersion>() {
			@Override
			public String getValue(CourseVersion courseVersion) {
				return courseVersion.getDistributionPrefix();
			}
		}, "Prefixo de Distribuição");
		
		table.addColumn(new TextColumn<CourseVersion>() {
			@Override
			public String getValue(CourseVersion courseVersion) {
				return courseVersion.isDisabled() ? "Desativada" : "Ativa";
			}
		}, "Status");
		
		table.addColumn(new TextColumn<CourseVersion>() {
			@Override
			public String getValue(CourseVersion courseVersion) {
				return formHelper.dateToString(courseVersion.getVersionCreatedAt());
			}
		}, "Data de Criação");

		List<HasCell<CourseVersion, ?>> cells = new LinkedList<HasCell<CourseVersion, ?>>();
		cells.add(new EnrollmentActionsHasCell("Gerenciar", getManageCourseVersionDelegate(EnrollmentState.enrolled)));

		CompositeCell<CourseVersion> cell = new CompositeCell<CourseVersion>(cells);
		table.addColumn(new Column<CourseVersion, CourseVersion>(cell) {
			@Override
			public CourseVersion getValue(CourseVersion courseVersion) {
				return courseVersion;
			}
		}, "Ações");
		
		// Add a selection model to handle user selection.
		final SingleSelectionModel<CourseVersion> selectionModel = new SingleSelectionModel<CourseVersion>();
		table.setSelectionModel(selectionModel);
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						CourseVersion selected = selectionModel.getSelectedObject();
						if (selected != null) {
							placeCtrl.goTo(new AdminCourseVersionPlace(selected.getUUID()));
						}
					}
				});
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		pagination = new KornellPagination(table, presenter);
	}

	private Delegate<CourseVersion> getManageCourseVersionDelegate(final EnrollmentState state) {
		return new Delegate<CourseVersion>() {
			@Override
			public void execute(CourseVersion courseVersion) {
				placeCtrl.goTo(new AdminCourseVersionPlace(courseVersion.getUUID()));
			}
		};
	}

	@SuppressWarnings("hiding")
  private class EnrollmentActionsActionCell<CourseVersion> extends ActionCell<CourseVersion> {
		
		public EnrollmentActionsActionCell(String message, Delegate<CourseVersion> delegate) {
			super(message, delegate);
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, CourseVersion value, NativeEvent event, ValueUpdater<CourseVersion> valueUpdater) {
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

	private class EnrollmentActionsHasCell implements HasCell<CourseVersion, CourseVersion> {
		private EnrollmentActionsActionCell<CourseVersion> cell;

		public EnrollmentActionsHasCell(String text, Delegate<CourseVersion> delegate) {
			final String actionName = text;
			cell = new EnrollmentActionsActionCell<CourseVersion>(text, delegate) {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, CourseVersion object, SafeHtmlBuilder sb) {
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
		public Cell<CourseVersion> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<CourseVersion, CourseVersion> getFieldUpdater() {
			return null;
		}

		@Override
		public CourseVersion getValue(CourseVersion object) {
			return object;
		}
	}


	@Override
	public void setCourseVersions(List<CourseVersion> courseVersions, Integer count, Integer searchCount) {
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

		pagination.setRowData(courseVersions, StringUtils.isSome(presenter.getSearchTerm()) ? searchCount : count);
	
		initTable();
		adminHomePanel.setVisible(true);
	}


}