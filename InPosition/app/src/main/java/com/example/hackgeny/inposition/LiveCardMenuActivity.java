package com.example.hackgeny.inposition;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.List;

public class LiveCardMenuActivity extends Activity {

    private List<ExhibitionCard> cards;
    private CardScrollView cardScroll;

    public static Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Place> places = new DataBase(this).closestPlaces(userLocation);

        prepareExhibitionCards(places);

        cardScroll = new CardScrollView(this);
        cardScroll.setAdapter(new ExhibitionCardAdapter(cards, this));
        cardScroll.activate();
        setContentView(cardScroll);
    }

    private void prepareExhibitionCards(ArrayList<Place> places) {
        cards = new ArrayList<ExhibitionCard>();
        for (Place place : places) {
            cards.add(new ExhibitionCard(place.getTitle(), place.getInfo(), Card.ImageLayout.FULL, new int[]{R.drawable.testimage}));
        }
        cards.add(new ExhibitionCard("Sample Title", "Sample Information", Card.ImageLayout.FULL, new int[]{R.drawable.testimage}));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.in_position, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_stop:
                // Stop the service which will unpublish the live card.
                //Stop the service which will unpublish the live card.
                stopService(new Intent(this, InPosition.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        finish();
    }
}



