/*******************************************************************************
 * QBiC User DB Tools enables users to add people and affiliations to our mysql user database.
 * Copyright (C) 2016  Andreas Friedrich
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package views;

import java.util.List;
import java.util.Map;

import model.Person;
import model.Styles;
import qdbtools.main.QuserdbtoolsUI;
import helpers.Helpers;

import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class PersonInput extends FormLayout {

  /**
   * 
   */
  private static final long serialVersionUID = 2657654653139639151L;
  private Button commit;
  private TextField userName;
  private ComboBox title;
  private TextField first;
  private TextField last;
  private TextField eMail;
  private TextField phone;
  private ComboBox affiliation;
  private ComboBox role;

  private Map<String, Integer> affiliationMap;

  public PersonInput(List<String> titles, Map<String, Integer> affiliations, List<String> roles) {
    setMargin(true);

    affiliationMap = affiliations;

    userName = new TextField("Username");
    // userName.setRequired(true);
    userName.addValidator(
        new RegexpValidator(Helpers.VALID_USERNAME_REGEX, "Please input a valid username."));
    addComponent(Styles.questionize(userName,
        "University Tübingen user name or user name provided by QBiC. If left empty a dummy user name is chosen "
        + "which cannot be used to log in until a real name is added. Person information can still be added to "
        + "projects or experiments in that case.",
        "User Name"));

    title = new ComboBox("Title", titles);
    title.setRequired(true);
    title.setStyleName(ValoTheme.COMBOBOX_SMALL);
    title.setNullSelectionAllowed(false);
    addComponent(title);

    first = new TextField("First Name");
    first.setRequired(true);
    first.addValidator(new RegexpValidator(Helpers.VALID_NAME_REGEX, "Please input a valid name."));
    addComponent(first);

    last = new TextField("Last Name");
    last.setRequired(true);
    last.addValidator(new RegexpValidator(Helpers.VALID_NAME_REGEX, "Please input a valid name."));
    addComponent(last);

    eMail = new TextField("E-Mail");
    eMail.setRequired(true);
    eMail.addValidator(new RegexpValidator(Helpers.VALID_EMAIL_ADDRESS_REGEX,
        "Please input a valid e-mail address."));
    addComponent(eMail);

    phone = new TextField("Phone");
    addComponent(phone);

    affiliation = new ComboBox("Affiliation", affiliations.keySet());
    affiliation.setNullSelectionAllowed(false);
    affiliation.setRequired(true);
    affiliation.setFilteringMode(FilteringMode.CONTAINS);
    affiliation.setStyleName(ValoTheme.COMBOBOX_SMALL);
    addComponent(Styles.questionize(affiliation, "Work group or organization this person is part of. If it does not exist in the system "
        + "a \"New Affiliation\" has to be created first. Additional Affiliations and roles can be set in the next Tab.", "Affiliation"));

    role = new ComboBox("Role", roles);
    role.setRequired(true);
    role.setStyleName(ValoTheme.COMBOBOX_SMALL);
    role.setNullSelectionAllowed(false);
    addComponent(role);

    commit = new Button("Register User");
    addComponent(commit);
  }

  public boolean isValid() {
    return userName.isValid() && title.isValid() && first.isValid() && last.isValid() && title.isValid()
        && eMail.isValid() && phone.isValid() && affiliation.isValid() && role.isValid();
  }

  public Button getCommitButton() {
    return commit;
  }

  public Person getPerson() {
    String ttl = null;
    if (title.getValue() != null)
      ttl = title.getValue().toString();
    String affRole = null;
    if (role.getValue() != null)
      affRole = role.getValue().toString();
    String affi = (String) affiliation.getValue();
    return new Person(userName.getValue(), ttl, first.getValue(), last.getValue(), eMail.getValue(),
        phone.getValue(), affiliationMap.get(affi), affi, affRole);
  }
}
