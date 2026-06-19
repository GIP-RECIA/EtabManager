package fr.recia.manager.web.dto.enseignement;

import fr.recia.manager.db.entities.education.Enseignement;
import lombok.Data;

@Data
public class EnseignementFormationPossibleDto {
    private long id;
    private String libelle;

    public EnseignementFormationPossibleDto(Enseignement enseignement){
        this.id = enseignement.getId();
        this.libelle = enseignement.getMatiere();
    }
}
