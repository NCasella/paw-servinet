package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.EmailNotUsed;
import ar.edu.itba.paw.webapp.validation.FieldsValueMatch;
import ar.edu.itba.paw.webapp.validation.UsernameNotUsed;

import javax.validation.constraints.*;
@FieldsValueMatch(field = "password", fieldMatch = "passwordConfirmation")
public class RegisterUserForm {
   @NotEmpty
   @NotNull
   @Size(max=255)
   private String name;

   @NotNull
   @NotEmpty
   @Size(max=255)
   private String surname;

   @NotNull
   @NotEmpty
   @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
   @EmailNotUsed
   @Size(max=255)
   private String email;

   @NotNull
   @NotEmpty
   @UsernameNotUsed
   @Size(max=255)
   private String username;

   @NotNull
   @NotEmpty
   @Size(max=255, min = 8)
   private String password;

   @NotNull
   @NotEmpty
   @Size(max=255, min = 8)
   private String passwordConfirmation;

   @NotNull
   @NotEmpty
   @Size(max=255)
   @Pattern(regexp = "^\\+(\\d{1,3})?\\s?9?\\s?(\\d{1,4})?\\s?(\\d{6,8})$")
   private String telephone;

   public String getUsername(){
       return username;
   }

   public String getName() {
       return name;
   }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public String getSurname() {
        return surname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

}
