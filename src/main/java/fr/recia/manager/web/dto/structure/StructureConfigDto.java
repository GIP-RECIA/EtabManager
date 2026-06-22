package fr.recia.manager.web.dto.structure;

import fr.recia.manager.db.enums.CategoriePersonne;
import lombok.Data;

import java.util.List;

@Data
public class StructureConfigDto {
    private List<CategoriePersonne> categoriesPersonne;
}
