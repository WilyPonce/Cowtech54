/*
 * Copyright 2016 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidplot;

import android.graphics.Color;
import android.graphics.Paint;
import com.androidplot.util.PixelUtils;

/**
 * A basic implementation of a {@link LineLabelFormatter}.
 */
public class SimpleLineLabelFormatter implements LineLabelFormatter {

    private static final int DEFAULT_TEXT_SIZE_SP = 12;
    private static final int DEFAULT_STROKE_SIZE_DP = 2;
    private Paint paint;

    public SimpleLineLabelFormatter() {
        this(new Paint());
        getPaint().setColor(Color.WHITE);
        getPaint().setTextSize(PixelUtils.spToPix(DEFAULT_TEXT_SIZE_SP));
        getPaint().setStrokeWidth(PixelUtils.dpToPix(DEFAULT_STROKE_SIZE_DP));
    }

    public SimpleLineLabelFormatter(int color) {
        this();
        getPaint().setColor(color);
    }

    public SimpleLineLabelFormatter(Paint paint) {
        this.paint = paint;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    @Override
    public Paint getPaint(Number value) {
        return getPaint();
    }
}
