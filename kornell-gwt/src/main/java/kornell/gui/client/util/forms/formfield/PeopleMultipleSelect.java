package kornell.gui.client.util.forms.formfield;

import java.util.HashMap;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.Typeahead;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Person;
import kornell.core.to.PeopleTO;
import kornell.core.to.PersonTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.util.forms.FormHelper;

public class PeopleMultipleSelect extends Composite {
    private FormHelper formHelper = GWT.create(FormHelper.class);

    private KornellSession session;
    private TextBox search;
    private MultiWordSuggestOracle oracle;
    private ListBox multipleSelect;
    private Map<String, Person> oraclePeople;

    public PeopleMultipleSelect(KornellSession session) {
        this.session = session;
    }

    @Override
    public Widget asWidget() {
        return getPeopleMultipleSelectPanel();
    }

    private FlowPanel getPeopleMultipleSelectPanel() {
        FlowPanel courseClassAdminsPanel = new FlowPanel();
        courseClassAdminsPanel.addStyleName("fieldPanel");

        courseClassAdminsPanel.add(getTypeAheadPanel());
        FlowPanel multipleSelectPanel = getMultipleSelectPanel();
        courseClassAdminsPanel.add(multipleSelectPanel);

        return courseClassAdminsPanel;
    }

    private FlowPanel getMultipleSelectPanel() {
        FlowPanel multipleSelectPanel = new FlowPanel();
        multipleSelect = new ListBox(true);
        multipleSelect.addStyleName("selectField");

        multipleSelectPanel.add(multipleSelect);

        Button btnRemove = new Button("Remover");
        btnRemove.addStyleName("btnSelected btnStandard");

        btnRemove.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                while (multipleSelect.getSelectedIndex() != -1) {
                    multipleSelect.removeItem(multipleSelect.getSelectedIndex());
                }
            }
        });
        multipleSelectPanel.add(btnRemove);

        return multipleSelectPanel;
    }

    private FlowPanel getTypeAheadPanel() {
        FlowPanel typeaheadPanel = new FlowPanel();
        Typeahead typeahead = new Typeahead();
        search = new TextBox();
        search.addStyleName("textField");
        search.setPlaceholder("Digite o nome de usuário a adicionar");

        search.addKeyUpHandler(new KeyUpHandler() {
            String currentSearch = "";
            String previousSearch = "";

            Timer searchChangesTimer = new Timer() {
                @Override
                public void run() {
                    searchChanged(currentSearch);
                }
            };

            @Override
            public void onKeyUp(KeyUpEvent event) {
                currentSearch = search.getText();
                if (currentSearch.length() >= 1 && (!currentSearch.equals(previousSearch)
                        || event.getNativeKeyCode() == 86 || event.getNativeKeyCode() == 17)) {
                    searchChangesTimer.cancel();
                    searchChangesTimer.schedule(333);
                }
                previousSearch = currentSearch;
            }
        });

        typeahead.add(search);

        oracle = (MultiWordSuggestOracle) typeahead.getSuggestOracle();

        typeaheadPanel.add(typeahead);

        Button btnAdd = new Button("Adicionar");
        btnAdd.addStyleName("btnAction btnStandard");

        btnAdd.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addAdmin();
            }
        });

        typeaheadPanel.add(btnAdd);

        return typeaheadPanel;
    }

    private void searchChanged(final String searchStr) {
        session.people().findBySearchTerm(searchStr, session.getInstitution().getUUID(), new Callback<PeopleTO>() {
            @Override
            public void ok(PeopleTO to) {
                oraclePeople = new HashMap<String, Person>();
                oracle.clear();
                String username, oracleStr = "";
                Person person;
                int i = 0;
                for (PersonTO personTO : to.getPeopleTO()) {
                    person = personTO.getPerson();
                    username = personTO.getUsername();
                    if(username == null){
                        username = person.getEmail();
                    }
                    oracleStr = username
                            + (StringUtils.isSome(person.getFullName()) ? " (" + person.getFullName() + ")" : "");
                    oracle.add(oracleStr);
                    oraclePeople.put(username, person);
                    if (++i == 10) {
                        break;
                    }
                }
                if (to.getPeopleTO().size() == 1) {
                    Person p = oraclePeople.get(searchStr);
                    if (p != null) {
                        search.setText(oracleStr);
                    }
                }
            }
        });
    }

    private void addAdmin() {
        String item = search.getText().split(" \\(")[0];
        if (oraclePeople != null && oraclePeople.containsKey(item)) {
            if (!formHelper.isItemInListBox(search.getText(), multipleSelect)) {
                Person person = oraclePeople.get(item);
                multipleSelect.addItem(search.getText(), person.getUUID());
            }
            search.setText("");
        }
    }

    public ListBox getMultipleSelect() {
        return multipleSelect;
    }

    public void addItem(String item, String uuid) {
        multipleSelect.addItem(item, uuid);
    }
}