package views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Person;
import model.PersonAffiliationConnectionInfo;
import model.RoleAt;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

public class MultiAffiliationTab extends FormLayout {

  private ComboBox organization;
  private ComboBox person;
  private Button addToTable;

  private Table table;

  private Button setContactPerson;
  private Button commit;

  private Map<String, Integer> affiliationMap;
  private Map<String, Integer> personMap;
  private List<String> availableRoles;
  private Map<Integer, Person> personAffiliationsInTable;

  public MultiAffiliationTab(Map<String, Integer> persons, Map<String, Integer> affiliations,
      List<String> roles) {
    setMargin(true);
    this.affiliationMap = affiliations;
    this.personMap = persons;
    this.availableRoles = roles;

    person = new ComboBox("Person", persons.keySet());
    person.setStyleName(ValoTheme.COMBOBOX_SMALL);
    person.setNullSelectionAllowed(false);
    addComponent(person);
    organization = new ComboBox("Organization", affiliations.keySet());
    organization.setNullSelectionAllowed(false);
    organization.setStyleName(ValoTheme.COMBOBOX_SMALL);
    addComponent(organization);

    addToTable = new Button("Add to Preview");
    addComponent(addToTable);
    addToTable.setEnabled(false);

    table = new Table();
    table.setWidthUndefined();
    table.addContainerProperty("Title", String.class, null);
    table.addContainerProperty("First Name", String.class, null);
    table.addContainerProperty("Family Name", String.class, null);
    table.addContainerProperty("Affiliation", String.class, null);
    table.setColumnWidth("Affiliation", 250);
    table.addContainerProperty("Role", ComboBox.class, null);
    // table.addContainerProperty("Main Contact", CheckBox.class, null);
    table.addContainerProperty("Remove", Button.class, null);
    table.setImmediate(true);
    table.setVisible(false);
    addComponent(table);

    commit = new Button("Save Changes");
    addComponent(commit);
  }

  public ComboBox getPersonBox() {
    return person;
  }

  public ComboBox getOrganizationBox() {
    return organization;
  }

  public Button getCommitButton() {
    return commit;
  }

  public List<PersonAffiliationConnectionInfo> getNewConnections() {
    // String ttl = title.getValue().toString();
    // if (ttl == null)
    // ttl = "";
    // String affRole = role.getValue().toString();
    // if (affRole == null)
    // affRole = "";
    return null;
    // return new Person(userName.getValue(), ttl, first.getValue(), last.getValue(),
    // eMail.getValue(), phone.getValue(), affiliationMap.get((String) affiliation.getValue()),
    // affRole);
  }

  public void reactToPersonSelection(List<Person> personsWithAffiliations) {
    table.removeAllItems();
    personAffiliationsInTable = new HashMap<Integer, Person>();

    addDataToTable(personsWithAffiliations);

    table.setVisible(true);
  }

  public void addDataToTable(List<Person> personsWithAffiliations) {
    for (Person p : personsWithAffiliations) {
      String title = p.getTitle();
      String first = p.getFirst();
      String last = p.getLast();
      Map<Integer, RoleAt> map = p.getAffiliationInfos();
      for (Integer i : p.getAffiliationInfos().keySet()) {
        personAffiliationsInTable.put(i, p);
        String affiliation = map.get(i).getAffiliation();
        String role = map.get(i).getRole();
        List<Object> row = new ArrayList<Object>();
        row.add(title);
        row.add(first);
        row.add(last);
        row.add(affiliation);
        ComboBox roleInput = new ComboBox("", availableRoles);
        roleInput.setStyleName(ValoTheme.COMBOBOX_SMALL);
        roleInput.setValue(role);
        row.add(roleInput);
        Button delete = new Button("Remove");
        row.add(delete);
        delete.setData(i);
        delete.addClickListener(new Button.ClickListener() {
          /**
           * 
           */
          private static final long serialVersionUID = 5414693256990177472L;

          @Override
          public void buttonClick(ClickEvent event) {
            Button b = event.getButton();
            Integer iid = (Integer) b.getData();
            table.removeItem(iid);
            table.setPageLength(table.size());
            personAffiliationsInTable.remove(iid);
          }
        });
        table.addItem(row.toArray(), i);
      }
    }
    table.setPageLength(table.size());
  }

  public boolean isValid() {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean newAffiliationPossible() {
    if (organization.getValue() == null)
      return false;
    int selectedAffi = affiliationMap.get(organization.getValue());
    Collection<Person> peopleAffisInTable = personAffiliationsInTable.values();
    boolean in = false;
    for (Person p : peopleAffisInTable)
      in |= p.getAffiliationInfos().containsKey(selectedAffi);
    return organization.getValue() != null && !in;
  }

  public Button getAddButton() {
    return addToTable;
  }
}
