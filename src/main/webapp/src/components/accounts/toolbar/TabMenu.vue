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
import type { RouteLocationAsRelativeGeneric } from 'vue-router'
import { onMounted, onUnmounted, ref, useTemplateRef } from 'vue'
import TabItem from './TabItem.vue'

interface TabItemT {
  id: string
  name: string
  to: RouteLocationAsRelativeGeneric
}

defineProps<{
  items: TabItemT[]
}>()

defineEmits<{
  removeItem: [id: string]
}>()

const self = useTemplateRef<HTMLElement>('dropdown')
const dropdownButton = useTemplateRef<HTMLButtonElement>('dropdown-button')

const isExpanded = ref<boolean>(false)

onMounted(() => {
  self.value?.addEventListener('keyup', handleKeyPress)
  document.addEventListener('keyup', handleOutsideEvents)
  document.addEventListener('click', handleOutsideEvents)
})

onUnmounted(() => {
  self.value?.removeEventListener('keyup', handleKeyPress)
  document.removeEventListener('keyup', handleOutsideEvents)
  document.removeEventListener('click', handleOutsideEvents)
})

function handleKeyPress(e: KeyboardEvent): void {
  if (isExpanded.value && e.key === 'Escape') {
    e.preventDefault()
    close(e)
  }
}

function handleOutsideEvents(e: KeyboardEvent | MouseEvent): void {
  if (
    isExpanded.value
    && self.value
    && e.target instanceof HTMLElement
    && !(self.value.contains(e.target) || e.composedPath().includes(self.value))
  ) {
    close(undefined, false)
  }
}

function toggle(): void {
  isExpanded.value = !isExpanded.value
}

function close(_: Event | undefined = undefined, resetFocus: boolean = true): void {
  isExpanded.value = false
  if (resetFocus)
    dropdownButton.value?.focus()
}
</script>

<template>
  <div
    ref="dropdown"
    class="dropdown"
  >
    <button
      ref="dropdown-button"
      type="button"
      :aria-expanded="isExpanded"
      aria-controls="dropdown-tab-menu"
      aria-label="Menu onglets"
      :disabled="items.length === 0"
      class="btn-tertiary circle"
      @click="toggle"
    >
      <div>
        {{ items.length < 20 ? items.length : 'X' }}
      </div>
    </button>
    <ul
      id="dropdown-tab-menu"
      :style="{
        display: isExpanded ? undefined : 'none',
      }"
    >
      <li
        v-for="item in items"
        :key="item.id"
      >
        <TabItem
          :id="item.id"
          :name="item.name"
          :to="item.to"
          @click="toggle"
          @close="(id) => $emit('removeItem', id)"
        />
      </li>
    </ul>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.dropdown {
  position: relative;
  width: fit-content;

  > button {
    text-decoration: none !important;

    > div {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      border: 2px solid var(--#{$prefix}basic-black);
      border-radius: 2px;
      width: 18px;
      height: 18px;
      font-size: var(--#{$prefix}font-size-xs);
      font-weight: bold;
    }

    &:hover,
    &:focus-visible {
      > div {
        border-color: var(--#{$prefix}primary);
      }
    }
  }

  > ul {
    @include unstyled-list;
    position: absolute;
    top: calc(100% + 6px);
    left: 0;
    min-width: 100%;
    max-width: calc(100vw - 32px);
    background-color: var(--#{$prefix}body-bg);
    border-radius: 8px;
    box-shadow: var(--#{$prefix}shadow-strong) HEXToRGBA($black, 0.1);
    overflow: hidden;
    z-index: 1000;

    > li > div {
      border-radius: unset;
      box-shadow: unset;
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    > ul > li:not(:last-child) {
      border-bottom: 1px solid HEXToRGBA($black, 0.1);
    }
  }
}
</style>
