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
package db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import logging.Log4j2Logger;
import model.Affiliation;
import model.Person;
import model.PersonAffiliationConnectionInfo;
import model.RoleAt;

public class DBManager {
  private Config config;

  logging.Logger logger = new Log4j2Logger(DBManager.class);

  public DBManager(Config config) {
    this.config = config;
  }

  private void logout(Connection conn) {
    try {
      conn.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void printAffiliations() {
    String sql = "SELECT * FROM organizations";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        System.out.println(Integer.toString(rs.getInt(1)) + " " + rs.getString(2) + " "
            + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5) + " "
            + rs.getString(6) + " " + rs.getString(7) + " " + rs.getString(8) + " "
            + rs.getString(9) + " " + rs.getString(10) + " " + rs.getString(11));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
  }

  public String getProjectName(String projectIdentifier) {
    String sql = "SELECT short_title from projects WHERE openbis_project_identifier = ?";
    String res = "";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      statement.setString(1, projectIdentifier);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        res = rs.getString(1);
      }
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }

  private void removePersonFromAllProjects(int userID) {
    logger.info("Trying to remove all project associations of user with ID " + userID);
    String sql = "DELETE FROM projects_persons WHERE person_id = ?";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      statement.setInt(1, userID);
      statement.execute();
      logger.info("Successful.");
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
  }

  private void removePersonFromAllAffiliationRoles(int userID) {
    logger.info("Trying to remove all affiliation associations of user with ID " + userID);
    String sql = "DELETE FROM persons_organizations WHERE person_id = ?";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      statement.setInt(1, userID);
      statement.execute();
      logger.info("Successful.");
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    } finally {
      endQuery(null, statement);
    }
    logger.info("Trying to remove user from special affiliation roles");
    sql = "UPDATE organizations SET head=NULL WHERE head = ?";
    try {
      statement = conn.prepareStatement(sql);
      statement.setInt(1, userID);
      statement.execute();
      logger.info("Successful for head.");
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    } finally {
      endQuery(null, statement);
    }

    sql = "UPDATE organizations SET main_contact=NULL WHERE main_contact = ?";
    try {
      statement = conn.prepareStatement(sql);
      statement.setInt(1, userID);
      statement.execute();
      logger.info("Successful for main contact.");
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
  }

  private Connection login() {
    String DB_URL = "jdbc:mariadb://" + config.getHostname() + ":" + config.getPort() + "/"
        + config.getSql_database();

    Connection conn = null;

    try {
      Class.forName("org.mariadb.jdbc.Driver");
      conn = DriverManager.getConnection(DB_URL, config.getUsername(), config.getPassword());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return conn;
  }

  public void addOrChangeSecondaryNameForProject(String projectCode, String secondaryName) {
    logger
        .info("Adding/Updating secondary name of projects " + projectCode + " to " + secondaryName);
    String sql = "UPDATE projects SET secondary_name=? WHERE tutorial_id=?";
    // String sql = "INSERT INTO projects (pi_id, project_code) VALUES(?, ?)";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      statement.setString(2, projectCode);
      statement.setString(3, secondaryName);
      statement.execute();
      logger.info("Successful.");
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
  }

  public List<String> getPossibleEnumsForColumnsInTable(String table, String column) {
    String sql = "desc " + table + " " + column;
    Connection conn = login();
    List<String> res = new ArrayList<String>();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        for (String s : rs.getString(2).replace("enum('", "").replace("')", "").split("','"))
          res.add(s);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }

  public List<String> getPossibleSetOptionsForColumnsInTable(String table, String column) {
    String sql = "desc " + table + " " + column;
    Connection conn = login();
    List<String> res = new ArrayList<String>();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        for (String s : rs.getString(2).replace("set('", "").replace("')", "").split("','"))
          res.add(s);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }

  // public void addProjectForPrincipalInvestigator(int pi_id, String projectCode) {
  // logger.info("Trying to add project " + projectCode + " to the principal investigator DB");
  // String sql = "INSERT INTO projects (pi_id, project_code) VALUES(?, ?)";
  // Connection conn = login();
  // try (PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
  // {
  // statement.setInt(1, pi_id);
  // statement.setString(2, projectCode);
  // statement.execute();
  // logger.info("Successful.");
  // } catch (SQLException e) {
  // logger.error("SQL operation unsuccessful: " + e.getMessage());
  // e.printStackTrace();
  // } finally {
  // endQuery(conn, statement);
  // }
  // }

  // public String getInvestigatorForProject(String projectCode) {
  // String id_query = "SELECT pi_id FROM projects WHERE project_code = " + projectCode;
  // String id = "";
  // Connection conn = login();
  // try (PreparedStatement statement = conn.prepareStatement(id_query)) {
  // ResultSet rs = statement.executeQuery();
  // while (rs.next()) {
  // id = Integer.toString(rs.getInt("pi_id"));
  // }
  // } catch (SQLException e) {
  // e.printStackTrace();
  // }
  //
  // String sql = "SELECT first_name, last_name FROM project_investigators WHERE pi_id = " + id;
  // String fullName = "";
  // try (PreparedStatement statement = conn.prepareStatement(sql)) {
  // ResultSet rs = statement.executeQuery();
  // while (rs.next()) {
  // String first = rs.getString("first_name");
  // String last = rs.getString("last_name");
  // fullName = first + " " + last;
  // }
  // } catch (SQLException e) {
  // e.printStackTrace();
  // } finally {
  // endQuery(conn, statement);
  // }
  // return fullName;
  // }

  public boolean isProjectInDB(String projectIdentifier) {
    logger.info("Looking for project " + projectIdentifier + " in the DB");
    String sql = "SELECT * from projects WHERE openbis_project_identifier = ?";
    boolean res = false;
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      statement.setString(1, projectIdentifier);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        res = true;
        logger.info("project found!");
      }
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }

  public int addProjectToDB(String projectIdentifier, String projectName) {
    if (!isProjectInDB(projectIdentifier)) {
      logger.info("Trying to add project " + projectIdentifier + " to the person DB");
      String sql = "INSERT INTO projects (openbis_project_identifier, short_title) VALUES(?, ?)";
      Connection conn = login();
      PreparedStatement statement = null;
      try {
        statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, projectIdentifier);
        statement.setString(2, projectName);
        statement.execute();
        ResultSet rs = statement.getGeneratedKeys();
        if (rs.next()) {
          logout(conn);
          logger.info("Successful.");
          return rs.getInt(1);
        }
      } catch (SQLException e) {
        logger.error("SQL operation unsuccessful: " + e.getMessage());
        e.printStackTrace();
      } finally {
        endQuery(conn, statement);
      }
      return -1;
    }
    return -1;
  }

  public boolean hasPersonRoleInProject(int personID, int projectID, String role) {
    logger.info("Checking if person already has this role in the project.");
    String sql =
        "SELECT * from projects_persons WHERE person_id = ? AND project_id = ? and project_role = ?";
    boolean res = false;
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      statement.setInt(1, personID);
      statement.setInt(2, projectID);
      statement.setString(3, role);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        res = true;
        logger.info("person already has this role!");
      }
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }

  public void addPersonToProject(int projectID, int personID, String role) {
    if (!hasPersonRoleInProject(personID, projectID, role)) {
      logger.info("Trying to add person with role " + role + " to a project.");
      String sql =
          "INSERT INTO projects_persons (project_id, person_id, project_role) VALUES(?, ?, ?)";
      Connection conn = login();
      PreparedStatement statement = null;
      try {
        statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, projectID);
        statement.setInt(2, personID);
        statement.setString(3, role);
        statement.execute();
        logger.info("Successful.");
      } catch (SQLException e) {
        logger.error("SQL operation unsuccessful: " + e.getMessage());
        e.printStackTrace();
      } finally {
        endQuery(conn, statement);
      }
    }
  }

  public void printPeople() {
    String sql = "SELECT * FROM project_investigators";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        System.out.println(Integer.toString(rs.getInt(1)) + " " + rs.getString(2) + " "
            + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5) + " "
            + rs.getString(6) + " " + rs.getString(7) + " " + rs.getString(8) + " "
            + rs.getString(9) + " " + rs.getString(10) + " " + rs.getString(11));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
  }

  public void printProjects() {
    String sql = "SELECT pi_id, project_code FROM projects";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        int pi_id = rs.getInt("pi_id");
        String first = rs.getString("project_code");
        System.out.println(pi_id + first);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
  }

  public Map<String, Integer> getAffiliationMap() {
    Map<String, Integer> res = new HashMap<String, Integer>();
    String sql = "SELECT * FROM organizations";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        int id = rs.getInt("id");
        String groupName = rs.getString("group_name");
        String acronym = rs.getString("group_acronym");
        String organization = rs.getString("institute");
        if (acronym == null)
          res.put(groupName + " - " + organization, id);
        else
          res.put(groupName + " (" + acronym + ") - " + organization, id);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }

  public boolean addNewAffiliation(Affiliation affiliation) {
    logger.info("Trying to add new affiliation to the DB");
    // TODO empty values are inserted as empty strings, ok?
    boolean res = false;
    String insert =
        "INSERT INTO organizations (group_name,group_acronym,umbrella_organization,institute,faculty,street,zip_code,"
            + "city,country,webpage";
    String values = "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
    if (affiliation.getContactPersonID() > 0) {
      insert += ",contact";
      values += ", ?";
    }
    if (affiliation.getHeadID() > 0) {
      insert += ",head";
      values += ", ?";
    }
    String sql = insert + ") " + values + ")";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, affiliation.getGroupName());
      statement.setString(2, affiliation.getAcronym());
      statement.setString(3, affiliation.getOrganization());
      statement.setString(4, affiliation.getInstitute());
      statement.setString(5, affiliation.getFaculty());
      statement.setString(6, affiliation.getStreet());
      statement.setString(7, affiliation.getZipCode());
      statement.setString(8, affiliation.getCity());
      statement.setString(9, affiliation.getCountry());
      statement.setString(10, affiliation.getWebpage());
      int offset = 0;
      if (affiliation.getContactPersonID() > 0) {
        statement.setInt(11, affiliation.getContactPersonID());
        offset++;
      }
      if (affiliation.getHeadID() > 0)
        statement.setInt(11 + offset, affiliation.getHeadID());
      statement.execute();
      logger.info("Successful.");
      res = true;
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }

  private String getFreeDummyUserName() {
    List<Person> existing = getPeopleWithDummyUserNames();
    int last = 0;
    for (Person p : existing) {
      String user = p.getUsername();
      try {
        int num = Integer.parseInt(user.substring(4));
        last = Math.max(last, num);
      } catch (NumberFormatException e) {
        logger.warn("Could not parse number from dummy username " + user);
      }
    }
    return "todo" + Integer.toString(last + 1);

  }

  public List<Person> getPeopleWithDummyUserNames() {
    String dummy = "todo";
    List<Person> existing = new ArrayList<Person>();
    String sql = "SELECT * FROM persons WHERE username LIKE ?";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      statement.setString(1, dummy + "%");
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        String username = rs.getString("username");
        String title = rs.getString("title");
        String first = rs.getString("first_name");
        String last = rs.getString("family_name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        existing.add(new Person(username, title, first, last, email, phone, -1, "", ""));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return existing;
  }

  public boolean addNewPerson(Person person) {
    logger.info("Trying to add new person to the DB");
    // TODO empty values are inserted as empty strings, ok?
    boolean res = false;
    String sql =
        "INSERT INTO persons (username,title,first_name,family_name,email,phone,active) VALUES(?, ?, ?, ?, ?, ?, ?)";
    Connection conn = login();
    int person_id = -1;
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      String user = person.getUsername();
      if (user == null || user.isEmpty())
        user = getFreeDummyUserName();
      statement.setString(1, user);
      statement.setString(2, person.getTitle());
      statement.setString(3, person.getFirst());
      statement.setString(4, person.getLast());
      statement.setString(5, person.geteMail());
      statement.setString(6, person.getPhone());
      statement.setBoolean(7, true);
      statement.execute();
      ResultSet answer = statement.getGeneratedKeys();
      answer.next();
      person_id = answer.getInt(1);
      res = true;
      logger.info("Successfully added person to db.");
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    } finally {
      if (statement != null)
        endQuery(null, statement);
    }
    if (person_id > 0) {
      Map<Integer, RoleAt> affiliationInfos = person.getAffiliationInfos();
      for (int affiliation_id : affiliationInfos.keySet()) {
        sql =
            "INSERT INTO persons_organizations (person_id, organization_id, occupation) VALUES(?, ?, ?)";
        try {
          statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          statement.setInt(1, person_id);
          statement.setInt(2, affiliation_id);
          statement.setString(3, affiliationInfos.get(affiliation_id).getRole());
          statement.execute();
          logger.info("Successfully added person affiliation information to db.");
        } catch (SQLException e) {
          res = false;
          logger.error("SQL operation unsuccessful: " + e.getMessage());
          e.printStackTrace();
        } finally {
          endQuery(null, statement);
        }
      }
    }
    if (conn != null)
      logout(conn);
    return res;
  }

  // public List<String> getPersons() {
  // List<String> res = new ArrayList<String>();
  // String sql = "SELECT * FROM person";
  // Connection conn = login();
  // try (PreparedStatement statement = conn.prepareStatement(sql)) {
  // ResultSet rs = statement.executeQuery();
  // while (rs.next()) {
  // String first = Integer.toString(rs.getInt("first_name"));
  // String last = Integer.toString(rs.getInt("last_name"));
  // res.add(first + " " + last);
  // }
  // } catch (SQLException e) {
  // e.printStackTrace();
  // } finally {
  // endQuery(conn, statement);
  // }
  // return res;
  // }

  public Map<String, Integer> getPersonMap() {
    Map<String, Integer> res = new HashMap<String, Integer>();
    String sql = "SELECT * FROM persons";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        int id = rs.getInt("id");
        String first = rs.getString("first_name");
        String last = rs.getString("family_name");
        res.put(first + " " + last, id);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }

  public void printTableNames() throws SQLException {
    Connection conn = login();
    DatabaseMetaData md = conn.getMetaData();
    ResultSet rs = md.getTables(null, null, "%", null);
    while (rs.next()) {
      String table = rs.getString(3);
      System.out.println(table);
      String sql = "SELECT * FROM " + table;
      PreparedStatement statement = null;
      try {
        statement = conn.prepareStatement(sql);
        ResultSet r = statement.executeQuery();
        ResultSetMetaData metaData = r.getMetaData();
        int count = metaData.getColumnCount(); // number of column
        for (int i = 1; i <= count; i++) {
          System.out.println("col: " + metaData.getColumnLabel(i));
        }
      } catch (Exception e) {
        // TODO: handle exception
      } finally {
        if (statement != null)
          statement.close();
      }
    }
    logout(conn);
  }

  public boolean addOrUpdatePersonAffiliationConnections(int personID,
      List<PersonAffiliationConnectionInfo> newConnections) {
    Connection conn = login();
    boolean res = false;
    String sql = "";
    PreparedStatement statement = null;
    List<Integer> knownAffiliationIDs = getPersonAffiliationIDs(personID);
    for (PersonAffiliationConnectionInfo data : newConnections) {
      int affiID = data.getAffiliation_id();
      String role = data.getRole();
      logger.debug("Trying to add user " + personID + " (" + role + ") to affiliation " + affiID);
      if (knownAffiliationIDs.contains(affiID)) {
        logger.debug("user is part of that affiliation, updating role");
        sql =
            "UPDATE persons_organizations SET occupation = ? WHERE person_id = ? and organization_id = ?";
        try {
          statement = conn.prepareStatement(sql);
          statement.setInt(2, personID);
          statement.setInt(3, affiID);
          statement.setString(1, role);
          statement.execute();
          logger.info("Successful.");
          res = true;
        } catch (SQLException e) {
          logger.error("SQL operation unsuccessful: " + e.getMessage());
          e.printStackTrace();
        } finally {
          // we updated this connection and can remove the id
          knownAffiliationIDs.remove(new Integer(affiID));
        }
      } else {
        sql =
            "INSERT INTO persons_organizations (person_id, organization_id, occupation) VALUES (?,?,?)";
        try {
          logger.debug("user is not part of that affiliation yet, inserting");
          statement = conn.prepareStatement(sql);
          statement.setInt(1, personID);
          statement.setInt(2, affiID);
          statement.setString(3, role);
          statement.execute();
          logger.info("Successful.");
          res = true;
        } catch (SQLException e) {
          logger.error("SQL operation unsuccessful: " + e.getMessage());
          e.printStackTrace();
        } finally {
          // we added this connection and can remove the id
          knownAffiliationIDs.remove(new Integer(affiID));
        }
      }
    }
    // any ID still in the list must be from an affiliation connection that the user removed in
    // the UI, thus we remove it from the DB
    for (int oldAffiID : knownAffiliationIDs) {
      sql = "DELETE FROM persons_organizations WHERE person_id = ? and organization_id = ?";
      try {
        logger.debug("user affiliation id " + oldAffiID
            + " found in DB, but not in updated connections, deleting this connection");
        statement = conn.prepareStatement(sql);
        statement.setInt(1, personID);
        statement.setInt(2, oldAffiID);
        statement.execute();
        logger.info("Successful.");
        res = true;
      } catch (SQLException e) {
        logger.error("SQL operation unsuccessful: " + e.getMessage());
        e.printStackTrace();
      }
    }
    endQuery(conn, statement);
    return res;
  }

  public List<Integer> getPersonAffiliationIDs(int person_id) {
    List<Integer> res = new ArrayList<Integer>();
    String sql = "SELECT * FROM persons_organizations WHERE person_id = ?";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      statement.setInt(1, person_id);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        res.add(rs.getInt("organization_id"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }

  public List<Person> getPersonTable() {
    List<Person> res = new ArrayList<Person>();
    String lnk = "persons_organizations";
    String sql =
        "SELECT persons.*, organizations.id, organizations.group_name, organizations.group_acronym, "
            + lnk + ".occupation FROM persons, organizations, " + lnk + " WHERE persons.id = " + lnk
            + ".person_id and organizations.id = " + lnk + ".organization_id";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String title = rs.getString("title");
        String first = rs.getString("first_name");
        String last = rs.getString("family_name");
        String eMail = rs.getString("email");
        String phone = rs.getString("phone");
        int affiliationID = rs.getInt("organizations.id");
        String affiliation =
            rs.getString("group_name") + " (" + rs.getString("group_acronym") + ")";
        String role = rs.getString(lnk + ".occupation");
        res.add(new Person(username, title, first, last, eMail, phone, affiliationID, affiliation,
            role));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }

  public Set<String> getInstituteNames() {
    Set<String> res = new HashSet<String>();
    String sql = "SELECT institute FROM organizations";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        res.add(rs.getString("institute"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }

  public Affiliation getOrganizationInfosFromInstitute(String institute) {
    Affiliation res = null;
    String sql = "SELECT * FROM organizations WHERE institute LIKE ?";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      statement.setString(1, institute + "%");
      ResultSet rs = statement.executeQuery();
      Set<String> streets = new HashSet<String>();
      Set<String> orgs = new HashSet<String>();
      Set<String> faculties = new HashSet<String>();
      Set<Integer> zips = new HashSet<Integer>();
      Set<String> cities = new HashSet<String>();
      Set<String> countries = new HashSet<String>();
      while (rs.next()) {
        faculties.add(rs.getString("faculty"));
        orgs.add(rs.getString("umbrella_organization"));
        streets.add(rs.getString("street"));
        zips.add(rs.getInt("zip_code"));
        countries.add(rs.getString("country"));
        cities.add(rs.getString("city"));
        institute = rs.getString("institute");
      }
      String street = "";
      String faculty = "";
      String organization = "";
      String zipCode = "";
      String city = "";
      String country = "";
      if (streets.size() == 1)
        street = streets.iterator().next();
      if (orgs.size() == 1)
        organization = orgs.iterator().next();
      if (faculties.size() == 1)
        faculty = faculties.iterator().next();
      if (countries.size() == 1)
        country = countries.iterator().next();
      if (zips.size() == 1)
        zipCode = Integer.toString(zips.iterator().next());
      if (cities.size() == 1)
        city = cities.iterator().next();
      res = new Affiliation(-1, "", "", organization, institute, faculty, "", "", street, zipCode,
          city, country, "");
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }

  public Affiliation getOrganizationInfosFromOrg(String organization) {
    Affiliation res = null, maybe = null;
    String sql = "SELECT * FROM organizations WHERE umbrella_organization LIKE ?";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      statement.setString(1, organization + "%");
      ResultSet rs = statement.executeQuery();
      String street = "";
      while (rs.next()) {
        String faculty = rs.getString("faculty");
        String institute = rs.getString("institute");
        if (!street.isEmpty() && !street.equals(rs.getString("street"))) {
          street = "";
          break;
        } else
          street = rs.getString("street");
        organization = rs.getString("umbrella_organization");
        String zipCode = rs.getString("zip_code");
        String city = rs.getString("city");
        String country = rs.getString("country");
        maybe = new Affiliation(-1, "", "", organization, institute, faculty, "", "", street,
            zipCode, city, country, "");
      }
      if (!street.isEmpty())
        res = maybe;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }

  public List<Affiliation> getAffiliationTable() {
    List<Affiliation> res = new ArrayList<Affiliation>();
    String sql = "SELECT * from organizations";
    // String sql =
    // "SELECT organizations.*, persons.first_name, persons.family_name FROM organizations, persons"
    // + " WHERE persons.id = main_contact";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        int id = rs.getInt("id");
        String groupName = rs.getString("group_name");
        String acronym = rs.getString("group_acronym");
        if (acronym == null)
          acronym = "";
        String organization = rs.getString("umbrella_organization");
        String faculty = rs.getString("faculty");
        String institute = rs.getString("institute");
        String street = rs.getString("street");
        String zipCode = rs.getString("zip_code");
        String city = rs.getString("city");
        String country = rs.getString("country");
        String webpage = rs.getString("webpage");
        int contactID = rs.getInt("main_contact");
        int headID = rs.getInt("head");
        String contact = null;
        String head = null;
        if (contactID > 0) {
          Person c = getPerson(contactID);
          contact = c.getFirst() + " " + c.getLast();
        }
        if (headID > 0) {
          Person h = getPerson(headID);
          head = h.getFirst() + " " + h.getLast();
        }
        res.add(new Affiliation(id, groupName, acronym, organization, institute, faculty, contact,
            head, street, zipCode, city, country, webpage));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }


  public String getInvestigatorForProject(String projectIdentifier) {
    String details = getPersonDetailsForProject(projectIdentifier, "PI");
    return details.split("\n")[0].trim();
  }

  public String getPersonDetailsForProject(String projectIdentifier, String role) {
    String sql =
        "SELECT projects_persons.*, projects.* FROM projects_persons, projects WHERE projects.openbis_project_identifier = ?"
            + " AND projects.id = projects_persons.project_id AND projects_persons.project_role = ?";

    int id = -1;

    List<Person> personWithAffiliations = new ArrayList<Person>();

    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      statement.setString(1, projectIdentifier);
      statement.setString(2, role);

      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        id = rs.getInt("person_id");
      }
      personWithAffiliations = getPersonWithAffiliations(id);
    } catch (SQLException e) {
      e.printStackTrace();
      logout(conn);
      // LOGGER.debug("Project not associated with Investigator. PI will be set to 'Unknown'");
    } finally {
      endQuery(conn, statement);
    }

    String details = "";
    if (personWithAffiliations.size() > 0) {
      Person p = personWithAffiliations.get(0);
      String institute = p.getOneAffiliationWithRole().getAffiliation();

      details = String.format("%s %s \n%s \n \n%s \n%s \n", p.getFirst(), p.getLast(), institute,
          p.getPhone(), p.geteMail());
      // TODO is address important?
    }
    return details;
  }

  public List<Person> getPersonWithAffiliations(Integer personID) {
    List<Person> res = new ArrayList<Person>();
    String lnk = "persons_organizations";
    String sql =
        "SELECT persons.*, organizations.id, organizations.group_name, organizations.group_acronym, "
            + lnk + ".occupation FROM persons, organizations, " + lnk + " WHERE persons.id = "
            + Integer.toString(personID) + " AND persons.id = " + lnk
            + ".person_id and organizations.id = " + lnk + ".organization_id";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String title = rs.getString("title");
        String first = rs.getString("first_name");
        String last = rs.getString("family_name");
        String eMail = rs.getString("email");
        String phone = rs.getString("phone");
        int affiliationID = rs.getInt("organizations.id");
        String affiliation = rs.getString("group_name");
        if (rs.getString("group_acronym") != null)
          affiliation += " (" + rs.getString("group_acronym") + ")";
        String role = rs.getString(lnk + ".occupation");
        res.add(new Person(username, title, first, last, eMail, phone, affiliationID, affiliation,
            role));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }

  public Person getPerson(Integer id) {
    Person res = null;
    String sql = "SELECT * FROM persons WHERE persons.id = " + Integer.toString(id);
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        String username = rs.getString("username");
        String title = rs.getString("title");
        String first = rs.getString("first_name");
        String last = rs.getString("family_name");
        String eMail = rs.getString("email");
        String phone = rs.getString("phone");
        res = new Person(username, title, first, last, eMail, phone, -1, null, null);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res;
  }

  private void endQuery(Connection c, PreparedStatement p) {
    if (p != null)
      try {
        p.close();
      } catch (Exception e) {
        logger.error("PreparedStatement close problem");
      }
    if (c != null)
      try {
        logout(c);
      } catch (Exception e) {
        logger.error("Database Connection close problem");
      }
  }

  public void setAffiliationVIP(int affi, int person, String role) {
    logger.info("Trying to set/change affiliation-specific role " + role);
    String sql = "UPDATE organizations SET " + role + "=? WHERE id = ?";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      statement.setInt(1, person);
      statement.setInt(2, affi);
      statement.execute();
      logger.info("Successful for " + role);
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    } finally {
      endQuery(null, statement);
    }
  }
}
