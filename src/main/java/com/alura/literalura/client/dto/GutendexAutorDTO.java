package com.alura.literalura.client.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GutendexAutorDTO {
    public String name;

    @JsonAlias("birth_year")
    public Integer birthYear;

    @JsonAlias("death_year")
    public Integer deathYear;


}
