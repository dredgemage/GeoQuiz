package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;

public class QuizFragment extends android.support.v4.app.Fragment {

    // view objects
    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPreviousButton;
    private ImageButton mNextButton;
    private TextView mQuestionTextView;

    // logical objects
    public static final String EXTRA_QUIZ_ID = "quiz_id";
    private ArrayList<String> Questions = new ArrayList<>();
    private ArrayList<Boolean> Answers = new ArrayList<>();
    private Question[] mQuestionBank = new Question[11];
    private int mCurrentIndex = 0;

    private void updateQuestion() {
        Log.d("DEBUG", "updateQuestion called");
        // update text in TextView to match current question
        String question = Questions.get(mCurrentIndex).substring(1);
        mQuestionTextView.setText(question);

        // save current question in SharedPreferences
        SharedPreferences sharedpreferences = this.getActivity().getSharedPreferences("GeoQuizPREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("mCurrentIndex", mCurrentIndex);
        editor.commit();
        Log.d("DEBUG", "updateQuestion finished");
    }

    private void checkAnswer(boolean userPressedTrue) {
        Log.d("DEBUG", "checkAnswer called");

        // preparation
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId;

        // check and set message for toast
        if (userPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_toast;
        } else {
            messageResId = R.string.incorrect_toast;
        }

        // make toast
        Toast.makeText(this.getActivity(), messageResId, Toast.LENGTH_SHORT)
                .show();
        Log.d("DEBUG", "checkAnswer finished");
    }

    private void resetQuestions() {
        Log.d("DEBUG", "resetQuestions called");

        //use AssetManager to get file names for enabled region
        AssetManager assets = this.getActivity().getAssets();
        try {
            String[] filenames = assets.list("QuestionBank");
            for (String filename : filenames) {
                try {
                    InputStream json = assets.open("QuestionBank" + "/" + filename);
                    BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
                    String line;
                    while ((line = in.readLine()) != null) {
                        Questions.add(line);
                    }
                    in.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // separate answer letter from question
        for (String q : Questions) {
            Answers.add(q.substring(0,1).equals("T"));
            q = q.substring(1);
            Log.d("Questions", q);
        }

        Log.d("DEBUG", "resetQuestions finished");
    }

    public static android.support.v4.app.Fragment newInstance(UUID quizId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_QUIZ_ID, quizId);

        android.support.v4.app.Fragment fragment = new QuizFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetQuestions();

        // save questions and answers from resetQuestions to questionBank
        for (int i = 0; i < Questions.size(); i++) {
            mQuestionBank[i] = new Question(Questions.get(i), Answers.get(i));
        }

        // load current index from ShardPreferences if it exists
        SharedPreferences sharedpreferences = this.getActivity().getSharedPreferences("GeoQuizPREFS", Context.MODE_PRIVATE);
        int restoredQuestion = sharedpreferences.getInt("mCurrentIndex", 0);
        if (restoredQuestion != 0) {
            mCurrentIndex = restoredQuestion;
            Log.d("DEBUG", "GeoQuizPREFS loaded");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quiz, container, false);

        // on click go to next question
        mQuestionTextView = (TextView) v.findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % Questions.size();
                updateQuestion();
            }
        });

        // on click go to next question
        mNextButton = (ImageButton) v.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % Questions.size();
                updateQuestion();
            }
        });

        // on click go to previous question
        mPreviousButton = (ImageButton) v.findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentIndex == 0) {
                    mCurrentIndex = Questions.size() - 1;
                } else {
                    mCurrentIndex = (mCurrentIndex - 1) % Questions.size();
                }
                updateQuestion();
            }
        });

        // on click checkAnswer
        mTrueButton = (Button) v.findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        // on click checkAnswer
        mFalseButton = (Button) v.findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        updateQuestion();
        return v;
    }

}
