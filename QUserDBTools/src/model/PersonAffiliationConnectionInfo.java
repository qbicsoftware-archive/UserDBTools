package model;

public class PersonAffiliationConnectionInfo {
  
  private int person_id;
  private int affiliation_id;
  private String role;
  
  public PersonAffiliationConnectionInfo(int person_id, int affiliation_id, String role) {
    super();
    this.person_id = person_id;
    this.affiliation_id = affiliation_id;
    this.role = role;
  }
  
  public int getPerson_id() {
    return person_id;
  }
  public int getAffiliation_id() {
    return affiliation_id;
  }
  public String getRole() {
    return role;
  }

}
