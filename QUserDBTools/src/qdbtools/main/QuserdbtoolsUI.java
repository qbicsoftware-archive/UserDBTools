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
package qdbtools.main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import ldap.LDAPConfig;
import logging.Log4j2Logger;
import model.Affiliation;
import model.Person;
import model.Tuple;
import views.AffiliationInput;
import views.AffiliationVIPTab;
import views.MultiAffiliationTab;
import views.TableOverview;
import views.PersonInput;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.UserGroup;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.themes.ValoTheme;

import config.ConfigurationManagerFactory;
import db.Config;
import db.DBManager;
import de.uni_tuebingen.qbic.main.LiferayAndVaadinUtils;

@SuppressWarnings("serial")
@Theme("quserdbtools")
public class QuserdbtoolsUI extends UI {

  @WebServlet(value = "/*", asyncSupported = true)
  @VaadinServletConfiguration(productionMode = false, ui = QuserdbtoolsUI.class,
      widgetset = "qdbtools.main.widgetset.QuserdbtoolsWidgetset")
  public static class Servlet extends VaadinServlet {
  }

  private logging.Logger logger = new Log4j2Logger(QuserdbtoolsUI.class);
  private DBManager dbControl;
  private Map<String, Integer> affiMap;
  private Map<String, Integer> personMap;

  private TabSheet options;

  private String user;
  private Config config;

  @Override
  protected void init(VaadinRequest request) {
    final VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    setContent(layout);

    options = new TabSheet();

    this.config = readConfig();
    logger.debug(config.getUserGrps().toString());
    logger.debug(config.getAdminGrps().toString());
    LDAPConfig ldapConfig = readLdapConfig();// TODO

    dbControl = new DBManager(config);

    user = "";
    if (LiferayAndVaadinUtils.isLiferayPortlet()) {
      logger.info("User DB Tools are running on Liferay and user is logged in.");
      user = LiferayAndVaadinUtils.getUser().getScreenName();
    }
    initTabs();

    layout.addComponent(options);
  }

  public static HorizontalLayout questionize(Component c, final String info, final String header) {
    final HorizontalLayout res = new HorizontalLayout();
    res.setSpacing(true);

    res.setVisible(c.isVisible());
    res.setCaption(c.getCaption());
    c.setCaption(null);
    res.addComponent(c);

    PopupView pv = new PopupView(new Content() {

      @Override
      public Component getPopupComponent() {
        Label l = new Label(info, ContentMode.HTML);
        l.setCaption(header);
        l.setIcon(FontAwesome.INFO);
        l.setWidth("350px");
        l.addStyleName("info");
        return new VerticalLayout(l);
      }

      @Override
      public String getMinimizedValueAsHTML() {
        return "[?]";
      }
    });
    pv.setHideOnMouseOut(false);

    res.addComponent(pv);

    return res;
  }

  private void initTabs() {
    boolean admin = isAdmin();
    options.removeAllComponents();
    if (!admin &&!isDevelopment() && !canUsePortlet()) {
      VerticalLayout rightsMissingTab = new VerticalLayout();
      rightsMissingTab.setCaption("User Database Input");
      Label info = new Label(
          "Your account does not have the necessary rights to add new people to our database.\n"
              + "If you think you should be able to do so, please contact us.",
          ContentMode.PREFORMATTED);
      rightsMissingTab.addComponent(info);
      options.addTab(rightsMissingTab, "Information");
      options.setSelectedTab(rightsMissingTab);
      options.setEnabled(false);

    } else {

      affiMap = dbControl.getAffiliationMap();
      personMap = dbControl.getPersonMap();

      List<String> affiliationRoles =
          dbControl.getPossibleEnumsForColumnsInTable("persons_organizations", "occupation");

      AffiliationInput addAffilTab = new AffiliationInput(dbControl.getInstituteNames(),
          dbControl.getPossibleEnumsForColumnsInTable("organizations", "faculty"), personMap);
      options.addTab(addAffilTab, "New Affiliation");

      PersonInput addUserTab =
          new PersonInput(dbControl.getPossibleEnumsForColumnsInTable("persons", "title"), affiMap,
              affiliationRoles);
      options.addTab(addUserTab, "New Person");

      List<Affiliation> affiTable = dbControl.getAffiliationTable();
      Map<Integer, Tuple> affiPeople = new HashMap<Integer, Tuple>();
      for (Affiliation a : affiTable) {
        int id = a.getID();
        affiPeople.put(id, new Tuple(a.getContactPerson(), a.getHeadName()));
      }

      AffiliationVIPTab vipTab = new AffiliationVIPTab(personMap, affiMap, affiPeople);
      options.addTab(vipTab, "Edit Affiliation VIPs");

      MultiAffiliationTab multiAffilTab =
          new MultiAffiliationTab(personMap, affiMap, affiliationRoles);
      options.addTab(multiAffilTab, "Additional Person-Affiliations");
      if (!admin) {
        options.getTab(2).setEnabled(false);
        options.getTab(3).setEnabled(false);
      }

      TableOverview overview = new TableOverview(dbControl.getPersonTable(), affiTable);
      options.addTab(overview, "Existing Entries");

      initPortletToDBFunctionality(addAffilTab, addUserTab, multiAffilTab, vipTab);
    }
  }

  boolean isDevelopment() {
    boolean devEnv = false;
    try {
      // TODO tests if this is somehow a local development environment
      // in which case user is granted admin rights. Change so it works for you.
      // Be careful that this is never true on production or better yet that logged out users can
      // not see the portlet page.
      String path = new File(".").getCanonicalPath();
      devEnv = path.toLowerCase().contains("eclipse");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return devEnv;
  }

  private boolean canUsePortlet() {
    try {
      for (UserGroup grp : LiferayAndVaadinUtils.getUser().getUserGroups()) {
        String name = grp.getName();
        if (config.getUserGrps().contains(name)) {
          logger.info("User can use portlet because he is part of "+name);
          return true;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("Could not fetch user groups. User won't be able to use portlet.");
    }
    return false;
  }

  private boolean isAdmin() {
    if (isDevelopment())
      return true;
    else {
      try {
        for (UserGroup grp : LiferayAndVaadinUtils.getUser().getUserGroups()) {
          String name = grp.getName();
          if (config.getAdminGrps().contains(name)) {
            logger.info("User has full rights because he is part of "+name);
            return true;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        logger.error("Could not fetch user groups. User won't be able to use portlet.");
      }
      return false;
    }
  }

  private void initPortletToDBFunctionality(final AffiliationInput addAffilTab,
      final PersonInput addUserTab, final MultiAffiliationTab multiAffilTab,
      final AffiliationVIPTab vipTab) {
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

    vipTab.getSetHeadAndContactButton().addClickListener(new Button.ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
        int affi = vipTab.getSelectedAffiTabID();
        int contact = vipTab.getNewContactID();
        int head = vipTab.getNewHeadID();
        if (affi > 0) {
          if (head > 0)
            dbControl.setAffiliationVIP(affi, head, "head");
          if (contact > 0)
            dbControl.setAffiliationVIP(affi, contact, "main_contact");
          vipTab.updateVIPs();
        }
      }
    });

    // addAffilTab.getInstituteField().addBlurListener(new BlurListener() {
    //
    // @Override
    // public void blur(BlurEvent event) {
    // Object val = addAffilTab.getInstituteField().getValue();
    // if (val != null) {
    // Affiliation orgInfo = dbControl.getOrganizationInfosFromInstitute(val.toString());
    // if (orgInfo != null)
    // addAffilTab.autoComplete(orgInfo);
    // }
    // }
    // });
    addAffilTab.getInstituteField().addValueChangeListener(new ValueChangeListener() {

      @Override
      public void valueChange(ValueChangeEvent event) {
        Object val = addAffilTab.getInstituteField().getValue();
        if (val != null) {
          Affiliation orgInfo = dbControl.getOrganizationInfosFromInstitute(val.toString());
          if (orgInfo != null)
            addAffilTab.autoComplete(orgInfo);
        }
      }
    });

    // addAffilTab.getOrganizationField().addBlurListener(new BlurListener() {
    //
    // @Override
    // public void blur(BlurEvent event) {
    // String val = addAffilTab.getOrganizationField().getValue();
    // if (!val.isEmpty()) {
    // Affiliation orgInfo = dbControl.getOrganizationInfosFromOrg(val);
    // if (orgInfo != null)
    // addAffilTab.autoComplete(orgInfo);
    // }
    // }
    // });

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
          if (dbControl.addOrUpdatePersonAffiliationConnections(
              personMap.get(multiAffilTab.getPersonBox().getValue()),
              multiAffilTab.getChangedAndNewConnections()))
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

  private Config readConfig() {
    config.ConfigurationManager c = ConfigurationManagerFactory.getInstance();

    return new Config(c.getMysqlHost(), c.getMysqlPort(), c.getMysqlDB(), c.getMysqlUser(),
        c.getMysqlPass(), c.getDBInputUserGrps(), c.getDBInputAdminGrps());
  }

  private LDAPConfig readLdapConfig() {
    config.ConfigurationManager c = ConfigurationManagerFactory.getInstance();

    return new LDAPConfig(c.getLdapHost(), c.getLdapBase(), c.getLdapUser(), c.getLdapPass());
  }
}
