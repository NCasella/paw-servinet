package ar.edu.itba.paw.webapp.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexDto {
    private String users;
    private String businesses;
    private String services;
    private String appointments;
    private String questions;
    private String reviews;
    private String images;
}
