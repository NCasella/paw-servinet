package ar.edu.itba.paw.webapp.dto.input;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Data
@NoArgsConstructor
public class UserPasswordModificationDTO {
    @NotNull
    @NotEmpty
    @Size(max=255, min = 8)
    private String oldPassword;
    @NotNull
    @NotEmpty
    @Size(max=255, min = 8)
    private String newPassword;

    @Override
    public int hashCode(){
        return Objects.hash(oldPassword, newPassword);
    }

}
