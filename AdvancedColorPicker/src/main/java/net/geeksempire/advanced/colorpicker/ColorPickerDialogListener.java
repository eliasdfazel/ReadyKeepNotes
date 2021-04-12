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

import androidx.annotation.ColorInt;

/**
 * Callback used for getting the selected color from a color picker dialog.
 */
public interface ColorPickerDialogListener {

  /**
   * Callback that is invoked when a color is selected from the color picker dialog.
   *
   * @param dialogId The dialog id used to create the dialog instance.
   * @param color The selected color
   */
  void onColorSelected(int dialogId, @ColorInt int color);

  /**
   * Callback that is invoked when the color picker dialog was dismissed.
   *
   * @param dialogId The dialog id used to create the dialog instance.
   */
  void onDialogDismissed(int dialogId);
}
