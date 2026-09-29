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
import { faXmark } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

defineProps<{
  id: string
  name: string
  to: RouteLocationAsRelativeGeneric
}>()

defineEmits<{
  close: [id: string]
}>()
</script>

<template>
  <div>
    <router-link
      :to="to"
      :title="name"
    >
      {{ name }}
      <span />
    </router-link>
    <button
      type="button"
      :aria-label="`Fermer l'onglet ${name}`"
      @click="$emit('close', id)"
    >
      <FontAwesomeIcon
        :icon="faXmark"
      />
    </button>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

div {
  position: relative;
  display: flex;
  justify-content: space-between;
  gap: 2px;
  border-radius: 4px;
  box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);
  width: 150px;
  background-color: $white;
  overflow: hidden;

  > a {
    @include unstyled-link;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    font-size: var(--#{$prefix}font-size-sm);
    padding: 6px 0 6px 10px;

    &.router-link-exact-active {
      color: var(--#{$prefix}primary);
    }

    &:focus-visible {
      outline: none;
    }

    > span {
      position: absolute;
      inset: 0;
    }
  }

  > button {
    flex: 0 0 auto;
    z-index: 1;
    font-size: var(--#{$prefix}font-size-xs);
    opacity: 0.5;
    height: 34px;
    width: 34px;
    border-radius: 50%;

    &:hover,
    &:focus-visible {
      opacity: 1;
    }

    &:focus-visible {
      outline: 2px dotted var(--#{$prefix}primary);
      outline-offset: -4px;
    }
  }

  &:has(a:not(.router-link-exact-active):hover, a:not(.router-link-exact-active):focus-visible)::after {
    content: '';
    position: absolute;
    inset: auto 0 0;
    border-bottom: 2px dotted var(--#{$prefix}primary);
  }

  &:has(.router-link-exact-active:focus-visible) {
    outline: 2px dotted var(--#{$prefix}primary);
  }

  &:has(.router-link-exact-active)::after {
    content: '';
    position: absolute;
    inset: auto 0 0;
    border-bottom: 2px solid var(--#{$prefix}primary);
  }
}
</style>
