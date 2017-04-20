package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.CardExpansion;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.CardHandler;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.ExpansionsAdapter;

public class CreateRuleActivity extends AppCompatActivity {
    private ArrayList<CardExpansion> expansions;
    private ListView expansionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_rule);
        expansionList = (ListView) findViewById(R.id.expansion_list);
        expansions = CardHandler.getExpansions(this);
        expansionList.setAdapter(new ExpansionsAdapter(this, expansions));

    }
}
