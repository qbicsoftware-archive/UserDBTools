package qdbtools.main;

import java.awt.TextField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import ldap.LDAPConfig;
import model.Affiliation;
import model.Person;

import views.AffiliationInput;
import views.MultiAffiliationTab;
import views.TableOverview;
import views.UserInput;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import config.ConfigurationManagerFactory;

import db.DBConfig;
import db.DBManager;

@SuppressWarnings("serial")
@Theme("quserdbtools")
public class QuserdbtoolsUI extends UI {

  @WebServlet(value = "/*", asyncSupported = true)
  @VaadinServletConfiguration(productionMode = false, ui = QuserdbtoolsUI.class,
      widgetset = "qdbtools.main.widgetset.QuserdbtoolsWidgetset")
  public static class Servlet extends VaadinServlet {
  }

  DBManager dbControl;
  Map<String, Integer> affiMap;
  Map<String, Integer> personMap;

  TabSheet options;

  @Override
  protected void init(VaadinRequest request) {
    final VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    setContent(layout);

    options = new TabSheet();

    DBConfig dbConfig = readDBConfig();
    LDAPConfig ldapConfig = readLdapConfig();// TODO

    dbControl = new DBManager(dbConfig);

    initTabs();

    layout.addComponent(options);

  }

  private void initTabs() {
    options.removeAllComponents();

    affiMap = dbControl.getAffiliationMap();
    personMap = dbControl.getPersonMap();

    List<String> affiliationRoles =
        dbControl.getPossibleEnumsForColumnsInTable("persons_organizations", "occupation");

    AffiliationInput addAffilTab = new AffiliationInput(
        dbControl.getPossibleEnumsForColumnsInTable("organizations", "faculty"), personMap);
    options.addTab(addAffilTab, "New Affiliations");

    UserInput addUserTab = new UserInput(
        dbControl.getPossibleEnumsForColumnsInTable("persons", "title"), affiMap, affiliationRoles);
    options.addTab(addUserTab, "New Users");

    MultiAffiliationTab multiAffilTab =
        new MultiAffiliationTab(personMap, affiMap, affiliationRoles);
    options.addTab(multiAffilTab, "Additional user-affiliations");
    // options.getTab(2).setEnabled(false);// TODO

    TableOverview overview =
        new TableOverview(dbControl.getPersonTable(), dbControl.getAffiliationTable());
    options.addTab(overview, "Existing Entries");

    initPortletToDBFunctionality(addAffilTab, addUserTab, multiAffilTab);
  }

  private void initPortletToDBFunctionality(final AffiliationInput addAffilTab,
      final UserInput addUserTab, final MultiAffiliationTab multiAffilTab) {
    addAffilTab.getCommitButton().addClickListener(new Button.ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
        if (addAffilTab.isValid()) {
          if (dbControl.addNewAffiliation(addAffilTab.getAffiliation()))
            successfulCommit();
          else
            commitError("There has been an error.");
        } else
          inputError();
      }
    });
   
    addAffilTab.getInstituteField().addBlurListener(new BlurListener() {

      @Override
      public void blur(BlurEvent event) {
        String val = addAffilTab.getInstituteField().getValue();
        if (!val.isEmpty()) {
          Affiliation orgInfo = dbControl.getOrganizationInfosFromInstitute(val);
          if (orgInfo != null)
            addAffilTab.autoComplete(orgInfo);
        }
      }
    });

    addAffilTab.getOrganizationField().addBlurListener(new BlurListener() {

      @Override
      public void blur(BlurEvent event) {
        String val = addAffilTab.getOrganizationField().getValue();
        if (!val.isEmpty()) {
          Affiliation orgInfo = dbControl.getOrganizationInfosFromOrg(val);
          if (orgInfo != null)
            addAffilTab.autoComplete(orgInfo);
        }
      }
    });

    addUserTab.getCommitButton().addClickListener(new Button.ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
        if (addUserTab.isValid()) {
          if (dbControl.addNewPerson(addUserTab.getPerson()))
            successfulCommit();
          else
            commitError("There has been an error.");
        } else
          inputError();
      }
    });

    multiAffilTab.getCommitButton().addClickListener(new Button.ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
        if (multiAffilTab.isValid()) {
          if (dbControl.addNewPersonAffiliationConnections(multiAffilTab.getNewConnections()))
            successfulCommit();
          else
            commitError("There has been an error.");
        } else
          inputError();
      }
    });

    multiAffilTab.getAddButton().addClickListener(new Button.ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
        String personName = multiAffilTab.getPersonBox().getValue().toString();
        Person p = dbControl.getPerson(personMap.get(personName));

        String affiName = multiAffilTab.getOrganizationBox().getValue().toString();
        Person newP = new Person(p.getUsername(), p.getTitle(), p.getFirst(), p.getLast(),
            p.geteMail(), p.getPhone(), affiMap.get(affiName), affiName, "");
        multiAffilTab.addDataToTable(new ArrayList<Person>(Arrays.asList(newP)));
        multiAffilTab.getAddButton().setEnabled(false);
      }
    });

    ValueChangeListener multiAffiPersonListener = new ValueChangeListener() {

      @Override
      public void valueChange(ValueChangeEvent event) {
        if (multiAffilTab.getPersonBox().getValue() != null) {
          String personName = multiAffilTab.getPersonBox().getValue().toString();
          multiAffilTab.reactToPersonSelection(
              dbControl.getPersonWithAffiliations(personMap.get(personName)));
          multiAffilTab.getAddButton().setEnabled(multiAffilTab.newAffiliationPossible());
        }
      }
    };
    multiAffilTab.getPersonBox().addValueChangeListener(multiAffiPersonListener);

    ValueChangeListener multiAffiListener = new ValueChangeListener() {

      @Override
      public void valueChange(ValueChangeEvent event) {
        if (multiAffilTab.getPersonBox().getValue() != null) {
          multiAffilTab.getAddButton().setEnabled(multiAffilTab.newAffiliationPossible());
        }
      }
    };
    multiAffilTab.getOrganizationBox().addValueChangeListener(multiAffiListener);
  }

  private void successfulCommit() {
    Notification n = new Notification("Data has been successfully added to the database!");
    n.setStyleName(ValoTheme.NOTIFICATION_CLOSABLE);
    n.setDelayMsec(-1);
    n.show(UI.getCurrent().getPage());
    // wait a bit and reload tabs
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    initTabs();
  }

  private void inputError() {
    Notification n = new Notification("Please fill in all required fields correctly.");
    n.setStyleName(ValoTheme.NOTIFICATION_CLOSABLE);
    n.setDelayMsec(-1);
    n.show(UI.getCurrent().getPage());
  }

  private void commitError(String reason) {
    Notification n = new Notification(reason);
    n.setStyleName(ValoTheme.NOTIFICATION_CLOSABLE);
    n.setDelayMsec(-1);
    n.show(UI.getCurrent().getPage());
  }

  private DBConfig readDBConfig() {
    config.ConfigurationManager c = ConfigurationManagerFactory.getInstance();

    return new DBConfig(c.getMysqlHost(), c.getMysqlPort(), c.getMysqlDB(), c.getMysqlUser(),
        c.getMysqlPass());
  }

  private LDAPConfig readLdapConfig() {
    config.ConfigurationManager c = ConfigurationManagerFactory.getInstance();

    return new LDAPConfig(c.getLdapHost(), c.getLdapBase(), c.getLdapUser(), c.getLdapPass());
  }
}
