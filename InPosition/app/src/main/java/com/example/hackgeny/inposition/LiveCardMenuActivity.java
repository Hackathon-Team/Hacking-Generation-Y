package com.example.hackgeny.inposition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * A transparent {@link Activity} displaying a "Stop" options menu to remove the LiveCard.
 */
@SuppressWarnings("deprecation")
public class LiveCardMenuActivity extends Activity {
    private List<ExhibitionCard> mCards;
    private CardScrollView mCardScrollView;
    private Context context;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Open the options menu right away.
        openOptionsMenu();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        context = this;

        prepareExhibitionCards();

        mCardScrollView = new CardScrollView(this);
        ExhibitionCardAdapter adapter = new ExhibitionCardAdapter(mCards, context);
        mCardScrollView.setAdapter(adapter);
        mCardScrollView.activate();
        setContentView(mCardScrollView);
    }

    private void prepareExhibitionCards()
    {
        mCards = new ArrayList<ExhibitionCard>();

        ExhibitionCard ec = new ExhibitionCard("Sample Text", "Sample Information", Card.ImageLayout.FULL, new int[]{R.drawable.testimage});
        mCards.add(ec);
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
        // Nothing else to do, finish the Activity.
        finish();
    }
}


