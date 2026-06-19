package fr.recia.manager.web.dto.enseignement;

import fr.recia.manager.db.dto.groupe.ClasseDto;
import fr.recia.manager.db.entities.education.MEF;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormationPossibleDto {
    private long id;
    private String libelle;
    private List<ClasseFormationPossibleDto> classes;

    public FormationPossibleDto(MEF mef){
        this.id = mef.getId();
        this.libelle = mef.getFiliere();
        this.classes = new ArrayList<>();
    }

    public ClasseFormationPossibleDto addClasse(ClasseDto classe){
        ClasseFormationPossibleDto classeFormationPossibleDto = new ClasseFormationPossibleDto(classe);
        this.classes.add(classeFormationPossibleDto);
        return classeFormationPossibleDto;
    }
}
