/**
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import type { Duration } from 'date-fns'
import {
  format,
  formatISO,
  intervalToDuration,
  isAfter,
  isBefore,
} from 'date-fns'

type dateType = string | number | Date

function dateToDuration(date: dateType): Duration {
  return intervalToDuration({
    start: date,
    end: Date.now(),
  })
}

function isBeforeOrEqual(
  date: dateType,
  dateToCompare: dateType,
): boolean {
  return !isAfter(date, dateToCompare)
}

function isAfterOrEqual(
  date: dateType,
  dateToCompare: dateType,
): boolean {
  return !isBefore(date, dateToCompare)
}

function isBetween(
  date: dateType,
  dateToCompareMin: dateType,
  dateToCompareMax: dateType,
  include: boolean = true,
): boolean {
  return include
    ? (
        isAfterOrEqual(date, dateToCompareMin)
        && isBeforeOrEqual(date, dateToCompareMax)
      )
    : (
        isAfter(date, dateToCompareMin)
        && isBefore(date, dateToCompareMax)
      )
}

function formatDateTime(
  date: string | undefined | null,
): string | undefined {
  return date
    ? format(date, 'P p')
    : undefined
}

function toDateTime(date: string | null): string | null {
  return date !== null
    ? format(date, 'yyyy-MM-dd\'T\'HH:mm')
    : null
}

function toISODate(date: string | undefined): string | undefined {
  return date && date.trim().length > 1
    ? formatISO(date, { representation: 'date' })
    : undefined
}

export {
  dateToDuration,
  formatDateTime,
  isAfterOrEqual,
  isBeforeOrEqual,
  isBetween,
  toDateTime,
  toISODate,
}
