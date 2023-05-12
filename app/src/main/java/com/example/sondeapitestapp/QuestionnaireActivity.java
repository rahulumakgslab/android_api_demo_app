package com.example.sondeapitestapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.sondeapitestapp.model.Question;
import com.example.sondeapitestapp.model.UserAnswer;
import com.example.sondeapitestapp.utils.Constant;

import java.util.ArrayList;

public class QuestionnaireActivity extends AppCompatActivity {

    public static String EXTRA_KEY_QUESTIONNAIRE_DATA = "questionnaire_data";
    public static String EXTRA_KEY_ANSWERS = "answers";

    private RecyclerView recyclerViewQuestionnaire;
    private QuestionnaireAdapter questionnaireAdapter;
    private ArrayList<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        recyclerViewQuestionnaire = findViewById(R.id.recyclerViewQuestionnaire);

        questions = getIntent().getParcelableArrayListExtra(EXTRA_KEY_QUESTIONNAIRE_DATA);
        questionnaireAdapter = new QuestionnaireAdapter(questions);
        recyclerViewQuestionnaire.setAdapter(questionnaireAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewQuestionnaire.setLayoutManager(layoutManager);

        findViewById(R.id.buttonSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<UserAnswer> userAnswers = questionnaireAdapter.getUserAnswers();
                if (isValidUserAnswers(userAnswers)) {
                    setResult(userAnswers);
                } else {
                    Toast.makeText(QuestionnaireActivity.this, "Please answer all non-optional questions", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidUserAnswers(ArrayList<UserAnswer> userAnswers) {
        for (int i = 0; i < questions.size(); i++) {
            String questionType = questions.get(i).getType();
            if (questions.get(i).isSkippable()) {
                if (!isAnswerValid(userAnswers.get(i), questionType)) {
                    if (userAnswers.get(i) == null) {
                        userAnswers.set(i, new UserAnswer());
                    }
                    userAnswers.get(i).setSkipped(true);
                }
            } else {
                if (!isAnswerValid(userAnswers.get(i), questionType)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isAnswerValid(UserAnswer userAnswer, String questionType) {
        if (userAnswer != null) {
            switch (questionType) {
                case Constant.QuestionType.TEXT_FIELD:
                    return !TextUtils.isEmpty(userAnswer.getResponse());
                case Constant.QuestionType.MULTIPLE_CHOICE:
                    return userAnswer.getOptionIndex() != null;
                case Constant.QuestionType.MULTIPLE_SELECT:
                    return userAnswer.getOptionIndexes() != null && !userAnswer.getOptionIndexes().isEmpty();
            }
        }
        return false;
    }

    private void setResult(ArrayList<UserAnswer> userAnswers) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(EXTRA_KEY_ANSWERS, userAnswers);
        setResult(RESULT_OK, intent);
        finish();
    }
}