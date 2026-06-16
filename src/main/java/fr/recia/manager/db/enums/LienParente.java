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

public enum LienParente {
    Pere(20, 1),
    Mere(10, 2),
    Tuteur(50, 3),
    MembreFamille(39, 4),
    ASE(51, 5),
    AutreLien(90, 6),
    EleveLuiMeme(70, 7),
    Fratrie(37, 37),
    Ascendant(38, 38),
    Educateur(41, 41),
    AssistantFamilial(42, 42),
    GardeEnfant(43, 43);

    private int code;

    private int oldCode;

    LienParente(int code, int oldCode) {
        this.code = code;
        this.oldCode = oldCode;
    }

    public int getCode() {
        return code;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public int getOldCode() {
        return oldCode;
    }

    public void setOldCode(final int oldCode) {
        this.oldCode = oldCode;
    }

    public static LienParente valueOf(final int code) {
        if (code == LienParente.Pere.getCode() || code == LienParente.Pere.getOldCode()) {
            return LienParente.Pere;
        } else if (code == LienParente.Mere.getCode() || code == LienParente.Mere.getOldCode()) {
            return LienParente.Mere;
        } else if (code == LienParente.Tuteur.getCode() || code == LienParente.Tuteur.getOldCode()) {
            return LienParente.Tuteur;
        } else if (code == LienParente.MembreFamille.getCode() || code == LienParente.MembreFamille.getOldCode()) {
            return LienParente.MembreFamille;
        } else if (code == LienParente.ASE.getCode() || code == LienParente.ASE.getOldCode()) {
            return LienParente.ASE;
        } else if (code == LienParente.AutreLien.getCode() || code == LienParente.AutreLien.getOldCode()) {
            return LienParente.AutreLien;
        } else if (code == LienParente.EleveLuiMeme.getCode() || code == LienParente.EleveLuiMeme.getOldCode()) {
            return LienParente.EleveLuiMeme;
        } else if (code == LienParente.Fratrie.getCode() || code == LienParente.Fratrie.getOldCode()) {
            return LienParente.Fratrie;
        } else if (code == LienParente.Ascendant.getCode() || code == LienParente.Ascendant.getOldCode()) {
            return LienParente.Ascendant;
        } else if (code == LienParente.Educateur.getCode() || code == LienParente.Educateur.getOldCode()) {
            return LienParente.Educateur;
        } else if (code == LienParente.AssistantFamilial.getCode() || code == LienParente.AssistantFamilial.getOldCode()) {
            return LienParente.AssistantFamilial;
        } else if (code == LienParente.GardeEnfant.getCode() || code == LienParente.GardeEnfant.getOldCode()) {
            return LienParente.GardeEnfant;
        } else {
            throw new IllegalArgumentException("Value of '" + code + "' doesn't match any code to enum " +
                LienParente.class.getCanonicalName() + " in defined values " + ResponsableLegal.values());
        }
    }

    @Override
    public String toString() {
        return this.name() + "(" + this.getCode() + ")";
    }

}
