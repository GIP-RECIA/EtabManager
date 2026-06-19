package fr.recia.manager.web.dto.enseignement;

import fr.recia.manager.db.entities.groupe.AGroupeOfFoncClasseGroupe;
import lombok.Data;

@Data
public class ClasseEnseignementPossibleDto {
    private long id;
    private String libelle;

    public ClasseEnseignementPossibleDto(AGroupeOfFoncClasseGroupe classe){
        this.id = classe.getId();
        this.libelle = classe.getCn();
    }
}
