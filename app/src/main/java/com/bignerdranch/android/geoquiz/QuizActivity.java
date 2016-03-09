package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


public class QuizActivity extends AppCompatActivity {

    // view objects
    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPreviousButton;
    private ImageButton mNextButton;
    private TextView mQuestionTextView;

    // logical objects
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
        SharedPreferences sharedpreferences = getSharedPreferences("GeoQuizPREFS", Context.MODE_PRIVATE);
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
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
            .show();
        Log.d("DEBUG", "checkAnswer finished");
    }

    private void resetQuestions() {
        Log.d("DEBUG", "resetQuestions called");

        //use AssetManager to get file names for enabled region
        AssetManager assets = getAssets();
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

        /** print answers in debug log
        for (int testingvariable = 0; testingvariable < Questions.size(); testingvariable++){
            if (Answers.get(testingvariable) == Boolean.TRUE) {
                Log.d("Answers", "True");
            } else {
                Log.d("Answers", "False");
            }
        }**/

        Log.d("DEBUG", "resetQuestions finished");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        resetQuestions();

        // save questions and answers from resetQuestions to questionBank
        for (int i = 0; i < Questions.size(); i++) {
            mQuestionBank[i] = new Question(Questions.get(i), Answers.get(i));
        }

        // load current index from ShardPreferences if it exists
        SharedPreferences sharedpreferences = getSharedPreferences("GeoQuizPREFS", Context.MODE_PRIVATE);
        int restoredQuestion = sharedpreferences.getInt("mCurrentIndex", 0);
        if (restoredQuestion != 0) {
            mCurrentIndex = restoredQuestion;
            Log.d("DEBUG", "GeoQuizPREFS loaded");
        }

        // on click go to next question
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % Questions.size();
                updateQuestion();
            }
        });

        // on click go to next question
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % Questions.size();
                updateQuestion();
            }
        });

        // on click go to previous question
        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
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
        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        // on click checkAnswer
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        updateQuestion();
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
