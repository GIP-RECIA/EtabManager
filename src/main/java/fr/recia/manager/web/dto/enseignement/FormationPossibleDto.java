package fr.recia.manager.web.dto.enseignement;

import lombok.Data;

import java.util.List;

@Data
public class FormationPossibleDto {
    private NiveauFormationPossibleDto niveau;
    private ClasseFormationPossibleDto classe;
    private List<EnseignementFormationPossibleDto> enseignements;
}
