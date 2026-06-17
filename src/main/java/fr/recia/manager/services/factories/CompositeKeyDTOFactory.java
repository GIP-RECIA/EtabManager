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

package fr.recia.manager.services.factories;

import fr.recia.manager.web.dto.ICompositeKey;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Data Transfer Object factory for converting to and from models.
 *
 * @param <DTOKEY>   DTO type for the corresponding model
 * @param <MODELKEY> Model type
 * @param <KEY>      Type of id of the composite key
 * @param <TYPE>     Enum Type of the composite key
 * @author GIP RECIA - Julien Gribonvald
 */
public interface CompositeKeyDTOFactory<DTOKEY extends ICompositeKey<KEY, TYPE>, MODELKEY extends ICompositeKey<KEY, TYPE>, KEY extends Serializable, TYPE extends Serializable> {


    /**
     * Load model from storage based on the specified identifier.
     *
     * @param id Load model for this identifier
     * @return Model loaded from persistent storage
     * //@throws org.esupportail.publisher.service.exceptions.ObjectNotFoundException
     */
    MODELKEY convertToModelKey(@NotNull final DTOKEY id);

    Set<MODELKEY> convertToModelKey(@NotNull final Set<DTOKEY> id);

    List<MODELKEY> convertToModelKey(@NotNull final List<DTOKEY> id);

    /**
     * Load model from storage based on the specified identifier.
     *
     * @param set Load model for this identifier
     * @return Model loaded from persistent storage
     * //@throws org.esupportail.publisher.service.exceptions.ObjectNotFoundException
     */
    DTOKEY convertToDTOKey(@NotNull final MODELKEY set);

    Set<DTOKEY> convertToDTOKey(@NotNull final Set<MODELKEY> id);

    List<DTOKEY> convertToDTOKey(@NotNull final List<MODELKEY> id);


}
