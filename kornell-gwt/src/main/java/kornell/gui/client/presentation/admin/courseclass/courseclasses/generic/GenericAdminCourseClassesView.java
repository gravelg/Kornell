package kornell.gui.client.presentation.admin.courseclass.courseclasses.generic;

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
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseDetailsEntityType;
import kornell.core.error.KornellErrorTO;
import kornell.core.to.CourseClassTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.presentation.admin.common.ConfirmModalView;
import kornell.gui.client.presentation.admin.course.course.AdminCoursePlace;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassPlace;
import kornell.gui.client.presentation.admin.courseclass.courseclass.generic.GenericCourseClassConfigView;
import kornell.gui.client.presentation.admin.courseclass.courseclasses.AdminCourseClassesView;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionPlace;
import kornell.gui.client.util.AsciiUtils;
import kornell.gui.client.util.EnumTranslator;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.KornellPagination;

public class GenericAdminCourseClassesView extends Composite implements AdminCourseClassesView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminCourseClassesView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private PlaceController placeCtrl;
	final CellTable<CourseClassTO> table;
	private KornellPagination pagination;
	private AdminCourseClassesView.Presenter presenter;
	private FormHelper formHelper = GWT.create(FormHelper.class);
	private TextBox txtSearch;
	private Button btnSearch;
	private Timer updateTimer;
	private boolean canPerformAction = true;

	@UiField
	FlowPanel adminHomePanel;
	@UiField
	FlowPanel courseClassesPanel;
	@UiField
	FlowPanel courseClassesWrapper;
	@UiField
	FlowPanel createClassPanel;
	@UiField
	Button btnAddCourseClass;
	
	ConfirmModalView confirmModal;

	Tab adminsTab;
	FlowPanel adminsPanel;
	private KornellSession session;
	private EventBus bus;

	public GenericAdminCourseClassesView(final KornellSession session, final EventBus bus, final PlaceController placeCtrl, final ViewFactory viewFactory) {
		this.placeCtrl = placeCtrl;
		this.session = session;
		this.bus = bus;
		this.confirmModal = viewFactory.getConfirmModalView();
		initWidget(uiBinder.createAndBindUi(this));
		table = new CellTable<CourseClassTO>();
		btnAddCourseClass.setText("Criar Nova Turma");


		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						if(createClassPanel.getWidgetCount() > 0){
							createClassPanel.clear();
							courseClassesPanel.setVisible(true);
							courseClassesWrapper.setVisible(true);
						}
					}
				});

		btnAddCourseClass.setVisible(session.isInstitutionAdmin());
		btnAddCourseClass.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (session.isInstitutionAdmin()) {
					courseClassesPanel.setVisible(false);
					courseClassesWrapper.setVisible(false);
					createClassPanel.add(new GenericCourseClassConfigView(session, bus, placeCtrl, viewFactory.getAdminCourseClassPresenter(), null));
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
		table.addStyleName("courseClassesCellTable");
		table.addStyleName("lineWithoutLink");
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		for (int i = 0; table.getColumnCount() > 0;) {
			table.removeColumn(i);
		}

		List<HasCell<CourseClassTO, ?>> cellsC = new LinkedList<HasCell<CourseClassTO, ?>>();
		cellsC.add(new CourseClassLinkHasCell(CourseDetailsEntityType.COURSE.toString(), getGoToCourseDelegate()));
		CompositeCell<CourseClassTO> cellC = new CompositeCell<CourseClassTO>(cellsC);
		table.addColumn(new Column<CourseClassTO, CourseClassTO>(cellC) {
			@Override
			public CourseClassTO getValue(CourseClassTO courseClassTO) {
				return courseClassTO;
			}
		}, "Curso");

		List<HasCell<CourseClassTO, ?>> cellsCV = new LinkedList<HasCell<CourseClassTO, ?>>();
		cellsCV.add(new CourseClassLinkHasCell(CourseDetailsEntityType.COURSE_VERSION.toString(), getGoToCourseVersionDelegate()));
		CompositeCell<CourseClassTO> cellCV = new CompositeCell<CourseClassTO>(cellsCV);
		table.addColumn(new Column<CourseClassTO, CourseClassTO>(cellCV) {
			@Override
			public CourseClassTO getValue(CourseClassTO courseClassTO) {
				return courseClassTO;
			}
		}, "Versão");

		List<HasCell<CourseClassTO, ?>> cellsCC = new LinkedList<HasCell<CourseClassTO, ?>>();
		cellsCC.add(new CourseClassLinkHasCell(CourseDetailsEntityType.COURSE_CLASS.toString(), getGoToCourseClassDelegate()));
		CompositeCell<CourseClassTO> cellCC = new CompositeCell<CourseClassTO>(cellsCC);
		table.addColumn(new Column<CourseClassTO, CourseClassTO>(cellCC) {
			@Override
			public CourseClassTO getValue(CourseClassTO courseClassTO) {
				return courseClassTO;
			}
		}, "Turma");
		
		table.addColumn(new TextColumn<CourseClassTO>() {
			@Override
			public String getValue(CourseClassTO courseClassTO) {
				String value = EnumTranslator.translateEnum(courseClassTO.getCourseClass().getState());
				value += courseClassTO.getCourseClass().isInvisible() ? " / Invísivel" : "";
				value += courseClassTO.getCourseClass().isPublicClass() ? " / Pública" : "";
				return value;
			}
		}, "Status");
		
		table.addColumn(new TextColumn<CourseClassTO>() {
			@Override
			public String getValue(CourseClassTO courseClassTO) {
				String text = courseClassTO.getEnrollmentCount() + " (por ";
				text += EnumTranslator.translateEnum(courseClassTO.getCourseClass().getRegistrationType()) + ")";
				return text;
			}
		}, "Matrículas");
		
		table.addColumn(new TextColumn<CourseClassTO>() {
			@Override
			public String getValue(CourseClassTO courseClassTO) {
				return formHelper.dateToString(courseClassTO.getCourseClass().getCreatedAt());
			}
		}, "Data de Criação");

		List<HasCell<CourseClassTO, ?>> cells = new LinkedList<HasCell<CourseClassTO, ?>>();
		cells.add(new CourseClassActionsHasCell("Gerenciar", getManageCourseClassDelegate()));
		cells.add(new CourseClassActionsHasCell("Excluir", getDeleteCourseClassDelegate()));
		CompositeCell<CourseClassTO> cell = new CompositeCell<CourseClassTO>(cells);
		table.addColumn(new Column<CourseClassTO, CourseClassTO>(cell) {
			@Override
			public CourseClassTO getValue(CourseClassTO courseClassTO) {
				return courseClassTO;
			}
		}, "Ações");
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		pagination = new KornellPagination(table, presenter);
	}

	private Delegate<CourseClassTO> getGoToCourseDelegate() {
		return new Delegate<CourseClassTO>() {
			@Override
			public void execute(CourseClassTO courseClassTO) {
				placeCtrl.goTo(new AdminCoursePlace(courseClassTO.getCourseVersionTO().getCourseTO().getCourse().getUUID()));
			}
		};
	}

	private Delegate<CourseClassTO> getGoToCourseVersionDelegate() {
		return new Delegate<CourseClassTO>() {
			@Override
			public void execute(CourseClassTO courseClassTO) {
				placeCtrl.goTo(new AdminCourseVersionPlace(courseClassTO.getCourseVersionTO().getCourseVersion().getUUID()));
			}
		};
	}

	private Delegate<CourseClassTO> getGoToCourseClassDelegate() {
		return new Delegate<CourseClassTO>() {
			@Override
			public void execute(CourseClassTO courseClassTO) {
				placeCtrl.goTo(new AdminCourseClassPlace(courseClassTO.getCourseClass().getUUID()));
			}
		};
	}

	private Delegate<CourseClassTO> getManageCourseClassDelegate() {
		return new Delegate<CourseClassTO>() {
			@Override
			public void execute(CourseClassTO courseClassTO) {
				if(canPerformAction){
					placeCtrl.goTo(new AdminCourseClassPlace(courseClassTO.getCourseClass().getUUID()));
				}
			}
		};
	}

	private Delegate<CourseClassTO> getDeleteCourseClassDelegate() {
		return new Delegate<CourseClassTO>() {

			@Override
			public void execute(CourseClassTO courseClassTO) {
				if(canPerformAction){
					canPerformAction = false;

					confirmModal.showModal(
							"Tem certeza que deseja excluir a turma \"" + courseClassTO.getCourseClass().getName() + "\"?", 
							new com.google.gwt.core.client.Callback<Void, Void>() {
						@Override
						public void onSuccess(Void result) {
							bus.fireEvent(new ShowPacifierEvent(true));
							session.courseClass(courseClassTO.getCourseClass().getUUID()).delete(new Callback<CourseClass>() {	
								@Override
								public void ok(CourseClass to) {
									canPerformAction = true;
									bus.fireEvent(new ShowPacifierEvent(false));
									KornellNotification.show("Turma excluída com sucesso.");
									presenter.updateData();
								}
								
								@Override
								public void internalServerError(KornellErrorTO error){
									canPerformAction = true;
									bus.fireEvent(new ShowPacifierEvent(false));
									KornellNotification.show("Erro ao tentar excluir a turma.", AlertType.ERROR);
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


	@Override
	public void setCourseClasses(List<CourseClassTO> courseClasses, Integer count, Integer searchCount) {
		courseClassesWrapper.clear();
		if(courseClasses == null){
			adminHomePanel.setVisible(false);
			return;
		}
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
					presenter.updateCourseClass("");
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
		
		courseClassesWrapper.add(tableTools);
		courseClassesWrapper.add(panel);
		courseClassesWrapper.add(pagination);

		pagination.setRowData(courseClasses, StringUtils.isSome(presenter.getSearchTerm()) ? searchCount : count);
	
		initTable();
		adminHomePanel.setVisible(true);
	}

	@SuppressWarnings("hiding")
	private class CourseClassActionsActionCell<CourseClassTO> extends ActionCell<CourseClassTO> {

		public CourseClassActionsActionCell(String message, Delegate<CourseClassTO> delegate) {
			super(message, delegate);
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, CourseClassTO value, NativeEvent event,
				ValueUpdater<CourseClassTO> valueUpdater) {
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

	private class CourseClassActionsHasCell implements HasCell<CourseClassTO, CourseClassTO> {
		private CourseClassActionsActionCell<CourseClassTO> cell;

		public CourseClassActionsHasCell(String text, Delegate<CourseClassTO> delegate) {
			final String actionName = text;
			cell = new CourseClassActionsActionCell<CourseClassTO>(text, delegate) {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, CourseClassTO object, SafeHtmlBuilder sb) {
					if(!"Excluir".equals(actionName) || object.getEnrollmentCount() == 0){
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
		public Cell<CourseClassTO> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<CourseClassTO, CourseClassTO> getFieldUpdater() {
			return null;
		}

		@Override
		public CourseClassTO getValue(CourseClassTO object) {
			return object;
		}
	}


	private class CourseClassLinkHasCell implements HasCell<CourseClassTO, CourseClassTO> {
		private CourseClassActionsActionCell<CourseClassTO> cell;

		public CourseClassLinkHasCell(String text, Delegate<CourseClassTO> delegate) {
			cell = new CourseClassActionsActionCell<CourseClassTO>(text, delegate) {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context, CourseClassTO courseClassTO, SafeHtmlBuilder sb) {
					SafeHtml html = SafeHtmlUtils.fromTrustedString(buildButtonHTML(text, courseClassTO));
					sb.append(html);
				}
				
				private String buildButtonHTML(String text, CourseClassTO courseClassTO) {
					Anchor anchor = new Anchor();
					if(CourseDetailsEntityType.COURSE.toString().equals(text)){
						anchor.setText(courseClassTO.getCourseVersionTO().getCourseTO().getCourse().getTitle());
					} else if(CourseDetailsEntityType.COURSE_VERSION.toString().equals(text)){
						anchor.setText(courseClassTO.getCourseVersionTO().getCourseVersion().getName());
					} else if(CourseDetailsEntityType.COURSE_CLASS.toString().equals(text)){
						anchor.setText(courseClassTO.getCourseClass().getName());
					}
					return anchor.toString();
				}
			};
		}

		@Override
		public Cell<CourseClassTO> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<CourseClassTO, CourseClassTO> getFieldUpdater() {
			return null;
		}

		@Override
		public CourseClassTO getValue(CourseClassTO object) {
			return object;
		}
	}

}