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
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Person {

  // private int id;
  private String username;
  private String title;
  private String first;
  private String last;
  private String eMail;
  private String phone;
  private Map<Integer, RoleAt> affiliationInfo; // ids and roles
  private List<Affiliation> affiliations;

  public Person(String username, String title, String first, String last, String eMail,
      String phone, int affiliationID, String affiliationName, String affRole,
      List<Affiliation> affiliations) {
    super();
    this.username = username;
    this.title = title;
    this.first = first;
    this.last = last;
    this.eMail = eMail;
    this.phone = phone;
    this.affiliations = affiliations;
    affiliationInfo = new HashMap<Integer, RoleAt>();
    affiliationInfo.put(affiliationID, new RoleAt(affiliationName, affRole));
  }

  public Person(String username, String title, String first, String last, String eMail,
      String phone, int affiliationID, String affiliationName, String affRole) {
    super();
    this.username = username;
    this.title = title;
    this.first = first;
    this.last = last;
    this.eMail = eMail;
    this.phone = phone;
    this.affiliations = new ArrayList<Affiliation>();
    affiliationInfo = new HashMap<Integer, RoleAt>();
    affiliationInfo.put(affiliationID, new RoleAt(affiliationName, affRole));
  }

  // public Person(int id, String username, String title, String first, String last, String eMail,
  // String phone, int affiID, String affiliation, String role) {
  // super();
  //// this.id = id;
  // this.username = username;
  // this.title = title;
  // this.first = first;
  // this.last = last;
  // this.eMail = eMail;
  // this.phone = phone;
  // affiliationInfo = new HashMap<Integer, RoleAt>();
  // affiliationInfo.put(affiID, new RoleAt(affiliation, role));
  // }

  /**
   * returns a random affiliation with its role for this user
   * 
   * @return
   */
  public RoleAt getOneAffiliationWithRole() {
    Random random = new Random();
    List<Integer> keys = new ArrayList<Integer>(affiliationInfo.keySet());
    Integer randomKey = keys.get(random.nextInt(keys.size()));
    return affiliationInfo.get(randomKey);
  }

  public String getUsername() {
    return username;
  }

  public String getTitle() {
    return title;
  }

  public String getFirst() {
    return first;
  }

  public String getLast() {
    return last;
  }

  public String geteMail() {
    return eMail;
  }

  public String getPhone() {
    return phone;
  }

  public Map<Integer, RoleAt> getAffiliationInfos() {
    return affiliationInfo;
  }

  public void addAffiliationInfo(int id, String name, String role) {
    affiliationInfo.put(id, new RoleAt(name, role));
  }
  //
  // public int getID() {
  // return id;
  // }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Person other = (Person) obj;
    if (affiliationInfo == null) {
      if (other.affiliationInfo != null)
        return false;
    } else if (!affiliationInfo.equals(other.affiliationInfo))
      return false;
    if (eMail == null) {
      if (other.eMail != null)
        return false;
    } else if (!eMail.equals(other.eMail))
      return false;
    if (first == null) {
      if (other.first != null)
        return false;
    } else if (!first.equals(other.first))
      return false;
    if (last == null) {
      if (other.last != null)
        return false;
    } else if (!last.equals(other.last))
      return false;
    if (phone == null) {
      if (other.phone != null)
        return false;
    } else if (!phone.equals(other.phone))
      return false;
    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;
    if (username == null) {
      if (other.username != null)
        return false;
    } else if (!username.equals(other.username))
      return false;
    return true;
  }

  public void setAffiliationID(int affiID) {
    RoleAt affi = affiliationInfo.get(-1);
    affiliationInfo.remove(-1);
    affiliationInfo.put(affiID, affi);
  }

  public List<Affiliation> getAffiliations() {
    return affiliations;
  }
}
