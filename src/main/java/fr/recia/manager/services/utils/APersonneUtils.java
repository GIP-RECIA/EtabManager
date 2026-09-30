package fr.recia.manager.services.utils;

import fr.recia.manager.configuration.bean.CustomConfigProperties;
import fr.recia.manager.db.dto.personne.DatabasePersonneDto;
import fr.recia.manager.db.entities.personne.APersonne;

import java.util.List;

public class APersonneUtils {

    /**
     * Donne le nom du guichet externe d'une personne.
     * Retourne null si la personne s'authentifie sur notre LDAP
     */
    public static String getGuichet(APersonne aPersonne, List<CustomConfigProperties.LoginOfficeProperties> loginOfficeProperties){
        for(CustomConfigProperties.LoginOfficeProperties loginOfficeProperty : loginOfficeProperties){
            if(loginOfficeProperty.getSource().equals(aPersonne.getCleJointure().getSource())){
                for(CustomConfigProperties.LoginOfficeProperties.GuichetProperties guichetProperty : loginOfficeProperty.getGuichets()){
                    if(guichetProperty.getCategoriesPersonne().contains(aPersonne.getCategorie())){
                        return guichetProperty.getNom();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Donne le nom du guichet externe d'une personne.
     * Retourne null si la personne s'authentifie sur notre LDAP
     */
    public static String getGuichet(DatabasePersonneDto databasePersonneDto, List<CustomConfigProperties.LoginOfficeProperties> loginOfficeProperties){
        for(CustomConfigProperties.LoginOfficeProperties loginOfficeProperty : loginOfficeProperties){
            if(loginOfficeProperty.getSource().equals(databasePersonneDto.getSource())){
                for(CustomConfigProperties.LoginOfficeProperties.GuichetProperties guichetProperty : loginOfficeProperty.getGuichets()){
                    if(guichetProperty.getCategoriesPersonne().contains(databasePersonneDto.getCategorie())){
                        return guichetProperty.getNom();
                    }
                }
            }
        }
        return null;
    }

}
