package com.example.iseeyou;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

public class imagePrefClass extends Preference {

    public imagePrefClass(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public imagePrefClass(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.image_preference);
    }
}