package com.bignerdranch.android.geoquiz;

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

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPreviousButton;
    private ImageButton mNextButton;
    private TextView mQuestionTextView;

    private ArrayList<String> Questions = new ArrayList<String>();
    private ArrayList<Boolean> Answers = new ArrayList<Boolean>();
    private Question[] mQuestionBank = new Question[11];

    /**private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };**/

    private int mCurrentIndex = 0;

    private void updateQuestion() {
        String question = Questions.get(mCurrentIndex).substring(1);
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId;

        if (userPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_toast;
        } else {
            messageResId = R.string.incorrect_toast;
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
            .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Log.d("DEBUG", "Quiz.Activity created.");
        resetQuestions();
        Log.d("DEBUG", "Questions reset");

        for (int i = 0; i < Questions.size(); i++) {
            mQuestionBank[i] = new Question(Questions.get(i), Answers.get(i));
            Log.d("Question", mQuestionBank[i].getQuestion());
        }
        Log.d("DEBUG", "onCreate phase 2");

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % Questions.size();
                updateQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

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

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % Questions.size();
                updateQuestion();
            }
        });

        updateQuestion();
    }

    private void resetQuestions() {
        Log.d("DEBUG", "resetQuestions called");
        //use AssetManager to get image file names for enabled regions
        AssetManager assets = getAssets();
        try {
            String[] filenames = assets.list("QuestionBank");
            for (String filename : filenames) {
                try {
                    InputStream json = assets.open("QuestionBank" + "/" + filename);
                    BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
                    String line = null;
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
        for (String q : Questions) {
            Answers.add(q.substring(0,1).equals("T"));
            q = q.substring(1);
            Log.d("Questions", q);
        }
        for (int testingvariable = 0; testingvariable < 5; testingvariable++){
            if (Answers.get(testingvariable) == Boolean.TRUE) {
                Log.d("Answers", "True");
            } else {
                Log.d("Answers", "False");
            }
        }
        Log.d("DEBUG", "resetQuestions finished");
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
