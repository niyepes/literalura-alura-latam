package com.alura.literalura.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.*;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GutendexResponse {
    public int count;
    public String next;
    public String previous;
    public List<GutendexLibroDTO> results;
}
