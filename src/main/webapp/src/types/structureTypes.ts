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

import type { Alert } from './alertTypes.ts'
import type { AppRole } from './enums/AppRole.ts'
import type { Composition, UserFunction } from './functionTypes.ts'
import type { Incertain } from './incertainTypes.ts'
import type { AccountUser, FunctionUser } from './userTypes.ts'

export interface CommonStructure {
  id: number
  type?: string
  uai?: string
  siren: string
  nom: string
}

export type SearchStructure = CommonStructure & {
  ville?: string
}

export type UserStructure = CommonStructure & {
  structureRattachement: boolean
  structureCourante: boolean
  fonctions: UserFunction[]
  additionalFonctions: UserFunction[]
  classes: string[]
  groupesPedagogiques: string[]
  authorizedForPrincipal: boolean
}

export type Structure = CommonStructure & {
  nomCourt?: string
  etat: string
  etatAlim: string
  source: string
  anneeScolaire: string
  adresse: {
    adresse: string
    codePostal: string
    ville: string
    boitePostale: string
    pays: string
  }
  categorie: string
  mail: string
  siteWeb: string
  modeleLogin: string
  logo: string
  personnes: AccountUser[]
  filieres: Composition[]
  withoutFunctions?: FunctionUser[]
  alerts: Alert[]
  incertains: Incertain[]
  permissions: AppRole[]
}
