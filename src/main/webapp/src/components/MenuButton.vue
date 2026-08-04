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
import {
  computed,
  onMounted,
  onUnmounted,
  ref,
  useId,
  useTemplateRef,
} from 'vue'

withDefaults(
  defineProps<{
    items: {
      uid: string | number
      name: string
    }[]
    disabled?: boolean
    aLabel?: string
    btnTitle?: string
    btnClass?: string
  }>(),
  {
    disabled: false,
  },
)

const emit = defineEmits<{
  'update:modelValue': [uid: string | number]
}>()

const self = useTemplateRef<HTMLElement>('dropdown')
const dropdownButton = useTemplateRef<HTMLButtonElement>('dropdown-button')

const uid = useId()

const id = computed<string>(() => (
  `dropdown-${uid}`
))

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

function close(
  _: Event | undefined = undefined,
  resetFocus: boolean = true,
): void {
  isExpanded.value = false
  if (resetFocus)
    dropdownButton.value?.focus()
}

function handleItemClick(key: string | number) {
  close()
  emit('update:modelValue', key)
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
      :aria-controls="id"
      :aria-label="aLabel"
      :title="btnTitle"
      :disabled="disabled"
      :class="btnClass"
      @click="toggle"
    >
      <slot />
    </button>
    <ul
      :id="id"
      :style="{
        display: isExpanded ? undefined : 'none',
      }"
    >
      <li
        v-for="item in items"
        :key="item.uid"
      >
        <button
          type="button"
          @click="handleItemClick(item.uid)"
        >
          {{ item.name }}
        </button>
      </li>
    </ul>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.dropdown {
  position: relative;
  width: fit-content;

  > ul {
    @include unstyled-list;
    position: absolute;
    top: calc(100% + 6px);
    right: 0;
    min-width: 100%;
    max-width: calc(100vw - 32px);
    background-color: var(--#{$prefix}body-bg);
    border-radius: 8px;
    box-shadow: var(--#{$prefix}shadow-strong) HEXToRGBA($black, 0.1);
    overflow: hidden;
    z-index: 1000;

    > li > button {
      @include unstyled-link;
      display: inline-flex;
      align-items: center;
      padding: 14px 12px;
      text-wrap: nowrap;
      font-size: var(--#{$prefix}font-size-sm);
      width: 100%;

      &:hover,
      &:focus-visible {
        font-weight: 500;
        background-color: var(--#{$prefix}hover);
        outline: none;
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    > ul > li:not(:last-child) {
      border-bottom: 1px solid HEXToRGBA($black, 0.1);
    }
  }
}
</style>
