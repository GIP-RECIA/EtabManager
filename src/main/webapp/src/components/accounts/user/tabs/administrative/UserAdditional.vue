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
  UserFunction,
  UserStructure,
} from '@/types/index.ts'
import { faPlus } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { usePossibleFunctionsQuery } from '@/services/queries/index.ts'
import UserFunctions from './UserFunctions.vue'

const props = defineProps<{
  structure: UserStructure
}>()

const emit = defineEmits<{
  editFunction: [
    structure: UserStructure,
    fonction: FunctionForm | undefined,
  ]
}>()

const { t } = useI18n()

function addFunction(structure: UserStructure): void {
  editFunction(structure, undefined)
}

function editFunction(
  structure: UserStructure,
  fonction: FunctionForm | undefined,
): void {
  emit('editFunction', structure, fonction)
}

const structureIdRef = computed<number>(() => (
  props.structure.id ?? Number.NaN
))

const {
  data: possibleFunctions,
} = usePossibleFunctionsQuery(structureIdRef)

const disabledFonctions = computed<UserFunction[]>(() => (
  [
    ...props.structure.fonctions,
    ...props.structure.additionalFonctions,
  ]
))

const disabled = computed<FunctionForm[]>(() => {
  const data: FunctionForm[] = []
  disabledFonctions.value.forEach(({ id, disciplines }) => {
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
</script>

<template>
  <div class="r-card">
    <header>
      <h3>
        {{ t('page.user.administrative.function.additional', 2) }}
      </h3>
    </header>

    <div class="body">
      <UserFunctions
        :fonctions="structure.additionalFonctions"
        :clickable="
          structure.authorizedForPrincipal
            && !!filteredFilieres
        "
        @tag-click="(fun) => editFunction(structure, fun)"
      />
    </div>

    <footer
      v-if="
        structure.authorizedForPrincipal
          && possibleFunctions?.fonctions
          && possibleFunctions.fonctions.length > 0
      "
    >
      <button
        type="button"
        class="btn-primary small"
        :disabled="
          filteredFilieres
            && filteredFilieres.length === 0
        "
        @click="addFunction(structure)"
      >
        {{ t('button.add') }}
        <FontAwesomeIcon
          :icon="faPlus"
        />
      </button>
    </footer>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;
</style>
