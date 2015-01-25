package com.example.hackgeny.inposition;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.glass.widget.CardBuilder;

public class ExhibitionCard extends CardBuilder
{
    public ExhibitionCard(Context context, String text, Drawable img)
    {
        super(context, Layout.CAPTION);
        super.setText(text);
        super.addImage(img);
    }

}
