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
import type { FunctionUser } from '@/types/index.ts'
import { useId } from 'vue'
import UserLink from './UserLink.vue'

withDefaults(
  defineProps<{
    header?: boolean
    label?: string
    users: FunctionUser[]
  }>(),
  {
    header: false,
  },
)

const uid = useId()
</script>

<template>
  <div class="r-card">
    <header v-if="header">
      <h3>
        {{ label }}
      </h3>
      <span class="count">
        {{ users.length }}
      </span>
    </header>

    <div class="body">
      <ul>
        <li
          v-for="user in users"
          :key="`${uid}-user-${user.id}`"
        >
          <UserLink
            :user="user"
          />
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.r-card {
  gap: 16px;

  > header {
    display: inline-flex;
    align-items: center;
    gap: 8px;

    > h3 {
      margin-bottom: 0;
    }

    > .count {
      opacity: 0.6;
    }
  }

  > .body > ul {
    @include unstyled-list;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 8px;
  }
}
</style>
