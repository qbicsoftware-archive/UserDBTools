package model;

public class ProjectInfo {

  private String space;
  private String projectCode;
  private String projectName;
  private int projectID;
  private String investigator;
  private String contact;
  private String manager;

  public ProjectInfo(String space, String projectCode, String projectName, int projectID,
      String investigator, String contact, String manager) {
    super();
    this.space = space;
    this.projectCode = projectCode;
    this.projectName = projectName;
    this.projectID = projectID;
    this.investigator = investigator;
    this.contact = contact;
    this.manager = manager;
  }

  public ProjectInfo(String space, String project, String shortName, int id) {
    super();
    this.space = space;
    this.projectCode = project;
    this.projectName = shortName;
    this.projectID = id;
  }

  public String getSpace() {
    return space;
  }

  public String getProjectCode() {
    return projectCode;
  }

  public String getProjectName() {
    return projectName;
  }

  public int getProjectID() {
    return projectID;
  }

  public String getInvestigator() {
    return investigator;
  }

  public String getContact() {
    return contact;
  }

  public String getManager() {
    return manager;
  }

  public void setManager(String name) {
    this.manager = name;
  }

  public void setContact(String name) {
    this.contact = name;
  }

  public void setInvestigator(String name) {
    this.investigator = name;
  }

  public void setProjectName(String newName) {
    this.projectName = newName;
  }

  @Override
  public String toString() {
    String res = projectCode + " (" + projectName + ")\n";
    res += "PI: " + investigator + ", ";
    res += "Ctct: " + contact + ", ";
    res += "Mngr: " + manager;
    return res;
  }

}
