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

package fr.recia.manager.db.utils;

/**
 * Liste des constantes d'entier.
 *
 * @author GIP RECIA - Gribonvald Julien
 * 29 avr. 09
 */
public final class IntConst {
    /**
     * Constante de la longueur de la chaine de la source dans la cle de jointure.
     */
    public static final int ISOURCE = IntConst.I60;
    /**
     * Constante de la longueur de la chaine de la clé dans la clé de jointure.
     */
    public static final int ICLE = IntConst.I20;
    /**
     * Constante de la longueur de la chaine de la clé dans la clé de jointure.
     */
    public static final int IUAI = IntConst.I8;

    /**
     * Constantes.
     */
    public static final int I0 = 0;
    public static final int I1 = 1;
    public static final int I2 = 2;
    public static final int I3 = 3;
    public static final int I4 = 4;
    public static final int I5 = 5;
    public static final int I6 = 6;
    public static final int I7 = 7;
    public static final int I8 = 8;
    public static final int I9 = 9;
    public static final int I10 = 10;
    public static final int I11 = 11;
    public static final int I15 = 15;
    public static final int I16 = 16;
    public static final int I20 = 20;
    public static final int I25 = 25;
    public static final int I30 = 30;
    public static final int I32 = 32;
    public static final int I35 = 35;
    public static final int I36 = 36;
    public static final int I40 = 40;
    public static final int I45 = 45;
    public static final int I50 = 50;
    public static final int I60 = 60;
    public static final int I70 = 70;
    public static final int I80 = 80;
    public static final int I90 = 90;
    public static final int I100 = 100;
    public static final int I128 = 128;

    /**
     * Constructeur de l'objet IntConst.java.
     */
    private IntConst() {
        throw new IllegalStateException("Util class");
    }

}
