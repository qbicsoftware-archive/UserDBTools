package views;

import helpers.Helpers;

import java.util.List;
import java.util.Map;

import logging.Log4j2Logger;
import model.Affiliation;

import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class AffiliationInput extends FormLayout {

  /**
   * 
   */
  private static final long serialVersionUID = 2499215556258217023L;
  private TextField groupName;
  private TextField acronym;
  private TextField organization;
  private TextField institute;
  private ComboBox faculty;
  private ComboBox contactPerson;
  private ComboBox head;
  private TextField street;
  private TextField zipCode;
  private TextField city;
  private TextField country;
  private TextField webpage;

  private Map<String, Integer> personMap;

  private Button commit;

  logging.Logger logger = new Log4j2Logger(AffiliationInput.class);

  public AffiliationInput(List<String> faculties, Map<String, Integer> personMap) {
    setMargin(true);

    this.personMap = personMap;

    groupName = new TextField("Group Name");
    groupName.setWidth("300px");
    addComponent(groupName);

    acronym = new TextField("Acronym");
    acronym.setWidth("300px");
    addComponent(acronym);

    organization = new TextField("Organization");
    organization.setWidth("300px");
    organization.setRequired(true);
    organization.setInputPrompt("...or university name");
    organization.setDescription("Organization or University Name");
    addComponent(organization);

    institute = new TextField("Institute");
    institute.setWidth("300px");
    // institute.setRequired(true);
    addComponent(institute);

    faculty = new ComboBox("Faculty", faculties);
    faculty.setRequired(true);
    faculty.setStyleName(ValoTheme.COMBOBOX_SMALL);
    faculty.setWidth("300px");
    addComponent(faculty);

    contactPerson = new ComboBox("Contact Person", personMap.keySet());
    contactPerson.setWidth("300px");
    contactPerson.setFilteringMode(FilteringMode.CONTAINS);
    contactPerson.setStyleName(ValoTheme.COMBOBOX_SMALL);
    // contactPerson.setRequired(true);
    addComponent(contactPerson);

    head = new ComboBox("Head", personMap.keySet());
    head.setWidth("300px");
    head.setFilteringMode(FilteringMode.CONTAINS);
    // head.setRequired(true);
    head.setStyleName(ValoTheme.COMBOBOX_SMALL);
    addComponent(head);

    street = new TextField("Street");
    street.setWidth("300px");
    street.setRequired(true);
    addComponent(street);

    zipCode = new TextField("Zip Code");
    zipCode.setWidth("300px");
    zipCode.setRequired(true);
    addComponent(zipCode);

    city = new TextField("City");
    city.setWidth("300px");
    city.setRequired(true);
    addComponent(city);

    country = new TextField("Country");
    country.setWidth("300px");
    country.setRequired(true);
    addComponent(country);

    webpage = new TextField("Webpage");
    webpage.setWidth("300px");
    webpage.addValidator(
        new RegexpValidator(Helpers.VALID_URL_REGEX, "This is not a valid web page format."));
    addComponent(webpage);

    commit = new Button("Register Affiliation");
    addComponent(commit);
  }

  public boolean isValid() {
    return groupName.isValid() && acronym.isValid() && organization.isValid() && institute.isValid()
        && faculty.isValid() && contactPerson.isValid() && head.isValid() && street.isValid()
        && zipCode.isValid() && country.isValid() && city.isValid() && webpage.isValid();
  }

  public Button getCommitButton() {
    return commit;
  }

  private int mapPersonToID(String person) {
    if (person == null) {
      logger.info("No optional person provided for new affiliation. Field will be empty.");
      return -1;
    } else
      return personMap.get(person);
  }

  public TextField getOrganizationField() {
    return organization;
  }

  public Affiliation getAffiliation() {
    String contact = null;
    if (contactPerson.getValue() != null)
      contact = contactPerson.getValue().toString();
    String headPerson = null;
    if (head.getValue() != null)
      headPerson = head.getValue().toString();

    int contactID = mapPersonToID(contact);
    int headID = mapPersonToID(headPerson);
    System.out.println(contactID);
    System.out.println(headID);

    String fac = faculty.getValue().toString();
    return new Affiliation(groupName.getValue(), acronym.getValue(), organization.getValue(),
        institute.getValue(), fac, contactID, headID, street.getValue(), zipCode.getValue(),
        city.getValue(), country.getValue(), webpage.getValue());
  }

  public void autoComplete(Affiliation affiliation) {
    organization.setValue(affiliation.getOrganization());
    institute.setValue(affiliation.getInstitute());
    faculty.setValue(affiliation.getFaculty());
    street.setValue(affiliation.getStreet());
    city.setValue(affiliation.getCity());
    zipCode.setValue(affiliation.getZipCode());
    country.setValue(affiliation.getCountry());
  }

  public TextField getInstituteField() {
    return institute;
  }

}
