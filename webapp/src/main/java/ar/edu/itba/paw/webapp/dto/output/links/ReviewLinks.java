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
public class ReviewLinks {
    private URI services;
    private URI self;
}
