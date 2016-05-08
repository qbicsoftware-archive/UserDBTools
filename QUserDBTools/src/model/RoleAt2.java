package model;

public class RoleAt2 {

  private Affiliation affiliation;
  private Person person;
  private String role;

  public RoleAt2(String affiliation, String role) {
    this.affiliation = affiliation;
    this.role = role;
  }
  
  public String getAffiliation() {
    return affiliation;
  }

  public String getRole() {
    return role;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RoleAt2 other = (RoleAt2) obj;
    if (affiliation == null) {
      if (other.affiliation != null)
        return false;
    } else if (!affiliation.equals(other.affiliation))
      return false;
    if (role == null) {
      if (other.role != null)
        return false;
    } else if (!role.equals(other.role))
      return false;
    return true;
  }

}
