/*
 * Copyright (C) 2012-2013 Paul Watts (paulcwatts@gmail.com)
 * and individual contributors.
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
package com.joulespersecond.seattlebusbot;

import com.joulespersecond.oba.ObaApi;
import com.joulespersecond.oba.request.ObaArrivalInfoRequest;
import com.joulespersecond.oba.request.ObaArrivalInfoResponse;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;


class ArrivalsListLoader extends AsyncTaskLoader<ObaArrivalInfoResponse> {

    private final String mStopId;

    private ObaArrivalInfoResponse mLastGoodResponse;

    private long mLastResponseTime = 0;

    private long mLastGoodResponseTime = 0;

    private int mMinutesAfter = 35;
            // includes vehicles arriving or departing in the next minutesAfter minutes

    private static final int MINUTES_INCREMENT = 30; // minutes

    public ArrivalsListLoader(Context context, String stopId) {
        super(context);
        mStopId = stopId;
    }

    @Override
    public ObaArrivalInfoResponse loadInBackground() {
        return ObaArrivalInfoRequest.newRequest(getContext(), mStopId, mMinutesAfter).call();
    }

    @Override
    public void deliverResult(ObaArrivalInfoResponse data) {
        mLastResponseTime = System.currentTimeMillis();
        if (data.getCode() == ObaApi.OBA_OK) {
            mLastGoodResponse = data;
            mLastGoodResponseTime = mLastResponseTime;
        }
        super.deliverResult(data);
    }

    public long getLastResponseTime() {
        return mLastResponseTime;
    }

    public ObaArrivalInfoResponse getLastGoodResponse() {
        return mLastGoodResponse;
    }

    public long getLastGoodResponseTime() {
        return mLastGoodResponseTime;
    }

    public void incrementMinutesAfter() {
        mMinutesAfter = mMinutesAfter + MINUTES_INCREMENT;
    }

    public int getMinutesAfter() {
        return mMinutesAfter;
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();
        mLastGoodResponse = null;
        mLastGoodResponseTime = 0;
        // Ensure the loader is stopped
        onStopLoading();
    }
}
