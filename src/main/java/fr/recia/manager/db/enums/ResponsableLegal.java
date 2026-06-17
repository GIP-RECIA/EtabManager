/*
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

package fr.recia.manager.db.enums;

import java.util.Arrays;

public enum ResponsableLegal {
    @Deprecated
    Non(0), Legal(1), EnCharge(2), Contact(3);

    private int code;

    ResponsableLegal(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public static ResponsableLegal valueOf(final int code) {
        if (code == ResponsableLegal.Non.getCode()) {
            return ResponsableLegal.Contact;
        } else if (code == ResponsableLegal.Legal.getCode()) {
            return ResponsableLegal.Legal;
        } else if (code == ResponsableLegal.EnCharge.getCode()) {
            return ResponsableLegal.EnCharge;
        } else if (code == ResponsableLegal.Contact.getCode()) {
            return ResponsableLegal.Contact;
        }
        throw new IllegalArgumentException("Value of '" + code + "' doesn't match any code to enum "
            + ResponsableLegal.class.getCanonicalName() + " in defined values " + Arrays.toString(ResponsableLegal.values()));
    }

    @Override
    public String toString() {
        return this.name() + "(" + this.getCode() + ")";
    }

}
