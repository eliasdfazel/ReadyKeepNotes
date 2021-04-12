/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.audio.recorder.model;

public enum AudioSampleRate {
    HZ_48000,
    HZ_44100,
    HZ_32000,
    HZ_22050,
    HZ_16000,
    HZ_11025,
    HZ_8000;

    public int getSampleRate(){
        return Integer.parseInt(name().replace("HZ_", ""));
    }
}