package ar.edu.itba.paw.webapp.dto.input;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PasswordResetDTO {
    @NotNull
    @NotEmpty
    @Size(max=255, min = 8)
    private String newPassword;

    @NotNull
    @NotEmpty
    private String code;

    public UUID getCodeAsUuid(){
        return UUID.fromString(code);
    }

    @Override
    public int hashCode(){
        return Objects.hash(code);
    }

}
