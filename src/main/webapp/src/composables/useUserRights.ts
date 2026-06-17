/**
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

import type { Ref } from 'vue'
import type { User } from '@/types/index.ts'
import { computed } from 'vue'
import { CategoriePersonne, Etat } from '@/types/enums'

export function useUserRights(
  user: Ref<User | undefined>,
) {
  const canReset = computed<boolean>(() => {
    if (!user.value)
      return false

    const { etat, local, guichet } = user.value

    return (local || !guichet) && [
      Etat.Valide,
      Etat.Bloque,
    ].includes(etat)
  })

  const canToggleLock = computed<boolean>(() => {
    if (!user.value)
      return false

    const { etat } = user.value

    return [
      Etat.Valide,
      Etat.Bloque,
    ].includes(etat)
  })

  const canDelete = computed<boolean>(() => {
    if (!user.value)
      return false

    const { etat, local } = user.value

    return local && !(
      etat === Etat.Deleting
      || etat === Etat.Delete
    )
  })

  const canUndoDelete = computed<boolean>(() => {
    if (!user.value)
      return false

    const { etat } = user.value

    return etat === Etat.Deleting
      || etat === Etat.Delete
  })

  const canForceDelete = computed<boolean>(() => {
    if (!user.value)
      return false

    const { etat } = user.value

    return etat === Etat.Deleting
  })

  const canAttach = computed<boolean>(() => {
    if (!user.value)
      return false

    const { etat } = user.value

    return etat === Etat.Invalide
      || etat === Etat.Valide
      || etat === Etat.Bloque
  })

  const isLocked = computed<boolean>(() => {
    if (!user.value)
      return false

    const { etat } = user.value

    return etat === Etat.Bloque
  })

  const isDeleting = computed<boolean>(() => {
    if (!user.value)
      return false

    const { etat } = user.value

    return etat === Etat.Deleting
  })

  const canSeeClassesAndGroups = computed<boolean>(() => {
    if (!user.value)
      return false

    const { categorie } = user.value

    return categorie === CategoriePersonne.Eleve
      || categorie === CategoriePersonne.Enseignant
  })

  const canSeeFunctions = computed<boolean>(() => {
    if (!user.value)
      return false

    const { categorie } = user.value

    return categorie === CategoriePersonne.Enseignant
      || categorie === CategoriePersonne.Non_enseignant_collectivite_locale
      || categorie === CategoriePersonne.Non_enseignant_etablissement
      || categorie === CategoriePersonne.Non_enseignant_service_academique
  })

  return {
    canReset,
    canToggleLock,
    canDelete,
    canUndoDelete,
    canForceDelete,
    canAttach,
    isLocked,
    isDeleting,
    canSeeClassesAndGroups,
    canSeeFunctions,
  }
}
