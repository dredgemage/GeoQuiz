package com.bignerdranch.android.geoquiz;

public class Question {

    private String mQuestion;
    private boolean mAnswerTrue;

    public Question(String Question, boolean answerTrue) {
        mQuestion = Question;
        mAnswerTrue = answerTrue;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion(String Question) {
        mQuestion = Question;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }
}
