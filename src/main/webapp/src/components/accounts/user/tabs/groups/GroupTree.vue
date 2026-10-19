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
defineProps<{
  groups: Map<string, any>
}>()
</script>

<template>
  <ul>
    <li
      v-for="[name, children] in groups"
      :key="name"
      :title="typeof children === 'string' ? children : undefined"
    >
      {{ name }}

      <GroupTree
        v-if="children.size > 0"
        :groups="children"
      />
    </li>
  </ul>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

ul {
  @include unstyled-list;
  padding-left: 16px;

  > li {
    margin: 8px 0;
  }
}
</style>
