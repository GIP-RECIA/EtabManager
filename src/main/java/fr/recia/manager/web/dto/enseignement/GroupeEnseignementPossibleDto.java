package fr.recia.manager.web.dto.enseignement;

import fr.recia.manager.db.entities.groupe.AGroupeOfFoncClasseGroupe;
import lombok.Data;

@Data
public class GroupeEnseignementPossibleDto {
    private long id;
    private String libelle;

    public GroupeEnseignementPossibleDto(AGroupeOfFoncClasseGroupe groupe){
        this.id = groupe.getId();
        this.libelle = groupe.getCn();
    }
}
