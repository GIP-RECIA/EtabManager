package fr.recia.manager.web.dto.enseignement;

import lombok.Data;

import java.util.List;

@Data
public class EnseignementModifyRequest {
    private long structure;
    private long enseignement;
    private List<Long> classesGroupes;
}
