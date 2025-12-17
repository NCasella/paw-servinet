package ar.edu.itba.paw.webapp.dto.input;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Objects;

@Data
@NoArgsConstructor
public class UserPatchDTO {
    private String username;
    private String email;
    private String telephone;
    private String locale;
    private String password;

    @Override
    public int hashCode(){
        return Objects.hash(email, telephone, username);
    }
}
