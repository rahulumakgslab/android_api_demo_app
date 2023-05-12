package com.example.sondeapitestapp;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sondeapitestapp.model.Question;
import com.example.sondeapitestapp.model.UserAnswer;
import com.example.sondeapitestapp.utils.Constant;

import java.util.ArrayList;
import java.util.Collections;

public class QuestionnaireAdapter extends RecyclerView.Adapter<QuestionnaireAdapter.ViewHolder> {

    ArrayList<Question> questionList;
    ArrayList<UserAnswer> userAnswerList;
    private String SYMBOL_FAHRENHEIT = "F";
    private String BODY_TEMPERATURE = "BODY_TEMPERATURE";

    public QuestionnaireAdapter(ArrayList<Question> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.userAnswerList = new ArrayList<>(Collections.nCopies(questionList.size(), null));

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_questionnaire_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Question questionData = questionList.get(position);
        viewHolder.textViewQuestion.setText(questionData.getText());
        viewHolder.textViewQueNumber.setText(getQuestionNumberLabel(position));
        initOptionalQuestionIndicatorView(viewHolder, questionData.isSkippable());

        String questionType = questionData.getType();
        if (questionType.equals(Constant.QuestionType.TEXT_FIELD)) {
            viewHolder.editTextTextAnswer.setVisibility(View.VISIBLE);
            viewHolder.layoutAnswerContainer.setVisibility(View.INVISIBLE);
            viewHolder.editTextTextAnswer.addTextChangedListener(getWatcher(position));
        } else {
            initMultipleQuestionTypeView(viewHolder, questionData, position);
        }
    }

    private String getQuestionNumberLabel(int position) {
        return "#" + (position + 1);
    }

    private TextWatcher getWatcher(int position) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                UserAnswer userAnswer = userAnswerList.get(position);
                if (userAnswer == null) {
                    userAnswer = new UserAnswer();
                }
                String textAnswer = null;
                if (BODY_TEMPERATURE.equals(questionList.get(position).getInputDataType())) {
                    textAnswer = charSequence.toString() + SYMBOL_FAHRENHEIT;
                }
                userAnswer.setResponse(textAnswer);
                userAnswerList.set(position, userAnswer);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    private void initMultipleQuestionTypeView(ViewHolder viewHolder, Question questionData, int position) {

        if (questionData.getType().equals(Constant.QuestionType.MULTIPLE_SELECT)) {

            for (int i = 0; i < questionData.getOptions().size(); i++) {
                View view = LayoutInflater.from(viewHolder.layoutAnswerContainer.getContext()).inflate(R.layout.layout_answer_muli_select, null, false);
                TextView answerTextView = view.findViewById(R.id.textViewCheckBoxAnswer);
                answerTextView.setText(questionData.getOptions().get(i).getText());

                answerTextView.setText(questionData.getOptions().get(i).getText());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onOptionClick(view, position, questionData);
                    }
                });

                viewHolder.layoutAnswerContainer.addView(view);
            }
        } else if (questionData.getType().equals(Constant.QuestionType.MULTIPLE_CHOICE)) {
            for (int i = 0; i < questionData.getOptions().size(); i++) {
                View view = LayoutInflater.from(viewHolder.layoutAnswerContainer.getContext()).inflate(R.layout.layout_answer_muli_choice, null, false);
                TextView answerTextView = view.findViewById(R.id.textViewRadioAnswer);

                answerTextView.setText(questionData.getOptions().get(i).getText());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onOptionClick(view, position, questionData);
                    }
                });
                viewHolder.layoutAnswerContainer.addView(view);
            }

        }
    }

    private void onOptionClick(View view, int position, Question questionData) {

        if (questionData.getType().equals(Constant.QuestionType.MULTIPLE_SELECT)) {
            ImageView imageViewCheckBox = view.findViewById(R.id.imageViewCheckBox);
            TextView textViewCheckBox = view.findViewById(R.id.textViewCheckBoxAnswer);

            handleMultipleSelectOnClick(position, questionData, imageViewCheckBox, textViewCheckBox);
        } else if (questionData.getType().equals(Constant.QuestionType.MULTIPLE_CHOICE)) {
            TextView textViewRadioAnswer = view.findViewById(R.id.textViewRadioAnswer);

            LinearLayout parentLinearLayout = (LinearLayout) view.getParent();
            handleMultipleChoiceOnClick(position, questionData, textViewRadioAnswer, parentLinearLayout);
        }
    }

    private void handleMultipleChoiceOnClick(int position, Question questionData, TextView textViewRadioAnswer, LinearLayout parentLinearLayout) {
        String selectedOptionText = textViewRadioAnswer.getText().toString();
        UserAnswer userAnswer = userAnswerList.get(position);

        if (userAnswer == null) {
            userAnswer = new UserAnswer();
        }
        int selectedOptionIndex = getOptionIndex(selectedOptionText, questionData);
        userAnswer.setOptionIndex(selectedOptionIndex);

        for (int i = 0; i < parentLinearLayout.getChildCount(); i++) {
            ImageView imageView = parentLinearLayout.getChildAt(i).findViewById(R.id.imageViewRadio);
            if (i == selectedOptionIndex) {
                imageView.setImageDrawable(ContextCompat.getDrawable(textViewRadioAnswer.getContext(), R.drawable.ic_radio_button_checked));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(textViewRadioAnswer.getContext(), R.drawable.ic_radio_button_unchecked));
            }
        }
        userAnswerList.set(position, userAnswer);
    }

    private void handleMultipleSelectOnClick(int position, Question questionData, ImageView imageViewCheckBox, TextView textViewCheckBox) {
        String selectedOptionText = textViewCheckBox.getText().toString();
        UserAnswer userAnswer = userAnswerList.get(position);

        if (userAnswer == null) {
            userAnswer = new UserAnswer();
            ArrayList<Integer> options = new ArrayList<>();
            options.add(getOptionIndex(selectedOptionText, questionData));
            imageViewCheckBox.setImageDrawable(ContextCompat.getDrawable(imageViewCheckBox.getContext(), R.drawable.ic_check_cheked));
            userAnswer.setOptionIndexes(options);
        } else {
            ArrayList<Integer> options = userAnswer.getOptionIndexes();
            int optionIndex = getOptionIndex(selectedOptionText, questionData);
            if (options.contains(optionIndex)) {
                options.remove(Integer.valueOf(optionIndex));
                imageViewCheckBox.setImageDrawable(ContextCompat.getDrawable(imageViewCheckBox.getContext(), R.drawable.ic_check_box_unchecked));
            } else {
                imageViewCheckBox.setImageDrawable(ContextCompat.getDrawable(imageViewCheckBox.getContext(), R.drawable.ic_check_cheked));
                options.add(getOptionIndex(selectedOptionText, questionData));
                userAnswer.setOptionIndexes(options);
            }
            userAnswer.setOptionIndexes(options);
        }
        userAnswerList.set(position, userAnswer);
    }

    private int getOptionIndex(String optionText, Question questionData) {
        for (int i = 0; i < questionData.getOptions().size(); i++) {
            if (optionText.equals(questionData.getOptions().get(i).getText())) {
                return i;
            }
        }
        throw new RuntimeException("unable to find option index from option text");
    }

    private void initOptionalQuestionIndicatorView(@NonNull ViewHolder viewHolder, boolean isSkippable) {
        if (isSkippable) {
            viewHolder.textViewOptionalQuestion.setVisibility(View.VISIBLE);
        } else {
            viewHolder.textViewOptionalQuestion.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public ArrayList<UserAnswer> getUserAnswers() {
        return userAnswerList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewQueNumber;
        TextView textViewOptionalQuestion;
        TextView textViewQuestion;
        EditText editTextTextAnswer;
        LinearLayout layoutAnswerContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewQueNumber = itemView.findViewById(R.id.textViewQueNumber);
            textViewOptionalQuestion = itemView.findViewById(R.id.textViewOptionalQuestion);
            textViewQuestion = itemView.findViewById(R.id.textViewQuestion);
            editTextTextAnswer = itemView.findViewById(R.id.editTextTextAnswer);
            layoutAnswerContainer = itemView.findViewById(R.id.layoutAnswerContainer);

        }
    }
}
