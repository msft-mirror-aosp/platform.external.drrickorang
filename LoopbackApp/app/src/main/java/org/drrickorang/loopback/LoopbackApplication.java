/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drrickorang.loopback;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;

public class LoopbackApplication extends Application {

    private int mSamplingRate = 48000;
    private int mPlayBufferSizeInBytes = 0;
    private int mRecordBuffSizeInBytes = 0;
    private int mAudioThreadType = 0; //0:Java, 1:Native (JNI)

    public static final int AUDIO_THREAD_TYPE_JAVA   = 0;
    public static final int AUDIO_THREAD_TYPE_NATIVE = 1;

    public static final int BYTES_PER_FRAME = 2;

    public void setDefaults () {

        if (isSafeToUseSles()) {
            mAudioThreadType = AUDIO_THREAD_TYPE_NATIVE;
        } else {

            mAudioThreadType = AUDIO_THREAD_TYPE_JAVA;
        }
        computeDefaults();
    }

    int getSamplingRate() {
        return mSamplingRate;
    }

    void setSamplingRate(int samplingRate) {
        mSamplingRate = samplingRate;
    }

    int getAudioThreadType() {
        return mAudioThreadType;
    }

    void setAudioThreadType(int audioThreadType) {
        mAudioThreadType = audioThreadType;
    }

    int getPlayBufferSizeInBytes() {
        return mPlayBufferSizeInBytes;
    }

    void setPlayBufferSizeInBytes(int playBufferSizeInBytes) {
        mPlayBufferSizeInBytes = playBufferSizeInBytes;
    }

    int getRecordBufferSizeInBytes() {
        return mRecordBuffSizeInBytes;
    }

    void setRecordBufferSizeInBytes(int recordBufferSizeInBytes) {
        mRecordBuffSizeInBytes = recordBufferSizeInBytes;
    }

    public void computeDefaults() {

        int samplingRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
        setSamplingRate(samplingRate);



        if( mAudioThreadType == AUDIO_THREAD_TYPE_NATIVE) {

            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            String value = am.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            int minBufferSizeInFrames = Integer.parseInt(value);
            int minBufferSizeInBytes = BYTES_PER_FRAME * minBufferSizeInFrames;

            setPlayBufferSizeInBytes(minBufferSizeInBytes);
            setRecordBufferSizeInBytes(minBufferSizeInBytes);
        } else {

            int minPlayBufferSizeInBytes = AudioTrack.getMinBufferSize(samplingRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            setPlayBufferSizeInBytes(minPlayBufferSizeInBytes);

            int minRecBufferSizeInBytes =  AudioRecord.getMinBufferSize(samplingRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            setRecordBufferSizeInBytes(minRecBufferSizeInBytes);
        }

        //log("computed defaults");

    }

    boolean isSafeToUseSles() {
        return  Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setDefaults();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
