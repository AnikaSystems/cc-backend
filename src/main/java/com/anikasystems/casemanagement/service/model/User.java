package com.anikasystems.casemanagement.service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(name = "firstname")
  private String firstname;

  @Column(name = "lastname")
  private String lastname;

  @Column(name = "internal")
  private boolean internal;

  public User() {

  }

  public User(String firstname, String lastname, boolean internal) {
    this.firstname = firstname;
    this.lastname = lastname;
    this.internal = internal;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public boolean isInternal() {
    return internal;
  }

  public void setInternal(boolean internal) {
    this.internal = internal;
  }

  @Override
  public String toString() {
    return "User [id=" + id + ", firstname=" + firstname + ", lastname=" + lastname + ", internal=" + internal + "]";
  }

}
