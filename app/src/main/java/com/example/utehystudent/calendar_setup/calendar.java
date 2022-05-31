package com.example.utehystudent.calendar_setup;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.CalendarView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class calendar extends CalendarView {


    public calendar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        drawBackground (canvas);
        drawWeekNumbersAndDates (canvas);
//        drawWeekSeparators (canvas);
//        drawSelectedDateVerticalBars (canvas);
        super.onDraw(canvas);

    }


    private void drawWeekNumbersAndDates(Canvas canvas) {
    }
}