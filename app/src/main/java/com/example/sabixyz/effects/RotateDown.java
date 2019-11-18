package com.example.sabixyz.effects;


import android.support.v4.view.ViewPager;
import android.view.View;

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
