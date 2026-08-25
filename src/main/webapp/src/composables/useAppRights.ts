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
import { computed } from 'vue'
import { AppRole } from '@/types/enums/index.ts'

export function useAppRights(
  structureRights: Ref<AppRole[] | undefined>,
) {
  const appRights = computed(() => new Set(structureRights.value ?? []))

  const hasRole = (role: AppRole) => computed(() => appRights.value.has(role))

  const hasAnyRole = (...roles: AppRole[]) => computed(() => (
    roles.some(role => appRights.value.has(role))
  ))

  // GLC
  const canReadGLC = hasRole(AppRole.READ_GLC)
  const canWriteGLC = hasRole(AppRole.WRITE_GLC)
  const canGLC = hasAnyRole(
    AppRole.READ_GLC,
    AppRole.WRITE_GLC,
  )

  // Rentree
  const canReadRentree = hasRole(AppRole.READ_RENTREE)
  const canWriteRentree = hasRole(AppRole.WRITE_RENTREE)
  const canRentree = hasAnyRole(
    AppRole.READ_RENTREE,
    AppRole.WRITE_RENTREE,
  )

  // ParamEtab
  const canReadParamEtab = hasRole(AppRole.READ_PARAMETAB)
  const canWriteParamEtab = hasRole(AppRole.WRITE_PARAMETAB)
  const canParamEtab = hasAnyRole(
    AppRole.READ_PARAMETAB,
    AppRole.WRITE_PARAMETAB,
  )

  // Group
  const canReadGroup = hasRole(AppRole.READ_GROUP)
  const canWriteGroup = hasRole(AppRole.WRITE_GROUP)
  const canGroup = hasAnyRole(
    AppRole.READ_GROUP,
    AppRole.WRITE_GROUP,
  )

  // Esidoc
  const canEsidoc = hasRole(AppRole.ESIDOC)

  // Uid
  const canViewUid = hasRole(AppRole.VIEW_UID)
  const canSearchUid = hasRole(AppRole.SEARCH_UID)

  // Attach
  const canAttach = hasRole(AppRole.ATTACH)

  return {
    hasRole,
    hasAnyRole,
    canReadGLC,
    canWriteGLC,
    canGLC,
    canReadRentree,
    canWriteRentree,
    canRentree,
    canReadParamEtab,
    canWriteParamEtab,
    canParamEtab,
    canReadGroup,
    canWriteGroup,
    canGroup,
    canEsidoc,
    canViewUid,
    canSearchUid,
    canAttach,
  }
}
