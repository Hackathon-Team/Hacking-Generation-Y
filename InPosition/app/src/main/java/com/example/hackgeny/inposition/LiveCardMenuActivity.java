package com.example.hackgeny.inposition;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.List;

public class LiveCardMenuActivity extends Activity {

    private List<ExhibitionCard> cards;
    private CardScrollView cardScroll;
    public Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ArrayList<Place> places = new DataBase(getApplicationContext()).closestPlaces(userLocation);

        cards = new ArrayList<ExhibitionCard>();
        for(Place place : places) {
            cards.add(new ExhibitionCard(place.getTitle(), place.getInfo(),
                    Card.ImageLayout.FULL, new int[]{R.drawable.testimage}));
        }

        cardScroll = new CardScrollView(this);
        cardScroll.setAdapter(new ExhibitionCardAdapter(cards, this));

        cardScroll.activate();
        setContentView(cardScroll);
    }

}


