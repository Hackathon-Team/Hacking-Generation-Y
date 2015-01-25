package com.example.hackgeny.inposition;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Abinesh on 1/24/15.
 */
public class ExhibitionCardAdapter extends CardScrollAdapter
{
    private List<ExhibitionCard> mCards;
    private Context context;

    public ExhibitionCardAdapter(List<ExhibitionCard> cards, Context context)
    {
        mCards = cards;
        this.context = context;
    }

    @Override
    public int getPosition(Object item)
    {
        return mCards.indexOf(item);
    }

    @Override
    public int getCount()
    {
        return mCards.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mCards.get(position);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        Card card = new Card(context);
        ExhibitionCard ec = mCards.get(position);

        if(ec.getTitle() != null)
        {
            card.setText(ec.getTitle());
        }
        if(ec.getInfoText() != null)
        {
            card.setFootnote(ec.getInfoText());
        }
        if(ec.getImage() != null)
        {
            card.setImageLayout(ec.getImage());
        }
        for(int img : ec.getImages()) {
            card.addImage(img);
        }

        return card.getView();
    }

}
