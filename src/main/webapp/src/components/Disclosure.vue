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
import { faChevronDown } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed, ref, useId } from 'vue'

const uid = useId()

const id = computed<string>(() => (
  `disclosure-${uid}`
))

const isRendered = ref<boolean>(false)

const isExpanded = ref<boolean>(false)

function toggle(): void {
  isExpanded.value = !isExpanded.value
  if (isExpanded.value)
    isRendered.value = true
}
</script>

<template>
  <div class="disclosure">
    <header>
      <button
        type="button"
        :aria-expanded="isExpanded"
        :aria-controls="id"
        @click="toggle"
      >
        <slot name="heading" />
        <FontAwesomeIcon
          :icon="faChevronDown"
          :style="{
            rotate: isExpanded ? '180deg' : undefined,
          }"
          class="folded-indicator"
        />
      </button>
    </header>
    <div
      :id="id"
      class="menu"
      :style="{
        display: isExpanded ? undefined : 'none',
      }"
    >
      <slot v-if="isRendered" />
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.disclosure {
  border-radius: 10px;
  // box-shadow: var(--#{$prefix}shadow-strong) HEXToRGBA($black, 0.1);

  > header {
    > button {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      // height: 60px;
      width: 100%;
      padding: 12px 16px;
      text-align: start;
      font-size: var(--#{$prefix}font-size-md);
      font-weight: bold;
      color: inherit;

      > .folded-indicator {
        font-size: 16px;
        margin: 8px;
        color: var(--#{$prefix}basic-black);
        transition: rotate 0.2s ease-in-out;
      }

      &:hover,
      &:focus-visible {
        outline: none;

        > .heading {
          color: var(--#{$prefix}primary);
        }
      }
    }
  }

  &:has(> header > button:focus-visible) {
    outline: 2px solid var(--#{$prefix}primary);
  }
}
</style>
