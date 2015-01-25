package com.example.hackgeny.inposition;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ExhibitionCardAdapter extends CardScrollAdapter {

    private List<ExhibitionCard> cards;
    private Context context;

    public ExhibitionCardAdapter(List<ExhibitionCard> cards, Context context) {
        this.cards = cards;
        this.context = context;
    }

    @Override
    public int getPosition(Object item) {
        return cards.indexOf(item);
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int position) {
        return cards.get(position);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        return cards.get(position).getView();

    }

}
