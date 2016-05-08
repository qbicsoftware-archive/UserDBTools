package views;

import java.util.ArrayList;
import java.util.List;

import model.Affiliation;
import model.Person;
import model.RoleAt;

import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class TableOverview extends VerticalLayout {

  private Table persons;
  private Table affiliations;

  public TableOverview(List<Person> personData, List<Affiliation> affiliationData) {
    persons = new Table("Persons");
    persons.setStyleName(ValoTheme.TABLE_SMALL);
//    persons.addContainerProperty("ID", Integer.class, null);
    persons.addContainerProperty("User", String.class, null);
    persons.addContainerProperty("Title", String.class, null);
    persons.addContainerProperty("First", String.class, null);
    persons.addContainerProperty("Last", String.class, null);
    persons.addContainerProperty("eMail", String.class, null);
    persons.addContainerProperty("Phone", String.class, null);
    persons.addContainerProperty("Affiliation", String.class, null);
    persons.addContainerProperty("Role", String.class, null);
    addComponent(persons);

    affiliations = new Table("Affiliations");
    affiliations.setStyleName(ValoTheme.TABLE_SMALL);
//    affiliations.addContainerProperty("ID", Integer.class, null);
    affiliations.addContainerProperty("group", String.class, null);
    // affiliations.addContainerProperty("acronym", String.class, null);
    affiliations.addContainerProperty("organization", String.class, null);
    affiliations.addContainerProperty("institute", String.class, null);
    affiliations.addContainerProperty("faculty", String.class, null);
    // affiliations.addContainerProperty("contactPerson", String.class, null);
    affiliations.addContainerProperty("street", String.class, null);
    affiliations.addContainerProperty("zipCode", String.class, null);
    // affiliations.addContainerProperty("city", String.class, null);
    // affiliations.addContainerProperty("country", String.class, null);
    // affiliations.addContainerProperty("webpage", String.class, null);
    addComponent(affiliations);

    for (int i = 0; i < personData.size(); i++) {
      int itemId = i;
      List<Object> row = new ArrayList<Object>();
      Person p = personData.get(i);
//      row.add(p.getID());
      row.add(p.getUsername());
      row.add(p.getTitle());
      row.add(p.getFirst());
      row.add(p.getLast());
      row.add(p.geteMail());
      row.add(p.getPhone());
      RoleAt a = p.getOneAffiliationWithRole();
      row.add(a.getAffiliation());
      row.add(a.getRole());
      // String affs = StringUtils.join(p.getAffiliationInfos().values(), ",");
      // row.add(affs);
      persons.addItem(row.toArray(new Object[row.size()]), itemId);
    }
    persons.setPageLength(persons.size());

    for (int i = 0; i < affiliationData.size(); i++) {
      int itemId = i;
      List<Object> row = new ArrayList<Object>();
      Affiliation a = affiliationData.get(i);
//      row.add(a.getID());
      row.add(a.getGroupName());
      // row.add(a.getAcronym());
      row.add(a.getOrganization());
      row.add(a.getInstitute());
      row.add(a.getFaculty());
      // row.add(a.getContactPerson());
      row.add(a.getStreet());
      row.add(a.getZipCode());
      // row.add(a.getCity());
      // row.add(a.getCountry());
      // row.add(a.getWebpage());
      affiliations.addItem(row.toArray(new Object[row.size()]), itemId);
    }
    affiliations.setPageLength(affiliationData.size());
  }
}
