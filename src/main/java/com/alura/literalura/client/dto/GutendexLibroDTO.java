package com.alura.literalura.client.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GutendexLibroDTO {
    public String title;

    public List<GutendexAutorDTO> authors;

    public List<String> languages;

    @JsonAlias("download_count")
    public Integer downloadCount;

}
