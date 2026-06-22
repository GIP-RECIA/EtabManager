package fr.recia.manager.web.dto.enseignement;

import fr.recia.manager.db.dto.groupe.EnseignementEtabDto;
import lombok.Data;

@Data
public class ClasseEnseignementPossibleDto {
    private long id;
    private String libelle;

    public ClasseEnseignementPossibleDto(EnseignementEtabDto enseignementEtabDto){
        this.id = enseignementEtabDto.getIdGr();
        this.libelle = enseignementEtabDto.getCn();
    }
}
