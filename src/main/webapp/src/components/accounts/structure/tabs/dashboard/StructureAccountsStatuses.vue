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
import type { Etat } from '@/types/enums/index.ts'
import type { Structure } from '@/types/index.ts'
import { faUser } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { etatFilters, etatMap } from '@/types/enums/index.ts'

const props = defineProps<{
  structure?: Structure
}>()

defineEmits<{
  selectedState: [Etat]
}>()

const isDev = import.meta.env.DEV

const { t } = useI18n()

const accountStates = computed(() => (
  etatFilters.map(etat => ({
    etat,
    icon: faUser,
    count: props.structure?.personnes.filter(personne => personne.etat === etat).length ?? 0,
    ...etatMap[etat],
  }))
))
</script>

<template>
  <div class="accounts-states">
    <h2>
      {{ t('page.structure.dashboard.accountsStatuses') }}
    </h2>

    <ul>
      <li
        v-for="etat in accountStates"
        :key="`account-state-${etat.etat}`"
      >
        <button
          type="button"
          :disabled="!isDev"
          @click="$emit('selectedState', etat.etat)"
        >
          <div>
            <span class="count">
              {{ etat.count?.toLocaleString() }}
            </span>
            <span class="status">
              {{ t(etat.i18n) }}
            </span>
          </div>
          <FontAwesomeIcon
            :icon="etat.icon"
            size="3x"
            :style="{
              color: etat.color,
            }"
          />
        </button>
      </li>
    </ul>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.accounts-states {
  > ul {
    @include unstyled-list;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    grid-auto-rows: 1fr;
    gap: 16px;

    > li > button {
      display: flex;
      align-items: center;
      gap: 8px;
      border-radius: 10px;
      box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);
      outline-color: transparent;
      outline-offset: -1px;
      padding: 12px;
      width: 100%;
      height: 100%;
      transition:
        outline 0.15s ease-out,
        box-shadow 0.15s ease-out;

      > div {
        flex: 1 1 auto;
        display: flex;
        flex-direction: column;

        > .count {
          font-size: var(--#{$prefix}font-size-xxl);
        }

        > .status {
          font-size: var(--#{$prefix}font-size-sm);
          opacity: 0.6;
        }
      }

      > svg {
        flex: 0 0 auto;
      }

      &:not(:disabled) {
        &:hover,
        &:focus-visible {
          outline: 2px solid var(--#{$prefix}primary);
          box-shadow: var(--#{$prefix}shadow-hover) HEXToRGBA(var(--#{$prefix}primary), 0.2);
        }
      }
    }
  }
}
</style>
