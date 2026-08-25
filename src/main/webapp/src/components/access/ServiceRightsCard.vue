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
import type { RightMember, ServiceRight, ServiceRights } from '@/types/index.ts'
import { faPen } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { alphaSort } from '@/utils/index.ts'

const props = defineProps<{
  serviceRights: ServiceRights
  edit: boolean
}>()

defineEmits<{
  edit: [service: string, right: ServiceRight]
}>()

const { t } = useI18n()

const extendedServiceRights = computed<{
  service: string
  rights: (
    ServiceRight & {
      groups: RightMember[]
      users: RightMember[]
      manuals: RightMember[]
    }
  )[]
}>(() => {
  const rights = props.serviceRights.rights.map((right) => {
    const allowedIds = new Set([
      ...right.mandatoryGroups.map(({ id }) => id),
      ...right.possibleGroups.map(({ id }) => id),
    ])

    const members = [...right.currentMembers]
      .sort((a, b) => (
        alphaSort(a.displayName, b.displayName, 'asc')
      ))

    return {
      ...right,
      groups: members.filter(({ id, user }) => !user && allowedIds.has(id)),
      users: members.filter(({ user }) => user),
      manuals: members.filter(({ id, user }) => !user && !allowedIds.has(id)),
    }
  })

  return {
    ...props.serviceRights,
    rights,
  }
})
</script>

<template>
  <div class="service-card">
    <header>
      <h3>
        {{ serviceRights.name }}
      </h3>
    </header>

    <div class="body">
      <div
        v-for="right in extendedServiceRights.rights"
        :key="right.role"
        class="role-card"
      >
        <header>
          <h4>
            {{ right.description }}
          </h4>
        </header>

        <ul>
          <li
            v-for="member in right.groups"
            :key="member.id"
            class="tag-primary"
          >
            {{ member.displayName }}
          </li>
          <li
            v-for="member in right.users"
            :key="member.id"
            :title="`${member.displayName} (${member.mail})`"
            class="tag-primary"
          >
            {{ member.displayName }}
          </li>
          <li
            v-for="member in right.manuals"
            :key="member.id"
            class="tag"
          >
            {{ member.displayName }}
          </li>
        </ul>

        <footer v-if="edit">
          <button
            type="button"
            :aria-label="`${t('button.edit')} - ${right.description}`"
            class="btn-primary small"
            @click="$emit('edit', serviceRights.service, right)"
          >
            {{ t('button.edit') }}
            <FontAwesomeIcon
              :icon="faPen"
            />
          </button>
        </footer>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.service-card {
  display: flex;
  flex-direction: column;
  border-radius: 10px;

  > .body {
    display: grid;
    gap: 24px;

    > .role-card {
      display: flex;
      flex-direction: column;
      border-radius: 10px;
      box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);
      padding: 16px;

      > ul {
        @include unstyled-list;
        display: flex;
        flex-wrap: wrap;
        gap: 7px 8px;
        padding-bottom: 16px;

        > li {
          &.tag,
          &.tag-primary {
            word-break: break-all;
          }
        }
      }

      > footer {
        display: flex;
        justify-content: flex-end;
        margin-top: 24px;
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    > .body {
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
    }
  }
}
</style>
