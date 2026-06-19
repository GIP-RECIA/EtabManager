<!--
 Copyright (C) 2023 GIP-RECIA, Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

<script setup lang="ts">
import type {
  CommonDiscipline,
  FunctionForm,
  PossibleFunction,
  SearchStructure,
  SearchUser,
  User,
  UserFunction,
} from '@/types/index.ts'
import {
  faFloppyDisk,
  faLink,
  faLinkSlash,
  faTrashCan,
  faXmark,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import {
  useAddUserFunctionMutation,
  usePossibleFunctionsQuery,
  useRemoveUserFunctionMutation,
} from '@/services/queries/index.ts'
import { toISODate } from '@/utils/index.ts'
import FunctionSelect from './manageAdditional/FunctionSelect.vue'
import StructureSelect from './manageAdditional/StructureSelect.vue'
import UserSelect from './manageAdditional/UserSelect.vue'

const props = defineProps<{
  title: string
  user?: User
  structureId?: number
  disabledFonctions?: UserFunction[]
  editFonction?: FunctionForm
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const modelValue = defineModel<boolean>({ required: true })

const { t } = useI18n()

const isDev = import.meta.env.DEV

/* User and structure */

const selectedUser = ref<SearchUser>()
const selectedStructure = ref<SearchStructure>()

const userStuctures = computed<number[] | undefined>(() => (
  props.user?.listeStructures.map(({ id }) => id)
))

const mandatory = computed(() => ({
  userId: selectedUser.value?.id ?? props.user?.id,
  structureId: selectedStructure.value?.id ?? props.structureId,
}))

/* Possible functions */

const structureIdRef = computed<number>(() => (
  mandatory.value.structureId ?? Number.NaN
))

const {
  data: possibleFunctions,
} = usePossibleFunctionsQuery(structureIdRef)

/* Filiere, discipline and dates */

const EMPTY_FUNCTION: FunctionForm = {
  filiere: undefined,
  discipline: undefined,
  dateDebut: undefined,
  dateFin: undefined,
}

const fields = ref<FunctionForm>({ ...EMPTY_FUNCTION })

function initializeFields(): void {
  if (props.editFonction) {
    fields.value = {
      ...props.editFonction,
      dateDebut: toISODate(props.editFonction.dateDebut),
      dateFin: toISODate(props.editFonction.dateFin),
    }
  }
  else if (possibleFunctions.value) {
    fields.value = {
      ...fields.value,
      dateFin: possibleFunctions.value.dateFinDefaut,
    }
  }
}

watch(
  () => props.editFonction,
  initializeFields,
)

watch(
  possibleFunctions,
  initializeFields,
  { immediate: true },
)

watch(
  () => modelValue.value,
  (val) => {
    if (val) {
      initializeFields()
      return
    }

    setTimeout(() => {
      selectedUser.value = undefined
      selectedStructure.value = undefined
      fields.value = { ...EMPTY_FUNCTION }
    }, 300)
  },
)

/* Filieres */

const disabled = computed<FunctionForm[]>(() => {
  const data: FunctionForm[] = []
  if (props.editFonction)
    return data

  props.disabledFonctions?.forEach(({ id, disciplines }) => {
    data.push(...disciplines.map(discipline => ({
      filiere: id,
      discipline: discipline.id,
      dateDebut: discipline.dateDebut,
      dateFin: discipline.dateFin,
    })))
  })

  return data
})

const filteredFilieres = computed<PossibleFunction[] | undefined>(() => {
  if (!disabled.value)
    return possibleFunctions.value?.fonctions

  return possibleFunctions.value
    ?.fonctions
    ?.map((filiere) => {
      const disciplines: CommonDiscipline[] = filiere.disciplines.filter(
        discipline => !disabled.value
          .map(({ filiere: fi, discipline: di }) => (
            `${fi}-${di}`
          ))
          .includes(
            `${filiere.id}-${discipline.id}`,
          ),
      )

      return { ...filiere, disciplines }
    })
    .filter(({ disciplines }) => disciplines.length > 0)
})

/* Actions */

const isAttach = computed<boolean>(() => (
  !props.user
  || !props.structureId
))

function functionsCount(functions: UserFunction[]): number {
  return functions.reduce(
    (acc, value) => acc + value.disciplines.length,
    0,
  )
}

const isDetach = computed<boolean>(() => {
  if (isAttach.value || !props.editFonction)
    return false

  const structure = props.user!.listeStructures
    .find(({ id }) => id === props.structureId)

  return (
    !!structure
    && functionsCount(structure.fonctions) === 0
    && functionsCount(structure.additionalFonctions) === 1
  )
})

const requiredAction = computed<string>(() => (
  isAttach.value
    ? 'attach'
    : isDetach.value
      ? 'detach'
      : 'save'
))

const canSave = computed<boolean>(() => {
  const {
    userId,
    structureId,
  } = mandatory.value
  const {
    filiere,
    discipline,
    dateDebut,
    dateFin,
  } = fields.value

  return !!(
    userId
    && structureId
    && filiere
    && discipline
    && (isDev ? dateDebut : true)
    && dateFin
  )
})

const {
  mutate: removeFunction,
} = useRemoveUserFunctionMutation()

async function remove(): Promise<void> {
  const {
    userId,
    structureId,
  } = mandatory.value
  if (!userId || !structureId)
    return

  removeFunction({
    id: userId,
    structureId,
    toDeleteFunction: fields.value,
    requiredAction: requiredAction.value,
  })
  emit('update:modelValue', false)
}

function close(): void {
  emit('update:modelValue', false)
}

const {
  mutate: addFunction,
} = useAddUserFunctionMutation()

async function save(): Promise<void> {
  const {
    userId,
    structureId,
  } = mandatory.value
  if (!userId || !structureId)
    return

  addFunction({
    id: userId,
    structureId,
    toAddFunction: fields.value,
    requiredAction: requiredAction.value,
  })
  emit('update:modelValue', false)
}
</script>

<template>
  <v-dialog
    v-model="modelValue"
    scrollable
    :max-width="1024"
  >
    <v-card rounded="xl">
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title>
          <h1>
            {{ title }}
          </h1>
        </v-toolbar-title>
        <template #append>
          <v-btn
            icon="fas fa-xmark"
            color="default"
            variant="plain"
            class="me-1"
            @click="close"
          />
        </template>
      </v-toolbar>

      <v-card-text class="pt-0 py-3">
        <div class="form-layout">
          <UserSelect
            v-if="!user"
            v-model="selectedUser"
            :structure-id="structureId"
            class="full-width"
          />

          <StructureSelect
            v-if="!structureId"
            v-model="selectedStructure"
            :exclude="userStuctures"
            class="full-width"
          />

          <FunctionSelect
            v-model="fields"
            :possible="filteredFilieres"
            :disable-fonction-edit="!!editFonction"
          />
        </div>
      </v-card-text>

      <v-card-actions>
        <button
          v-show="user && structureId"
          type="button"
          :disabled="!editFonction"
          class="btn-secondary"
          @click="remove"
        >
          {{ t(`button.${isDetach ? 'detach' : 'delete'}`) }}
          <FontAwesomeIcon
            :icon="isDetach ? faLinkSlash : faTrashCan"
          />
        </button>
        <div class="grow-1" />
        <button
          type="button"
          class="btn-secondary"
          @click="close"
        >
          {{ t('button.cancel') }}
          <FontAwesomeIcon
            :icon="faXmark"
          />
        </button>
        <button
          type="button"
          :disabled="!canSave"
          class="btn-primary"
          @click="save"
        >
          {{ t(`button.${isAttach ? 'attach' : 'save'}`) }}
          <FontAwesomeIcon
            :icon="isAttach ? faLink : faFloppyDisk"
          />
        </button>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

h1 {
  margin-bottom: 0;
}

.form-layout {
  display: grid;
  gap: 16px;

  @media (width >= map.get($grid-breakpoints, md)) {
    grid-template-columns: repeat(2, 1fr);

    > .full-width {
      grid-column: span 2;
    }
  }
}
</style>
