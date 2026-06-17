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
  RemoveUserOneAdditionalParams,
  SearchUser,
  SetUserAdditionalParams,
  SetUserOneAdditionalParams,
  User,
} from '@/types/index.ts'
import { instance as axios } from '@/utils/index.ts'

async function getUser(id: number) {
  return (
    await axios.get<User>(
      `/api/personne/${id}`,
    )
  ).data
}

async function deleteUser(id: number) {
  return (
    await axios.delete<void>(
      `/api/personne/${id}`,
    )
  ).status === 200
}

async function forceDeleteUser(id: number) {
  return (
    await axios.delete<void>(
      `/api/personne/${id}/force`,
    )
  ).status === 200
}

async function undoDeleteUser(id: number) {
  return (
    await axios.delete<void>(
      `/api/personne/${id}/undo`,
    )
  ).status === 200
}

async function resetUser(id: number) {
  return (
    await axios.put(
      `/api/personne/${id}/reset`,
    )
  ).status === 200
}

async function lockUser(id: number) {
  return (
    await axios.put<void>(
      `/api/personne/${id}/lock`,
    )
  ).status === 200
}

async function unlockUser(id: number) {
  return (
    await axios.put<void>(
      `/api/personne/${id}/unlock`,
    )
  ).status === 200
}

async function unlockUsers(ids: number[]) {
  return (
    await axios.put<void>(
      `/api/personne/unlock`,
      {
        params: {
          ids,
        },
      },
    )
  ).status === 200
}

async function searchUser(
  name: string,
  params?: {
    etab?: string
    not_in_etab?: number
    staff?: boolean
    check_rights?: boolean
  },
) {
  return (
    await axios.get<SearchUser[]>(
      '/api/personne',
      {
        params: {
          name,
          ...params,
        },
      },
    )
  ).data
}

async function setUserAdditional({
  id,
  structureId,
  toAddFunctions,
  toDeleteFunctions,
  requiredAction,
}: SetUserAdditionalParams) {
  return !!(
    await axios.post<void>(
      `/api/personne/${id}/fonction`,
      {
        structureId,
        toAddFunctions,
        toDeleteFunctions,
        requiredAction,
      },
    )
  )
}

function setUserOneAdditional({
  id,
  structureId,
  toAddFunction,
  requiredAction,
}: SetUserOneAdditionalParams) {
  return setUserAdditional({
    id,
    structureId,
    toAddFunctions: [toAddFunction],
    toDeleteFunctions: [],
    requiredAction,
  })
}

function removeUserOneAdditional({
  id,
  structureId,
  toDeleteFunction,
  requiredAction,
}: RemoveUserOneAdditionalParams) {
  return setUserAdditional({
    id,
    structureId,
    toAddFunctions: [],
    toDeleteFunctions: [toDeleteFunction],
    requiredAction,
  })
}

export {
  deleteUser,
  forceDeleteUser,
  getUser,
  lockUser,
  removeUserOneAdditional,
  resetUser,
  searchUser,
  setUserAdditional,
  setUserOneAdditional,
  undoDeleteUser,
  unlockUser,
  unlockUsers,
}
