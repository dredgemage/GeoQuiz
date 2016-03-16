package com.bignerdranch.android.geoquiz;

import android.view.Menu;
import android.view.MenuItem;

import java.util.UUID;


public class QuizActivity extends SingleFragmentActivity {

    private static final String EXTRA_QUIZ_ID =
            "com.bignerdranch.android.geoquiz.quiz_id";

    @Override
    protected QuizFragment createFragment() {
        UUID quizId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_QUIZ_ID);
        return QuizFragment.newInstance(quizId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
