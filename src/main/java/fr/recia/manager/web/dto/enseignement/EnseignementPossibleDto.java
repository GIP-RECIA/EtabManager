package fr.recia.manager.web.dto.enseignement;

import fr.recia.manager.db.dto.groupe.EnseignementEtabDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EnseignementPossibleDto {
    private long id;
    private String libelle;
    private List<ClasseEnseignementPossibleDto> classes;
    private List<GroupeEnseignementPossibleDto> groupes;

    public EnseignementPossibleDto(EnseignementEtabDto enseignement){
        this.id = enseignement.getIdEns();
        this.libelle = enseignement.getMatiere();
        this.classes = new ArrayList<>();
        this.groupes = new ArrayList<>();
    }

    public void addClasse(EnseignementEtabDto enseignementEtabDto){
        this.classes.add(new ClasseEnseignementPossibleDto(enseignementEtabDto));
    }

    public void addGroupe(EnseignementEtabDto enseignementEtabDto){
        this.groupes.add(new GroupeEnseignementPossibleDto(enseignementEtabDto));
    }
}
