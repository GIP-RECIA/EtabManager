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
import type { StructureRestriction } from '@/types/index.ts'
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import { useSaveRestrictionsMutation } from '@/services/queries/index.ts'

const props = withDefaults(
  defineProps<{
    structureId?: number
    restrictions?: StructureRestriction
    canEdit?: boolean
    disableEdit?: boolean
  }>(),
  {
    canEdit: true,
    disableEdit: false,
  },
)

const { t } = useI18n()

const { mutate } = useSaveRestrictionsMutation()

const isEdit = ref<boolean>(false)

const enabeled = ref<boolean>(false)

watch(
  () => props.restrictions,
  (val) => {
    if (!val)
      return

    enabeled.value = props.restrictions?.enabled ?? false
  },
)

function save(): void {
  if (
    !props.structureId
    || !props.restrictions
  ) {
    return
  }

  const body: StructureRestriction = {
    ...props.restrictions,
    enabled: enabeled.value,
  }

  mutate({
    id: props.structureId,
    body,
  })
}
</script>

<template>
  <div class="item">
    <div
      v-if="!isEdit"
    >
      <input
        id="enabled"
        v-model="enabeled"
        type="checkbox"
        :disabled="disableEdit"
        @change="save"
      >
      <label for="enabled">
        {{ t('enabled') }}
      </label>
    </div>
    <SafeEmptyData
      v-else
      :value="
        restrictions
          ? t(restrictions.enabled ? 'enabled' : 'disabled')
          : undefined
      "
    />
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;
</style>
