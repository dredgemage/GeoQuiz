package com.bignerdranch.android.geoquiz;

import android.support.v4.app.Fragment;

import java.util.UUID;

public class QuizActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        UUID quizId = (UUID)getIntent()
                .getSerializableExtra(QuizFragment.EXTRA_QUIZ_ID);
        return QuizFragment.newInstance(quizId);
    }

}
