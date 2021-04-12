/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.advanced.colorpicker;

import androidx.annotation.IntDef;

/**
 * The shape of the color preview
 */
@IntDef({ ColorShape.SQUARE, ColorShape.CIRCLE }) public @interface ColorShape {

  int SQUARE = 0;

  int CIRCLE = 1;
}
