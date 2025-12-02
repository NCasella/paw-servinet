package ar.edu.itba.paw.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationDTO {
    private String name;
    private String surname;
    private String email;
    private String telephone;
    private String username;
    private String password;
}
