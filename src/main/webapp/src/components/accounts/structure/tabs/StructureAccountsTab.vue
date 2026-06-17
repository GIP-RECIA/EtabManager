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
import type {
  ExpandedState,
  Row,
  RowSelectionState,
  SortingState,
} from '@tanstack/vue-table'
import type { AccountUser, Structure } from '@/types/index.ts'
import {
  faAngleDown,
  faAngleUp,
  faEye,
  faFileExport,
  faLockOpen,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import {
  createColumnHelper,
  FlexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useVueTable,
} from '@tanstack/vue-table'
import { useBreakpoints } from '@vueuse/core'
import { format } from 'date-fns'
import { computed, h, ref, watch, watchEffect } from 'vue'
import { useI18n } from 'vue-i18n'
import { RouterLink } from 'vue-router'
import Pagination from '@/components/Pagination.vue'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import { useNavigationTabs } from '@/composables'
import {
  CategoriePersonne,
  categoriePersonneMap,
  Etat,
  etatFilters,
  etatMap,
} from '@/types/enums/index.ts'
import { concatenate, getIconDefinition, getStateLabel } from '@/utils/index.ts'
import IndeterminateCheckbox from './accounts/IndeterminateCheckbox.vue'
import '@gip-recia/ui-webcomponents/dist/r-filters.js'

const props = defineProps<{
  structure?: Structure
}>()

const { t } = useI18n()

const breakpoints = useBreakpoints({
  xs: 0,
  sm: 576,
  md: 768,
  lg: 992,
  xl: 1200,
  xxl: 1400,
})

/* Filters */

const categoriesPersonne = computed<CategoriePersonne[]>(() => (
  [...new Set(
    props.structure?.personnes
      .map(user => user.categoriePersonne),
  )]
))

const sameSource = computed<boolean>(() => {
  const personnes = props.structure?.personnes

  return !!personnes?.length
    && personnes.length > 0
    && personnes.every(user => user.local === personnes[0].local)
})

const activeFilters = ref<{ id: string, checked: string[] }[]>([])

function isItemChecked(filterId: string, itemKey: string): boolean {
  const filter = activeFilters.value.find(f => f.id === filterId)
  if (!filter)
    return itemKey === `${filterId}-all`

  return filter.checked.includes(itemKey)
}

const filters = computed(() => [
  ...(!sameSource.value
    ? [{
        id: 'source',
        name: t('page.structure.accounts.filter.source.header'),
        type: 'radio',
        items: [
          {
            key: 'source-all',
            value: t('page.structure.accounts.filter.source.all'),
            checked: isItemChecked('source', 'source-all'),
          },
          {
            key: 'external',
            value: t('page.structure.accounts.filter.source.external'),
            checked: isItemChecked('source', 'external'),
          },
          {
            key: 'local',
            value: t('page.structure.accounts.filter.source.local'),
            checked: isItemChecked('source', 'local'),
          },
        ],
      }]
    : []
  ),
  ...(categoriesPersonne.value.length > 1
    ? [{
        id: 'profil',
        name: t('page.structure.accounts.filter.profil.header'),
        type: 'checkbox',
        items: [
          {
            key: 'profil-all',
            value: t('page.structure.accounts.filter.profil.all'),
            checked: isItemChecked('profil', 'profil-all'),
          },
          ...Object.values(CategoriePersonne)
            .filter(cat => (
              categoriesPersonne.value.includes(cat)
            ))
            .map(cat => ({
              key: cat,
              value: t(categoriePersonneMap[cat].i18n),
              checked: isItemChecked('profil', cat),
            })),
        ],
      }]
    : []
  ),
  {
    id: 'state',
    name: t('page.structure.accounts.filter.state.header'),
    type: 'checkbox',
    items: [
      {
        key: 'state-all',
        value: t('page.structure.accounts.filter.state.all'),
        checked: isItemChecked('state', 'state-all'),
      },
      ...etatFilters.map(etat => ({
        key: etat,
        value: t(etatMap[etat].i18n),
        checked: isItemChecked('state', etat),
      })),
    ],
  },
])

function updateFilters(e: CustomEvent): void {
  activeFilters.value = e.detail.activeFilters
}

/* Table */

const accounts = ref<AccountUser[]>([])

watch(
  () => props.structure?.personnes,
  (val) => {
    accounts.value = val ?? []
  },
  { immediate: true },
)

const filteredAccounts = computed<AccountUser[]>(() => {
  let result = accounts.value

  for (const filter of activeFilters.value) {
    const { id, checked } = filter

    if (checked.length === 0 || checked.includes(`${id}-all`))
      continue

    switch (id) {
      case 'source':
        result = result.filter(user => checked.includes(user.local ? 'local' : 'external'))
        break
      case 'profil':
        result = result.filter(user => checked.includes(user.categoriePersonne))
        break
      case 'state':
        result = result.filter(user => checked.includes(user.etat))
        break
    }
  }

  return result
})

const hasUid = computed<boolean>(() =>
  accounts.value.some(row => row.uid != null),
)

function renderEtat(row: Row<AccountUser>) {
  const etat = {
    icon: getIconDefinition(row.original.local),
    ...etatMap[row.original.etat],
  }
  const suppressDate = row.original.dateSuppression
    ? format(row.original.dateSuppression, 'P')
    : undefined
  const title = getStateLabel(
    etat.i18n,
    suppressDate,
    t,
  )

  return h(
    'span',
    {
      title,
    },
    [
      h(FontAwesomeIcon, {
        icon: etat.icon,
        size: 'lg',
        style: {
          color: etat.color,
        },
      }),
    ],
  )
}

function renderActions(row: Row<AccountUser>) {
  const userName = concatenate([row.original.nom, row.original.prenom], ' ')
  const viewTitle = t('page.structure.accounts.table.action.view')
  const expendCollapseTitle = t(
    `page.structure.accounts.table.action.${
      row.getIsExpanded()
        ? 'collapse'
        : 'expand'
    }`,
  )

  return [
    h(
      RouterLink,
      {
        to: {
          name: 'user',
          params: { userId: row.original.id },
        },
        class: 'btn-secondary small circle',
      },
      () => [
        h(
          'span',
          {
            title: viewTitle,
            ariaLabel: `${viewTitle} - ${userName}`,
          },
          [
            h(FontAwesomeIcon, {
              icon: faEye,
            }),
          ],
        ),
      ],
    ),
    h(
      'button',
      {
        type: 'button',
        ariaExpanded: row.getIsExpanded(),
        ariaControls: `user-menu-${row.original.id}`,
        class: 'btn-secondary small circle',
        onClick: row.getToggleExpandedHandler(),
      },
      [
        h(
          'span',
          {
            title: expendCollapseTitle,
            ariaLabel: `${expendCollapseTitle} - ${userName}`,
          },
          [
            h(FontAwesomeIcon, {
              icon: faAngleDown,
              style: {
                rotate: row.getIsExpanded() ? '180deg' : undefined,
              },
            }),
          ],
        ),
      ],
    ),
  ]
}

const columnHelper = createColumnHelper<AccountUser>()
const globalFilter = ref<string>()
const columns = computed(() => [
  columnHelper.display({
    id: 'select',
    header: ({ table }: { table: any }) => h(IndeterminateCheckbox, {
      checked: table.getIsAllPageRowsSelected(),
      indeterminate: table.getIsSomePageRowsSelected(),
      onChange: table.getToggleAllPageRowsSelectedHandler(),
      ariaLabel: t('page.structure.accounts.table.selectAll'),
    }),
    cell: ({ row }: { row: any }) => h(IndeterminateCheckbox, {
      checked: row.getIsSelected(),
      disabled: !row.getCanSelect(),
      onChange: row.getToggleSelectedHandler(),
      ariaLabel: concatenate([row.original.nom, row.original.prenom], ' '),
    }),
    enableGlobalFilter: false,
  }),
  columnHelper.accessor('etat', {
    id: 'etat',
    header: t('page.structure.accounts.table.status'),
    cell: ({ row }) => renderEtat(row),
    enableGlobalFilter: false,
  }),
  columnHelper.accessor('nom', {
    id: 'nom',
    header: t('page.structure.accounts.table.lastName'),
  }),
  columnHelper.accessor('prenom', {
    id: 'prenom',
    header: t('page.structure.accounts.table.firstName'),
  }),
  ...(
    breakpoints.greaterOrEqual('md').value
      ? [
          columnHelper.accessor('categoriePersonne', {
            header: t('page.structure.accounts.table.category'),
            cell: info => t(categoriePersonneMap[info.getValue()].i18n),
            enableGlobalFilter: false,
          }),
        ]
      : []
  ),
  columnHelper.display({
    id: 'actions',
    header: t('page.structure.accounts.table.action.header'),
    cell: ({ row }) => renderActions(row),
    enableGlobalFilter: false,
  }),
])
const rowSelection = ref<RowSelectionState>({})
const sorting = ref<SortingState>([])
const expanded = ref<ExpandedState>({})

const table = useVueTable({
  get data() {
    return filteredAccounts.value
  },
  get columns() {
    return columns.value
  },
  state: {
    get rowSelection() {
      return rowSelection.value
    },
    get globalFilter() {
      return globalFilter.value
    },
    get sorting() {
      return sorting.value
    },
    get expanded() {
      return expanded.value
    },
  },
  getCoreRowModel: getCoreRowModel(),
  getFilteredRowModel: getFilteredRowModel(),
  getPaginationRowModel: getPaginationRowModel(),
  getSortedRowModel: getSortedRowModel(),
  getRowCanExpand: () => true,
  initialState: {
    pagination: {
      pageSize: 20,
    },
  },
  enableRowSelection: true,
  onRowSelectionChange: (updateOrValue) => {
    rowSelection.value = typeof updateOrValue === 'function'
      ? updateOrValue(rowSelection.value)
      : updateOrValue
  },
  onGlobalFilterChange: (val) => {
    globalFilter.value = val as string
  },
  enableMultiSort: true,
  maxMultiSortColCount: 2,
  onSortingChange: (updaterOrValue) => {
    sorting.value = typeof updaterOrValue === 'function'
      ? updaterOrValue(sorting.value)
      : updaterOrValue
  },
  onExpandedChange: (updaterOrValue) => {
    expanded.value = typeof updaterOrValue === 'function'
      ? updaterOrValue(expanded.value)
      : updaterOrValue
  },
})

const {
  currentTabParams,
  setTabParams,
} = useNavigationTabs()

watch(
  globalFilter,
  (val) => {
    setTabParams({
      accountsSearch: val ?? '',
    })
  },
)

watch(
  activeFilters,
  (val) => {
    setTabParams({
      accountsActiveFilters: val,
    })
  },
  { deep: true },
)

watchEffect(() => {
  if (!props.structure)
    return

  const params = currentTabParams.value

  if (
    params?.accountsSearch !== undefined
    && params.accountsSearch !== globalFilter.value
  ) {
    globalFilter.value = params.accountsSearch
  }

  if (
    params?.accountsActiveFilters !== undefined
  ) {
    activeFilters.value = params.accountsActiveFilters
  }
})

/* Actions */

const selected = computed<number[]>(() => (
  table.getSelectedRowModel().rows.map(({ original: { id } }) => id)
))

const toUnlock = computed<number[]>(() => (
  table.getSelectedRowModel()
    .rows
    .filter(({ original: { etat } }) => etat === Etat.Bloque)
    .map(({ original: { id } }) => id)
))

function onUnlock(): void {

}

function onExport(): void {

}
</script>

<template>
  <div>
    <r-filters
      :data="filters"
      @update-filters="updateFilters"
    />

    <div v-dev class="accounts-actions">
      <h2 class="sr-only">
        {{ t('page.structure.actions') }}
      </h2>

      <ul>
        <li>
          <button
            type="button"
            :disabled="toUnlock.length === 0"
            class="btn-primary small"
            @click="onUnlock"
          >
            {{ t('button.unlock') }}
            <FontAwesomeIcon
              :icon="faLockOpen"
            />
          </button>
        </li>
        <li v-dev>
          <button
            type="button"
            :disabled="selected.length === 0"
            class="btn-primary small"
            @click="onExport"
          >
            {{ t('button.export') }}
            <FontAwesomeIcon
              :icon="faFileExport"
            />
          </button>
        </li>
      </ul>
    </div>

    <div class="field">
      <div class="field-layout">
        <div class="field-container">
          <div class="middle">
            <label for="structure-search">
              {{ t('page.structure.accounts.search') }}
            </label>
            <input
              id="structure-search"
              v-model.trim="globalFilter"
              type="text"
              placeholder=""
            >
          </div>
        </div>
        <div class="active-indicator" />
      </div>
    </div>

    <div class="accounts-data">
      <table :aria-label="t('page.structure.accounts.table.label')">
        <thead>
          <tr
            v-for="headerGroup in table.getHeaderGroups()"
            :key="headerGroup.id"
          >
            <th
              v-for="header in headerGroup.headers"
              :key="header.id"
              :colSpan="header.colSpan"
              :class="[
                header.column.getCanSort()
                  ? 'cursor-pointer select-none'
                  : '',
                header.column.columnDef.id,
              ]"
              scope="col"
              @click="header.column.getToggleSortingHandler()?.($event)"
            >
              <FlexRender
                v-if="!header.isPlaceholder"
                :render="header.column.columnDef.header"
                :props="header.getContext()"
              />

              <FontAwesomeIcon
                v-if="header.column.getIsSorted()"
                :icon="
                  header.column.getIsSorted() === 'asc'
                    ? faAngleUp
                    : faAngleDown
                "
              />
            </th>
          </tr>
        </thead>
        <tbody>
          <template
            v-for="row in table.getRowModel().rows"
            :key="row.id"
          >
            <tr class="contentLine">
              <td
                v-for="cell in row.getVisibleCells()"
                :key="cell.id"
                :class="cell.column.columnDef.id"
              >
                <FlexRender
                  :render="cell.column.columnDef.cell"
                  :props="cell.getContext()"
                />
              </td>
            </tr>
            <tr
              v-show="row.getIsExpanded()"
              :id="`user-menu-${row.original.id}`"
              class="expandedLine"
              role="none"
            >
              <td
                :colspan="row.getAllCells().length"
                role="none"
              >
                <div>
                  <p v-if="!breakpoints.greaterOrEqual('md').value">
                    <span class="sr-only">
                      {{ t('page.structure.accounts.table.category') }}
                    </span>
                    <SafeEmptyData
                      :value="t(categoriePersonneMap[row.original.categoriePersonne].i18n)"
                    />
                  </p>
                  <p v-if="hasUid">
                    <span class="sr-only">
                      {{ t('page.structure.accounts.table.uid') }}
                    </span>
                    <SafeEmptyData
                      :value="row.original.uid"
                    />
                  </p>
                  <p>
                    <span class="sr-only">
                      {{ t('page.structure.accounts.table.login') }}
                    </span>
                    <SafeEmptyData
                      :value="
                        row.original.guichet
                          ? t('page.structure.accounts.table.officeExternal')
                          : row.original.login
                      "
                    />
                  </p>
                  <p>
                    <span class="sr-only">
                      {{ t('page.structure.accounts.table.email') }}
                    </span>
                    <SafeEmptyData
                      :value="row.original.email"
                    />
                  </p>
                  <p>
                    <span class="sr-only">
                      {{ t('page.structure.accounts.table.sourceModificationDate') }}
                    </span>
                    <SafeEmptyData
                      :value="format(row.original.dateModificationSource, 'P')"
                    />
                  </p>
                </div>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>

    <Pagination
      :table="table"
    />
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.accounts-actions {
  flex: 0 1 auto;
  min-width: 320 - 2 * 16px;

  > ul {
    @include unstyled-list;
    display: flex;
    flex-wrap: wrap;
    justify-content: end;
    gap: 8px;
  }
}

.accounts-data {
  > table {
    display: grid;
    border-collapse: collapse;
    table-layout: fixed;
    width: 100%;

    > thead > tr > th,
    > tbody > tr > td {
      &.select,
      &.etat {
        padding: 12px;
        text-align: center;
      }

      &.select {
        width: 40px;
      }

      &.etat {
        width: 70px;
      }
    }

    > thead {
      position: sticky;
      top: 0;
      background-color: var(--#{$prefix}body-bg);

      > tr > th {
        padding: 12px;
      }
    }

    > tbody {
      > tr {
        transition: background-color 0.15s ease;

        &.contentLine {
          display: grid;
          grid-template-columns: 40px 1fr auto;
          grid-template-areas:
            'select nom    actions'
            'etat   prenom actions';
          align-items: center;
          white-space: nowrap;
          border-top: 1px solid var(--#{$prefix}stroke);

          > td {
            &:not(.select, .etat, .actions) {
              padding: 12px 16px;
            }

            &.select {
              grid-area: select;
              padding-bottom: 0;
            }

            &.etat {
              grid-area: etat;
              padding-top: 0;
            }

            &.nom {
              grid-area: nom;
              padding-bottom: 0;
            }

            &.prenom {
              grid-area: prenom;
              padding-top: 0;
            }

            &.actions {
              grid-area: actions;
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;
              gap: 8px;
              padding: 8px;

              > :deep(button) > span > svg {
                transition: rotate 0.2s ease-in-out;
              }
            }
          }
        }

        &.expandedLine {
          display: grid;

          > td > div {
            display: grid;
            align-items: center;
            grid-auto-rows: 1fr;

            > p {
              padding: 12px 16px;
            }
          }
        }

        > td {
          vertical-align: middle;
        }

        &:hover,
        &:has(:focus-visible),
        &:hover + tr.expandedLine,
        &:has(:focus-visible) + tr.expandedLine,
        &:has(+ tr.expandedLine:hover) {
          background-color: HEXToRGBA(var(--#{$prefix}btn-secondary-hover), 0.4);
        }
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, sm)) {
    > table {
      display: table;

      > thead > tr > th,
      > tbody > tr > td {
        &.actions {
          width: 100px;
        }
      }

      > thead > tr > th {
        padding: 12px 16px;
      }

      > tbody > tr {
        &.contentLine {
          display: table-row;

          > td {
            &.select {
              padding-bottom: 12px;
            }

            &.etat {
              padding-top: 12px;
            }

            &.nom {
              padding-bottom: 12px;
            }

            &.prenom {
              padding-top: 12px;
            }

            &.actions {
              flex-direction: row;
            }
          }
        }

        &.expandedLine {
          display: table-row;

          > td > div {
            grid-template-columns: repeat(2, 1fr);
          }
        }
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    > table > tbody > tr.expandedLine > td > div {
      grid-auto-flow: column;
      grid-auto-columns: 1fr;
    }
  }
}
</style>
