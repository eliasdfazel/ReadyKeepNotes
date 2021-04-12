/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geeksempire.ready.keep.notes.Utils.UI.Views.ZoomingImageView;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Describes how the {@link ZoomageView} will reset to its original size
 * once interaction with it stops. {@link #UNDER} will reset when the image is smaller
 * than or equal to its starting size, {@link #OVER} when it's larger than or equal to its starting size,
 * {@link #ALWAYS} in both situations,
 * and {@link #NEVER} causes no reset. Note that when using {@link #NEVER}, the image will still animate
 * to within the screen bounds in certain situations.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({AutoResetMode.NEVER, AutoResetMode.UNDER, AutoResetMode.OVER, AutoResetMode.ALWAYS})
public @interface AutoResetMode {

    int UNDER = 0;
    int OVER = 1;
    int ALWAYS = 2;
    int NEVER = 3;

    class Parser {

        @AutoResetMode
        public static int fromInt(final int value) {
            switch (value) {
                case OVER:
                    return OVER;
                case ALWAYS:
                    return ALWAYS;
                case NEVER:
                    return NEVER;
                default:
                    return UNDER;
            }
        }
    }

}
