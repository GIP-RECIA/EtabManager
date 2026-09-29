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
import type { FunctionForm, User, UserStructure } from '@/types/index.ts'
import { faHouseUser, faLandmark } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useUserRights } from '@/composables/index.ts'
import UserAdditional from './administrative/UserAdditional.vue'
import UserFunctions from './administrative/UserFunctions.vue'

const props = defineProps<{
  user?: User
}>()

const emit = defineEmits<{
  editFunction: [
    structure: UserStructure,
    fonction: FunctionForm | undefined,
  ]
}>()

const { t } = useI18n()

const userRef = computed<User | undefined>(() => props.user)

const {
  canSeeFunctions,
  canSeeClassesAndGroups,
} = useUserRights(userRef)

function editFunction(
  structure: UserStructure,
  fonction: FunctionForm | undefined,
): void {
  emit('editFunction', structure, fonction)
}
</script>

<template>
  <div>
    <div
      v-for="structure in user?.listeStructures"
      :key="`user-administrative-structure-${structure.id}`"
      class="structure-functions"
    >
      <div class="heading">
        <h2>
          {{ structure.nom }}
        </h2>
        <p v-show="structure.type || structure.uai || structure.siren">
          {{ structure.type }}
          <span v-show="structure.uai || structure.siren">
            {{ structure.uai ?? structure.siren }}
          </span>
        </p>
        <p
          v-if="
            structure.structureRattachement
              || structure.structureCourante
          "
          class="icons"
        >
          <span
            v-show="structure.structureRattachement"
            :title="t('page.user.structure.admin')"
          >
            <FontAwesomeIcon
              :icon="faLandmark"
              size="lg"
            />
          </span>
          <span
            v-show="structure.structureCourante"
            :title="t('page.user.structure.current')"
            class="current"
          >
            <FontAwesomeIcon
              :icon="faHouseUser"
              size="lg"
            />
          </span>
        </p>
      </div>

      <template v-if="canSeeFunctions">
        <div class="r-card">
          <header>
            <h3>
              {{ t('page.user.administrative.function.header', 2) }}
            </h3>
          </header>

          <div class="body">
            <UserFunctions
              :fonctions="structure.fonctions"
            />
          </div>
        </div>

        <UserAdditional
          :structure="structure"
          @edit-function="editFunction"
        />
      </template>

      <template v-if="canSeeClassesAndGroups">
        <div class="r-card">
          <header>
            <h3>
              {{ t('page.user.administrative.class', 2) }}
            </h3>
          </header>

          <div class="body">
            <ul v-if="structure.classes.length > 0">
              <li
                v-for="uClass in structure.classes"
                :key="`class-${uClass}`"
                class="tag-primary"
              >
                {{ uClass }}
              </li>
            </ul>
          </div>
        </div>

        <div class="r-card">
          <header>
            <h3>
              {{ t('page.user.administrative.educationalGroup', 2) }}
            </h3>
          </header>

          <div class="body">
            <ul
              v-if="structure.groupesPedagogiques.length > 0"
            >
              <li
                v-for="group in structure.groupesPedagogiques"
                :key="`educational-group${group}`"
                class="tag-primary"
              >
                {{ group }}
              </li>
            </ul>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.structure-functions {
  display: grid;
  gap: 16px;

  > .heading {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;

    > h2 {
      margin-bottom: 0;
    }

    > p:not(.icons) {
      opacity: 0.6;
      font-size: var(--#{$prefix}font-size-sm);
    }

    > p.icons {
      font-size: var(--#{$prefix}font-size-sm);
    }
  }

  > :deep(.r-card) > .body {
    display: flex;
    flex-direction: column;
    gap: 16px;

    > ul {
      @include unstyled-list;
      display: flex;
      flex-wrap: wrap;
      gap: 7px 8px;
    }
  }

  @media (width >= map.get($grid-breakpoints, lg)) {
    grid-template-columns: repeat(2, 1fr);
    align-items: start;

    > .heading {
      grid-column: span 2;
    }
  }
}
</style>
