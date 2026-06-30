/*
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.recia.manager.services.alert;

import fr.recia.manager.configuration.AppProperties;
import fr.recia.manager.configuration.bean.CustomConfigProperties;
import fr.recia.manager.db.dto.AlertDto;
import fr.recia.manager.db.dto.AlertType;
import fr.recia.manager.db.dto.education.DisciplineDto;
import fr.recia.manager.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.manager.services.db.FonctionService;
import fr.recia.manager.web.dto.function.DisciplineAlertDto;
import fr.recia.manager.web.dto.function.FiliereAlertDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AlertService {

    @Autowired
    private FonctionService fonctionService;

    private final List<CustomConfigProperties.AlertProperties> alertProperties;

    public AlertService(AppProperties appProperties) {
        this.alertProperties = appProperties.getCustomConfig().getAlerts();
    }

    private AlertDto buildFonctionAlert(TypeFonctionFiliereDto filiere, DisciplineDto discipline, int min, int max, int nbActuel, AlertType level, FonctionAlertType type, boolean action) {
        return AlertDto.builder()
            .filiere(new FiliereAlertDto(filiere.getId(), filiere.getLibelle()))
            .discipline(new DisciplineAlertDto(discipline.getId(), discipline.getLibelle()))
            .min(min)
            .max(max)
            .nbActuel(nbActuel)
            .level(level)
            .type(type)
            .action(action)
            .build();
    }

    @Cacheable(value = "fonctionAlerts", key = "#etablissementId")
    public List<AlertDto> getFonctionAlerts(Long etablissementId, String etablissementSource) {
        log.trace("getFonctionAlerts for {} and {}", etablissementId, etablissementSource);
        List<AlertDto> alerts = new ArrayList<>();
        for (CustomConfigProperties.AlertProperties alert : alertProperties) {
            // On ne regarde que les alertes de la même source que celle de l'établissement
            if (etablissementSource.equals(alert.getSource())) {
                List<CustomConfigProperties.AlertProperties.FonctionAlertProperties> fonctionAlerts = alert.getFonctionAlerts();
                if (fonctionAlerts != null) {
                    // Pour chaque alerte de cette source on regarde si on est dans ce cas
                    for (CustomConfigProperties.AlertProperties.FonctionAlertProperties fonctionAlert : fonctionAlerts) {
                        long nbDiscipline = fonctionService.nbDiscipline(etablissementId, fonctionAlert.getFiliere(), fonctionAlert.getDiscipline());
                        final TypeFonctionFiliereDto filiere = fonctionService.getTypeFonctionFiliereByCode(fonctionAlert.getFiliere(), etablissementSource);
                        final DisciplineDto discipline = fonctionService.getDisciplineByCode(fonctionAlert.getDiscipline(), etablissementSource);
                        if(filiere != null && discipline != null){
                            if (fonctionAlert.getMin() != null && fonctionAlert.getMin().getValue() > 0 && nbDiscipline < fonctionAlert.getMin().getValue()) {
                                alerts.add(buildFonctionAlert(filiere, discipline, fonctionAlert.getMin().getValue(),
                                    fonctionAlert.getMax().getValue(), (int) nbDiscipline, fonctionAlert.getMin().getType(), FonctionAlertType.min, fonctionAlert.getMin().isAction()));
                            }
                            if (fonctionAlert.getMax() != null && fonctionAlert.getMax().getValue() > 0 && nbDiscipline > fonctionAlert.getMax().getValue()) {
                                alerts.add(buildFonctionAlert(filiere, discipline, fonctionAlert.getMin().getValue(),
                                    fonctionAlert.getMax().getValue(), (int) nbDiscipline, fonctionAlert.getMax().getType(), FonctionAlertType.max, fonctionAlert.getMax().isAction()));
                            }
                        } else {
                            log.warn("Filiere or discipline is null for {}", fonctionAlert);
                        }
                    }
                }
                break;
            }
        }
        return alerts;
    }

}
