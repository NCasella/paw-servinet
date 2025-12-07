package ar.edu.itba.paw.webapp.dto.output.links;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLinks {
    private URI businessesOwned;
    private URI appointmentsRequested;
    private URI profilePicture;
    private URI self;
}
