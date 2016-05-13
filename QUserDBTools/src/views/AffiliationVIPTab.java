package views;

import java.util.Map;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.themes.ValoTheme;

import model.Person;
import model.Tuple;
import qdbtools.main.QuserdbtoolsUI;

public class AffiliationVIPTab extends FormLayout {

  private ComboBox affiTabOrgs;
  private ComboBox head;
  private ComboBox contact;
  private Button commitAffiTabButton;

  private Map<String, Integer> affiliationMap;
  private Map<String, Integer> personMap;
  private Map<Integer, Tuple> personAffiliationsInTable;


  public AffiliationVIPTab(Map<String, Integer> persons, Map<String, Integer> affiliations,
      Map<Integer, Tuple> affiliationPeople) {

    this.affiliationMap = affiliations;
    this.personMap = persons;
    this.personAffiliationsInTable = affiliationPeople;

    affiTabOrgs = new ComboBox("Affiliation", affiliations.keySet());
    affiTabOrgs.setStyleName(ValoTheme.COMBOBOX_SMALL);
    affiTabOrgs.setFilteringMode(FilteringMode.CONTAINS);
    affiTabOrgs.addValueChangeListener(new ValueChangeListener() {

      @Override
      public void valueChange(ValueChangeEvent event) {
        Object val = affiTabOrgs.getValue();
        contact.setVisible(val != null);
        head.setVisible(val != null);
        if (val != null) {
          String affiName = val.toString();
          int id = affiliations.get(affiName);
          Tuple names = personAffiliationsInTable.get(id);
          contact.setValue(names.getOne());
          head.setValue(names.getTwo());
        }
      }
    });

    head = new ComboBox("Head", persons.keySet());
    head.setStyleName(ValoTheme.COMBOBOX_SMALL);
    head.setFilteringMode(FilteringMode.CONTAINS);
    head.setVisible(false);
    contact = new ComboBox("Contact Person", persons.keySet());
    contact.setStyleName(ValoTheme.COMBOBOX_SMALL);
    contact.setFilteringMode(FilteringMode.CONTAINS);
    contact.setVisible(false);
    ValueChangeListener personListener = new ValueChangeListener() {

      @Override
      public void valueChange(ValueChangeEvent event) {
        boolean hasData = head.getValue() != null || contact.getValue() != null;
        commitAffiTabButton.setEnabled(hasData);
      }
    };
    head.addValueChangeListener(personListener);
    contact.addValueChangeListener(personListener);

    commitAffiTabButton = new Button("Save Contact");
    commitAffiTabButton.setEnabled(false);

    addComponent(affiTabOrgs);
    addComponent(head);
    addComponent(contact);
    commitAffiTabButton.setIcon(FontAwesome.SAVE);
    addComponent(QuserdbtoolsUI.questionize(commitAffiTabButton,
        "Add or change records to the selected people. "
            + "Existing people can only be replaced by a new selection, empty selections are ignored.",
        "Save Changes"));
  }


  public int getNewHeadID() {
    Object val = head.getValue();
    if (val != null) {
      String headName = val.toString();
      return personMap.get(headName);
    } else
      return -1;
  }

  public int getNewContactID() {
    Object val = contact.getValue();
    if (val != null) {
      String contactName = val.toString();
      return personMap.get(contactName);
    } else
      return -1;
  }

  public int getSelectedAffiTabID() {
    Object val = affiTabOrgs.getValue();
    if (val != null) {
      String affiName = val.toString();
      return affiliationMap.get(affiName);
    } else
      return -1;
  }

  public void updateVIPs() {
    int affi = getSelectedAffiTabID();
    Object ctct = contact.getValue();
    Object hd = head.getValue();
    if (ctct == null)
      ctct = personAffiliationsInTable.get(affi).getOne();
    if (hd == null)
      hd = personAffiliationsInTable.get(affi).getTwo();
    personAffiliationsInTable.put(affi, new Tuple(ctct, hd));
  }

  public Button getSetHeadAndContactButton() {
    return commitAffiTabButton;
  }
}
