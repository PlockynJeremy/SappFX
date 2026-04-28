package model;

public class User {

    private int    Id;
    private String username;
    private String role;

    public int getId()         { return Id; }
    public String getUsername(){ return username; }
    public String getRole()    { return role; }

    public void setId(int id)            { this.Id = id; }
    public void setUsername(String u)    { this.username = u; }
    public void setRole(String r)        { this.role = r; }
}
