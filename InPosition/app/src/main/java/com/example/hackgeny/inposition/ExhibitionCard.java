package com.example.hackgeny.inposition;

import com.google.android.glass.app.Card;

/**
 * Created by Abinesh on 1/24/15.
 */
public class ExhibitionCard
{
    private String title;
    private String infoText;
    private Card.ImageLayout img;
    private int[] images;

    public ExhibitionCard(String text, String infoText, Card.ImageLayout img, int[] images)
    {
        this.title = text;
        this.infoText = infoText;
        this.img = img;
        this.images = images;
    }
    public String getTitle()
    {
        return title;
    }

    public String getInfoText()
    {
        return infoText;
    }

    public Card.ImageLayout getImage()
    {
        return img;
    }

    public int[] getImages()
    {
        return images;
    }
}
