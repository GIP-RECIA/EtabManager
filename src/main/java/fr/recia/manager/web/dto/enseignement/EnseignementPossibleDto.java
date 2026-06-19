package fr.recia.manager.web.dto.enseignement;

import fr.recia.manager.db.entities.education.Enseignement;
import fr.recia.manager.db.entities.groupe.AGroupeOfFoncClasseGroupe;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EnseignementPossibleDto {
    private long id;
    private String libelle;
    private List<ClasseEnseignementPossibleDto> classes;
    private List<GroupeEnseignementPossibleDto> groupes;

    public EnseignementPossibleDto(Enseignement enseignement){
        this.id = enseignement.getId();
        this.libelle = enseignement.getMatiere();
        this.classes = new ArrayList<>();
        this.groupes = new ArrayList<>();
    }

    public void addClasse(AGroupeOfFoncClasseGroupe classe){
        this.classes.add(new ClasseEnseignementPossibleDto(classe));
    }

    public void addGroupe(AGroupeOfFoncClasseGroupe groupe){
        this.groupes.add(new GroupeEnseignementPossibleDto(groupe));
    }
}
