package fr.recia.manager.db.dto.groupe;

import fr.recia.manager.db.enums.CategorieGroupe;

public interface EnseignementEtabDto {
    CategorieGroupe getCategorie();
    String getMatiere();
    String getCn();
    Long getIdGr();
    Long getIdEns();
}
