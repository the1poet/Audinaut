/*
  This file is part of Subsonic.
    Subsonic is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    Subsonic is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with Subsonic. If not, see <http://www.gnu.org/licenses/>.
    Copyright 2015 (C) Scott Jackson
*/

package net.nullsum.audinaut.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class RecyclingImageView extends AppCompatImageView {
    private boolean invalidated = false;

    public RecyclingImageView(Context context) {
        super(context);
    }

    public RecyclingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Drawable drawable = this.getDrawable();
        if (drawable != null) {
            if (drawable instanceof BitmapDrawable) {
                if (isBitmapRecycled(drawable)) {
                    this.setImageDrawable(null);
                    setInvalidated(true);
                }
            } else if (drawable instanceof TransitionDrawable) {
                TransitionDrawable transitionDrawable = (TransitionDrawable) drawable;

                // If last bitmap in chain is recycled, just blank this out since it would be invalid anyways
                Drawable lastDrawable = transitionDrawable.getDrawable(transitionDrawable.getNumberOfLayers() - 1);
                if (isBitmapRecycled(lastDrawable)) {
                    this.setImageDrawable(null);
                    setInvalidated(true);
                } else {
                    // Go through earlier bitmaps and make sure that they are not recycled
                    for (int i = 0; i < transitionDrawable.getNumberOfLayers(); i++) {
                        Drawable layerDrawable = transitionDrawable.getDrawable(i);
                        if (isBitmapRecycled(layerDrawable)) {
                            // If anything in the chain is broken, just get rid of transition and go to last drawable
                            this.setImageDrawable(lastDrawable);
                            break;
                        }
                    }
                }
            }
        }

        super.onDraw(canvas);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        setInvalidated(false);
    }

    private boolean isBitmapRecycled(Drawable drawable) {
        if (!(drawable instanceof BitmapDrawable)) {
            return false;
        }

        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        return bitmapDrawable.getBitmap() != null && bitmapDrawable.getBitmap().isRecycled();
    }

    public boolean isInvalidated() {
        return invalidated;
    }

    private void setInvalidated(boolean invalidated) {
        this.invalidated = invalidated;
    }
}
