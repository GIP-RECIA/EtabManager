package fr.recia.web.dto.export;

import lombok.Data;

import java.util.List;

@Data
public class EsidocWSResponse {
    private List<String> exceptionUais;
    private String success;
}
