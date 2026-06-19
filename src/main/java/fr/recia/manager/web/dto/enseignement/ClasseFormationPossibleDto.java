package fr.recia.manager.web.dto.enseignement;

import fr.recia.manager.db.dto.groupe.ClasseDto;
import fr.recia.manager.db.entities.education.Enseignement;
import fr.recia.manager.db.entities.groupe.Classe;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ClasseFormationPossibleDto {
    private long id;
    private String libelle;
    private List<EnseignementFormationPossibleDto> enseignements;

    public ClasseFormationPossibleDto(ClasseDto classe){
        this.id = classe.getId();
        this.libelle = classe.getCn();
        this.enseignements = new ArrayList<>();
    }

    public void addEnseignement(Enseignement enseignement){
        EnseignementFormationPossibleDto enseignementFormationPossibleDto = new EnseignementFormationPossibleDto(enseignement);
        this.enseignements.add(enseignementFormationPossibleDto);
    }
}
