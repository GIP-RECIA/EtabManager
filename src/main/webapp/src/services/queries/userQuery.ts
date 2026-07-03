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
import type { ToastContainerOptions } from 'vue3-toastify'
import type { User } from '@/types/index.ts'
import {
  defineQueryOptions,
  useMutation,
  useQuery,
  useQueryCache,
} from '@pinia/colada'
import { computed } from 'vue'
import { toast } from 'vue3-toastify'
import { useRoute } from 'vue-router'
import i18n from '@/plugins/i18n.ts'
import {
  addUserFunction,
  deleteUser,
  forceDeleteUser,
  getUser,
  lockUser,
  removeUserFunction,
  resetUser,
  searchUser,
  undoDeleteUser,
  unlockUser,
  unlockUsers,
} from '@/services/api/index.ts'
import { Etat } from '@/types/enums'
import { errorHandler } from '@/utils/index.ts'

const { t } = i18n.global

function useUserQuery() {
  const route = useRoute()
  const userId = computed(() => Number(route.params.userId))

  return useQuery(() => ({
    key: ['user', userId.value],
    query: () => getUser(userId.value),
    enabled: !Number.isNaN(userId.value),
    staleTime: 1000 * 60 * 30,
  }))
}

function useUserQueryOptions(userId: number) {
  return defineQueryOptions({
    key: ['user', userId],
    query: () => getUser(userId),
    enabled: !Number.isNaN(userId),
    staleTime: 1000 * 60 * 30,
  })
}

function useDeleteUserMutation() {
  const queryCache = useQueryCache()

  return useMutation({
    mutation: deleteUser,
    onMutate(vars, _context) {
      const oldUser = queryCache.getQueryData<User>(['user', vars])

      if (oldUser) {
        const newData = oldUser
        newData.etat = Etat.Delete

        queryCache.setQueryData<User | undefined>(
          ['user', vars],
          newData,
        )
      }

      return { oldUser }
    },
    onSuccess(_data, _vars, _context) {
      toast.success(
        t('toast.deleteUser.success'),
        {
        } as ToastContainerOptions,
      )
    },
    onError(error, _vars, _context) {
      errorHandler(error, t('toast.deleteUser.error'))
    },
    onSettled(_data, _error, vars, context) {
      queryCache.invalidateQueries({ key: ['user', vars] })
      context.oldUser?.listeStructures.forEach((structure) => {
        queryCache.invalidateQueries({ key: ['structure', structure.id] })
      })
    },
  })
}

function useForceDeleteUserMutation() {
  const queryCache = useQueryCache()

  return useMutation({
    mutation: forceDeleteUser,
    onMutate(vars, _context) {
      const oldUser = queryCache.getQueryData<User>(['user', vars])

      if (oldUser) {
        const newData = oldUser
        newData.etat = Etat.Delete

        queryCache.setQueryData<User | undefined>(
          ['user', vars],
          newData,
        )
      }

      return { oldUser }
    },
    onSuccess(_data, _vars, _context) {
      toast.success(
        t('toast.forceDeleteUser.success'),
        {
        } as ToastContainerOptions,
      )
    },
    onError(error, _vars, _context) {
      errorHandler(error, t('toast.forceDeleteUser.error'))
    },
    onSettled(_data, _error, vars, context) {
      queryCache.invalidateQueries({ key: ['user', vars] })
      context.oldUser?.listeStructures.forEach((structure) => {
        queryCache.invalidateQueries({ key: ['structure', structure.id] })
      })
    },
  })
}

function useUndoDeleteUserMutation() {
  const queryCache = useQueryCache()

  return useMutation({
    mutation: undoDeleteUser,
    onMutate(vars, _context) {
      const oldUser = queryCache.getQueryData<User>(['user', vars])

      if (oldUser) {
        const newData = oldUser
        newData.etat = Etat.Valide

        queryCache.setQueryData<User | undefined>(
          ['user', vars],
          newData,
        )
      }

      return { oldUser }
    },
    onSuccess(_data, _vars, _context) {
      toast.success(
        t('toast.undoDeleteUser.success'),
        {
        } as ToastContainerOptions,
      )
    },
    onError(error, _vars, _context) {
      errorHandler(error, t('toast.undoDeleteUser.error'))
    },
    onSettled(_data, _error, vars, context) {
      queryCache.invalidateQueries({ key: ['user', vars] })
      context.oldUser?.listeStructures.forEach((structure) => {
        queryCache.invalidateQueries({ key: ['structure', structure.id] })
      })
    },
  })
}

function useResetUserMutation() {
  const queryCache = useQueryCache()

  return useMutation({
    mutation: resetUser,
    onMutate(vars, _context) {
      const oldUser = queryCache.getQueryData<User>(['user', vars])

      if (oldUser) {
        const newData = oldUser
        newData.etat = Etat.Invalide

        queryCache.setQueryData<User | undefined>(
          ['user', vars],
          newData,
        )
      }

      return { oldUser }
    },
    onSuccess(_data, _vars, _context) {
      toast.success(
        t('toast.resetUser.success'),
        {
        } as ToastContainerOptions,
      )
    },
    onError(error, _vars, _context) {
      errorHandler(error, t('toast.resetUser.error'))
    },
    onSettled(_data, _error, vars, context) {
      queryCache.invalidateQueries({ key: ['user', vars] })
      context.oldUser?.listeStructures.forEach((structure) => {
        queryCache.invalidateQueries({ key: ['structure', structure.id] })
      })
    },
  })
}

function useLockUserMutation() {
  const queryCache = useQueryCache()

  return useMutation({
    mutation: lockUser,
    onMutate(vars, _context) {
      const oldUser = queryCache.getQueryData<User>(['user', vars])

      if (oldUser) {
        const newData = oldUser
        newData.etat = Etat.Bloque

        queryCache.setQueryData<User | undefined>(
          ['user', vars],
          newData,
        )
      }

      return { oldUser }
    },
    onSuccess(_data, _vars, _context) {
      toast.success(
        t('toast.lockUser.success'),
        {
        } as ToastContainerOptions,
      )
    },
    onError(error, _vars, _context) {
      errorHandler(error, t('toast.lockUser.error'))
    },
    onSettled(_data, _error, vars, context) {
      queryCache.invalidateQueries({ key: ['user', vars] })
      context.oldUser?.listeStructures.forEach((structure) => {
        queryCache.invalidateQueries({ key: ['structure', structure.id] })
      })
    },
  })
}

function useUnlockUserMutation() {
  const queryCache = useQueryCache()

  return useMutation({
    mutation: unlockUser,
    onMutate(vars, _context) {
      const oldUser = queryCache.getQueryData<User>(['user', vars])

      if (oldUser) {
        const newData = oldUser
        newData.etat = Etat.Valide

        queryCache.setQueryData<User | undefined>(
          ['user', vars],
          newData,
        )
      }

      return { oldUser }
    },
    onSuccess(_data, _vars, _context) {
      toast.success(
        t('toast.unlockUser.success'),
        {
        } as ToastContainerOptions,
      )
    },
    onError(error, _vars, _context) {
      errorHandler(error, t('toast.unlockUser.error'))
    },
    onSettled(_data, _error, vars, context) {
      queryCache.invalidateQueries({ key: ['user', vars] })
      context.oldUser?.listeStructures.forEach((structure) => {
        queryCache.invalidateQueries({ key: ['structure', structure.id] })
      })
    },
  })
}

function useUnlockUsersMutation() {
  const queryCache = useQueryCache()

  return useMutation({
    mutation: unlockUsers,
    onSuccess(_data, _vars, _context) {
      toast.success(
        t('toast.unlockUsers.success'),
        {
        } as ToastContainerOptions,
      )
    },
    onError(error, _vars, _context) {
      errorHandler(error, t('toast.unlockUsers.error'))
    },
    onSettled: (_data, _error, vars, _context) => {
      vars.forEach((userId) => {
        queryCache.invalidateQueries({ key: ['user', userId] })
        queryCache.invalidateQueries({ key: ['structure'] })
      })
    },
  })
}

function useSearchUserQuery(
  name: Ref<string>,
  params?: {
    etab?: string
    not_in_etab?: number
    staff?: boolean
    check_rights?: boolean
  },
) {
  return useQuery(() => ({
    key: ['user', 'search', name.value],
    query: () => searchUser(name.value, params),
    enabled: !!name.value,
    staleTime: 1000 * 60 * 5,
  }))
}

function useAddUserFunctionMutation() {
  const queryCache = useQueryCache()

  return useMutation({
    mutation: addUserFunction,
    onSuccess(_data, vars, _context) {
      queryCache.invalidateQueries({ key: ['structure', vars.structureId] })
      toast.success(
        t(`toast.additional.success.${vars.requiredAction}`),
        {
        } as ToastContainerOptions,
      )
    },
    onError(error, vars, _context) {
      errorHandler(error, t(`toast.additional.error.${vars.requiredAction}`))
    },
    onSettled: (_data, _error, vars, _context) => {
      queryCache.invalidateQueries({ key: ['user', vars.id] })
    },
  })
}

function useRemoveUserFunctionMutation() {
  const queryCache = useQueryCache()

  return useMutation({
    mutation: removeUserFunction,
    onSuccess(_data, vars, _context) {
      queryCache.invalidateQueries({ key: ['structure', vars.structureId] })
      const key = vars.requiredAction === 'save'
        ? 'delete'
        : vars.requiredAction
      toast.success(
        t(`toast.additional.success.${key}`),
        {
        } as ToastContainerOptions,
      )
    },
    onError(error, vars, _context) {
      const key = vars.requiredAction === 'save'
        ? 'delete'
        : vars.requiredAction
      errorHandler(error, t(`toast.additional.error.${key}`))
    },
    onSettled: (_data, _error, vars, _context) => {
      queryCache.invalidateQueries({ key: ['user', vars.id] })
    },
  })
}

export {
  useAddUserFunctionMutation,
  useDeleteUserMutation,
  useForceDeleteUserMutation,
  useLockUserMutation,
  useRemoveUserFunctionMutation,
  useResetUserMutation,
  useSearchUserQuery,
  useUndoDeleteUserMutation,
  useUnlockUserMutation,
  useUnlockUsersMutation,
  useUserQuery,
  useUserQueryOptions,
}
