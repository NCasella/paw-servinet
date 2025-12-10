package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.webapp.validation.EmailNotUsed;
import ar.edu.itba.paw.webapp.validation.UsernameNotUsed;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Objects;

@Data
@NoArgsConstructor
public class UserCreationDTO {
    @NotEmpty(message = "NotEmpty.registerUserForm.name")
    @NotNull(message = "NotNull.registerUserForm.name")
    @Size(max=255,message = "Size.registerUserForm.name")
    private String name;

    @NotNull(message = "NotNull.registerUserForm.surname")
    @NotEmpty(message = "NotEmpty.registerUserForm.surname")
    @Size(max=255,message = "Size.registerUserForm.surname")
    private String surname;

    @NotNull(message = "NotNull.registerUserForm.email")
    @NotEmpty(message = "NotEmpty.registerUserForm.email")
    @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",message = "Email.registerUserForm.email")
    @EmailNotUsed
    @Size(max=255,message = "Size.registerUserForm.email")
    private String email;

    @NotNull(message = "NotNull.registerUserForm.telephone")
    @NotEmpty(message = "NotEmpty.registerUserForm.telephone")
    @Size(max=255,message = "Size.registerUserForm.telephone")
    @Pattern(regexp = "^\\+(\\d{1,3})?\\s?9?\\s?(\\d{1,4})?\\s?(\\d{6,8})$",message = "Pattern.registerUserForm.telephone")
    private String telephone;

    @NotNull(message = "NotNull.registerUserForm.username")
    @NotEmpty(message = "NotEmpty.registerUserForm.username")
    @UsernameNotUsed
    @Size(max=255,message="Size.registerUserForm.username")
    private String username;
    @NotNull(message = "NotNull.registerUserForm.password")
    @NotEmpty(message = "NotEmpty.registerUserForm.password")
    @Size(max=255, min = 8,message = "Size.registerUserForm.password")
    private String password;

    @Override
    public int hashCode(){
        return Objects.hash(name, surname, email, telephone, username, password);
    }

}
