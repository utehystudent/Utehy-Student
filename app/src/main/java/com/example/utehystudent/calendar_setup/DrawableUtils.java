package com.example.utehystudent.calendar_setup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.applandeo.materialcalendarview.CalendarUtils;
import com.applandeo.materialcalendarview.utils.DayColorsUtils;
import com.example.utehystudent.R;

public final class DrawableUtils {

    public static Drawable getCircleDrawableWithText(Context context, String string) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.sample_three_icons);
        Drawable text = CalendarUtils.getDrawableText(context, string, null, android.R.color.white, 12);

        Drawable[] layers = {background, text};
        return new LayerDrawable(layers);
    }

    public static Drawable getThreeDots(Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_circle);

        //Add padding to too large icon
        return new InsetDrawable(drawable, 150, 0, 150, 0);
    }

    public static Drawable getDayCircle(Context context, @ColorRes int borderColor, @ColorRes int fillColor) {
        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.calendar_day_background);
        drawable.setStroke(6, DayColorsUtils.parseColor(context, borderColor));
        drawable.setColor(DayColorsUtils.parseColor(context, fillColor));
        return drawable;
    }

    private DrawableUtils() {
    }
}