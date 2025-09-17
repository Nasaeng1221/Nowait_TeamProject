package com.myboot.auth;

public class Member {
  private long id;
  private String username;
  private String password;
  private String name;
  private String phone;
  private String email;
  private String role; // USER / OWNER / ADMIN

  public long getId(){ return id; }
  public void setId(long id){ this.id=id; }
  public String getUsername(){ return username; }
  public void setUsername(String v){ this.username=v; }
  public String getPassword(){ return password; }
  public void setPassword(String v){ this.password=v; }
  public String getName(){ return name; }
  public void setName(String v){ this.name=v; }
  public String getPhone(){ return phone; }
  public void setPhone(String v){ this.phone=v; }
  public String getEmail(){ return email; }
  public void setEmail(String v){ this.email=v; }
  public String getRole(){ return role; }
  public void setRole(String v){ this.role=v; }
}
