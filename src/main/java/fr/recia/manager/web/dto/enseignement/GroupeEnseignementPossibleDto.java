package fr.recia.manager.web.dto.enseignement;

import fr.recia.manager.db.dto.groupe.EnseignementEtabDto;
import lombok.Data;

@Data
public class GroupeEnseignementPossibleDto {
    private long id;
    private String libelle;

    public GroupeEnseignementPossibleDto(EnseignementEtabDto enseignementEtabDto){
        this.id = enseignementEtabDto.getIdGr();
        this.libelle = enseignementEtabDto.getCn();
    }
}
