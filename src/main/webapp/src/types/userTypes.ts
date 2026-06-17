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

import type {
  CategoriePersonne,
  CategorieRelation,
  Etat,
} from './enums/index.ts'
import type { UserStructure } from './structureTypes.ts'

export interface CommonUser {
  id: number
  etat: Etat
  local: boolean
}

export type FunctionUser = CommonUser & {
  cn: string
  dateSuppression: string
}

export type RelationUser = FunctionUser

export type SearchUser = CommonUser & {
  cn: string
  uid?: string
  email?: string
  dateSuppression: string
}

export type IncertainUser = SearchUser

export type AccountUser = CommonUser & {
  nom: string
  prenom: string
  uid?: string
  categoriePersonne: CategoriePersonne
  login: string
  guichet?: string
  email?: string
  dateSuppression?: string
  dateModificationSource: string
}

export type User = CommonUser & {
  anneeScolaire: string
  categorie: CategoriePersonne
  civilite: string
  source: string
  dateNaissance: string
  email?: string
  givenName: string
  patronyme: string
  cn: string
  sn: string
  uid?: string
  password?: string
  dateFin?: string
  dateSourceModification: string
  login: string
  guichet?: string
  dateModification?: string
  dateAcquittement?: string
  dateSuppression?: string
  photo?: string
  idPronote?: string
  listeRouge: boolean
  listeStructures: UserStructure[]
  relations: UserRelation[]
}

export interface UserRelation {
  categorieRelation: CategorieRelation
  personneEnRelation: RelationUser
  holder: boolean
}
