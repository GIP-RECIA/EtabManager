package fr.recia.manager.web.dto.enseignement;

import lombok.Data;

import java.util.List;

@Data
public class FormationModifyRequest {
    private long structure;
    private long mef;
    private long classe;
    private List<Long> enseignements;
}
