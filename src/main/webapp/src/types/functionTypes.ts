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

import type { endInfo } from './endInfoTypes.ts'
import type { FunctionUser } from './userTypes.ts'

export interface FunctionDate {
  dateDebut?: string
  dateFin?: string
}

export interface CommonFiliere {
  id: number
  libelle: string
}

export interface CommonDiscipline {
  id: number
  libelle: string
}

export type Composition = CommonFiliere & {
  disciplines: DisciplineWithUsers[]
  personnesWithoutDiscipline: FunctionUser[]
}

export type DisciplineWithUsers = CommonDiscipline & {
  personnes: FunctionUser[]
}

export type UserDisciplineWithDate = CommonDiscipline
  & FunctionDate

export type UserDisciplineWithDateAndEndInfo = CommonDiscipline
  & FunctionDate & {
    endInfo?: endInfo
  }

export interface StructurePossibleFunctions {
  dateFinDefaut: string
  fonctions: PossibleFunction[]
}

export type PossibleFunction = CommonFiliere & {
  disciplines: CommonDiscipline[]
}

export type UserFunction = CommonFiliere & {
  disciplines: UserDisciplineWithDate[]
}

export type UserFunctionExtended = CommonFiliere & {
  disciplines: UserDisciplineWithDateAndEndInfo[]
}

export type FunctionForm = {
  filiere?: number
  discipline?: number
} & FunctionDate

export interface SetUserFunctionParams {
  id: number
  structureId: number
  toAddFunctions: FunctionForm[]
  toDeleteFunctions: FunctionForm[]
  requiredAction: string
}

export type AddUserFunctionParams = Omit<
  SetUserFunctionParams,
  'toAddFunctions'
  | 'toDeleteFunctions'
> & {
  toAddFunction: FunctionForm
}

export type RemoveUserFunctionParams = Omit<
  SetUserFunctionParams,
  'toAddFunctions'
  | 'toDeleteFunctions'
> & {
  toDeleteFunction: FunctionForm
}
