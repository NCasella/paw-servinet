package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.webapp.validation.EmailNotUsed;
import ar.edu.itba.paw.webapp.validation.UsernameNotUsed;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Objects;

@Data
@NoArgsConstructor
public class UserPatchDTO {
    @NotNull
    @NotEmpty
    @UsernameNotUsed
    @Size(max=255)
    private String username;
    @NotNull
    @NotEmpty
    @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @EmailNotUsed
    @Size(max=255)
    private String email;
    @NotNull
    @NotEmpty
    @Size(max=255)
    @Pattern(regexp = "^\\+(\\d{1,3})?\\s?9?\\s?(\\d{1,4})?\\s?(\\d{6,8})$")
    private String telephone;

    @Override
    public int hashCode(){
        return Objects.hash(email, telephone, username);
    }
}
