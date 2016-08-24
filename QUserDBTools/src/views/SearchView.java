/*******************************************************************************
 * QBiC User DB Tools enables users to add people and affiliations to our mysql user database.
 * Copyright (C) 2016 Andreas Friedrich
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package views;

import java.util.ArrayList;
import java.util.List;

import model.Affiliation;
import model.Person;
import model.RoleAt;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class SearchView extends VerticalLayout {

  private TextField searchPerson;
  private TextField searchAffiliation;
  private Button search;
  private Table persons;
  private Table affiliations;

  public SearchView() {
    setSpacing(true);
    setMargin(true);

    setCaption("Search for entries in the Database");
    HorizontalLayout searchFields = new HorizontalLayout();
    searchFields.setSpacing(true);
    searchPerson = new TextField("Search for Person");
    searchAffiliation = new TextField("Search for Affiliation");
    searchFields.addComponent(searchPerson);
    searchFields.addComponent(searchAffiliation);
    search = new Button("Search");
    addComponent(searchFields);
    addComponent(search);

    persons = new Table("People");
    persons.setPageLength(1);
    persons.setStyleName(ValoTheme.TABLE_SMALL);
    // persons.addContainerProperty("ID", Integer.class, null);
    persons.addContainerProperty("User", String.class, null);
    persons.addContainerProperty("Title", String.class, null);
    persons.addContainerProperty("First", String.class, null);
    persons.addContainerProperty("Last", String.class, null);
    persons.addContainerProperty("eMail", String.class, null);
    persons.addContainerProperty("Phone", String.class, null);
    persons.addContainerProperty("(1st) Affiliation", String.class, null);
    persons.addContainerProperty("Role", String.class, null);
    addComponent(persons);

    affiliations = new Table("Affiliations");
    affiliations.setStyleName(ValoTheme.TABLE_SMALL);
    // affiliations.addContainerProperty("ID", Integer.class, null);
    affiliations.addContainerProperty("group", String.class, null);
    // affiliations.addContainerProperty("acronym", String.class, null);
    affiliations.addContainerProperty("organization", String.class, null);
    affiliations.addContainerProperty("institute", String.class, null);
    affiliations.addContainerProperty("faculty", String.class, null);
    // affiliations.addContainerProperty("contactPerson", String.class, null);
    affiliations.addContainerProperty("street", String.class, null);
    affiliations.addContainerProperty("zipCode", String.class, null);
    affiliations.setPageLength(1);
    addComponent(affiliations);
  }

  public Button getSearchButton() {
    return search;
  }

  public TextField getPersonSearchField() {
    return searchPerson;
  }

  public TextField getAffiliationSearchField() {
    return searchAffiliation;
  }

  public void setPersons(List<Person> foundPersons) {
    persons.removeAllItems();
    persons.setPageLength(foundPersons.size());
    for (int i = 0; i < foundPersons.size(); i++) {
      int itemId = i;
      List<Object> row = new ArrayList<Object>();
      Person p = foundPersons.get(i);
      row.add(p.getUsername());
      row.add(p.getTitle());
      row.add(p.getFirst());
      row.add(p.getLast());
      row.add(p.geteMail());
      row.add(p.getPhone());
      RoleAt a = p.getOneAffiliationWithRole();
      row.add(a.getAffiliation());
      row.add(a.getRole());
      persons.addItem(row.toArray(new Object[row.size()]), itemId);
    }
  }

  public void setAffiliations(List<Affiliation> foundAffiliations) {
    affiliations.removeAllItems();
    affiliations.setPageLength(foundAffiliations.size());
    for (int i = 0; i < foundAffiliations.size(); i++) {
      int itemId = i;
      List<Object> row = new ArrayList<Object>();
      Affiliation a = foundAffiliations.get(i);
      row.add(a.getGroupName());
      row.add(a.getOrganization());
      row.add(a.getInstitute());
      row.add(a.getFaculty());
      row.add(a.getStreet());
      row.add(a.getZipCode());
      System.out.println(a.getZipCode());
      affiliations.addItem(row.toArray(new Object[row.size()]), itemId);
    }
  }
}
