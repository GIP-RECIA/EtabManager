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
import type { VueTable } from '@tanstack/vue-table'
import {
  faAngleLeft,
  faAngleRight,
  faAnglesLeft,
  faAnglesRight,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

defineProps<{
  table: VueTable<any, any>
}>()
</script>

<template>
  <div
    v-show="table.getPageCount() > 1"
    class="pagination"
  >
    <button
      type="button"
      class="btn-secondary small circle"
      :disabled="!table.getCanPreviousPage()"
      @click="() => table.setPageIndex(0)"
    >
      <FontAwesomeIcon
        :icon="faAnglesLeft"
      />
    </button>

    <button
      type="button"
      class="btn-secondary small circle"
      :disabled="!table.getCanPreviousPage()"
      @click="() => table.previousPage()"
    >
      <FontAwesomeIcon
        :icon="faAngleLeft"
      />
    </button>

    <p>
      <span class="sr-only">page</span>
      {{ table.atoms.pagination.get().pageIndex + 1 }}
      <span class="sr-only">sur</span>
      <span aria-hidden="true">/</span>
      {{ table.getPageCount() }}
    </p>

    <button
      type="button"
      class="btn-secondary small circle"
      :disabled="!table.getCanNextPage()"
      @click="() => table.nextPage()"
    >
      <FontAwesomeIcon
        :icon="faAngleRight"
      />
    </button>

    <button
      type="button"
      class="btn-secondary small circle"
      :disabled="!table.getCanNextPage()"
      @click="() => table.setPageIndex(table.getPageCount() - 1)"
    >
      <FontAwesomeIcon
        :icon="faAnglesRight"
      />
    </button>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 20px;

  > p {
    width: 6em;
    text-align: center;
  }
}
</style>
