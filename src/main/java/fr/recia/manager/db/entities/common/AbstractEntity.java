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

package fr.recia.manager.db.entities.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public abstract class AbstractEntity extends AbstractSimpleEntity implements Serializable {

    /** Gestion des versions de l'objet, incrémentation de +1 à chaque modification de update ou merge. */
    @Version
    private long version;
    /** Création automatique de la date de création de l'objet lors de la construction. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;
    /** Donne l'information de la date de modification de l'objet. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateModification;
    /** Donne l'information de la date d'acquittement de l'objet lors de l'export. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAcquittement;

    /**
     * Setter automatique du membre dateCreation lors de la creation de l'objet.
     */
    @PrePersist
    public void prePersistOps() {
        Date d = new Date();
        d.setTime(Calendar.getInstance().getTimeInMillis());
        if (this.dateCreation == null) {
            this.dateCreation = d;
        }
        if (this.dateModification == null) {
            this.dateModification = d;
        }
    }

    /**
     * Setter automatique du membre dateModification lors de la modification de l'objet.
     * WARNING : Surchargé dans entity APersonne pour éviter la màj auto lors de la modification en delete.
     */
    @PreUpdate
    public void preUpdateOps() {
        this.dateModification.setTime(Calendar.getInstance().getTimeInMillis());
    }

    @Override
    public String toString() {
        return "AbstractEntity [" + super.toString() + ", " + this.version + ", " + this.dateCreation + ", " + this.dateModification + "]";
    }

}
