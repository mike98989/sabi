package com.sabi.sabixyz.effects;


import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class RotateDown implements ViewPager.PageTransformer {
    private static final float ROTATION = -15f;

    @Override
    public void transformPage( View page, float position ) {
        final float width = page.getWidth();
        final float rotation = ROTATION * position;

        page.setPivotX( width * 0.5f );
        page.setPivotY( 0f );
        page.setTranslationX( 0f );
        page.setRotation( rotation );
    }
}
