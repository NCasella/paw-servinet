package ar.edu.itba.paw.webapp.dto.input;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Data
@NoArgsConstructor
public class PasswordRecoveryRequestDTO {
    @NotNull(message = "NotNull.requestPasswordRecoveryForm.email")
    @NotEmpty(message = "NotNull.requestPasswordRecoveryForm.email")
    @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",message ="Email.registerUserForm.email" )
    @Size(max=255,message = "Size.requestPasswordRecoveryForm.email")
    private String email;

    @Override
    public int hashCode(){
        return Objects.hash(email);
    }
}
