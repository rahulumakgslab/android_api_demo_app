package com.example.sondeapitestapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sondeapitestapp.model.MeasureDetailResponse;
import com.example.sondeapitestapp.model.PostAnswerResponse;
import com.example.sondeapitestapp.model.PostAnswersRequest;
import com.example.sondeapitestapp.model.QuestionnaireResponse;
import com.example.sondeapitestapp.model.QuestionnaireUserResponse;
import com.example.sondeapitestapp.model.S3FilePathRequest;
import com.example.sondeapitestapp.model.S3FilePathResponse;
import com.example.sondeapitestapp.model.ScoreRequest;
import com.example.sondeapitestapp.model.ScoreResponse;
import com.example.sondeapitestapp.model.UserAnswer;
import com.example.sondeapitestapp.network.RetrofitClient;
import com.example.sondeapitestapp.network.SondeApi;
import com.example.sondeapitestapp.utils.DateUtils;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RespiratorySymptomsRiskActivity extends AppCompatActivity {

    public static final String EXTRA_KEY_ACCESS_TOKEN = "accessToken";
    public static final String EXTRA_KEY_USER_IDENTIFIER = "userIdentifier";

    private String RESPIRATORY_SYMPTOMS_RISK = "respiratory-symptoms-risk";

    private int RECORD_REQUEST_CODE = 100;
    private int QUESTIONNAIRE_REQUEST_CODE = 101;
    private String TAG = RespiratorySymptomsRiskActivity.class.getSimpleName();

    private String accessToken;
    private String userIdentifier;
    private ProgressDialog mProgressDialog;
    private QuestionnaireResponse resSymptomsRiskQuestions;
    private String resSymptomsRiskAudioFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respiratory_symptoms_risk);
        mProgressDialog = new ProgressDialog(this);

        accessToken = getIntent().getExtras().getString(EXTRA_KEY_ACCESS_TOKEN);
        userIdentifier = getIntent().getExtras().getString(EXTRA_KEY_USER_IDENTIFIER);

        lunchAudioRecordingActivity();
    }

    private void lunchAudioRecordingActivity() {
        Intent intent = new Intent(RespiratorySymptomsRiskActivity.this, RecordingActivity.class);
        startActivityForResult(intent, RECORD_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECORD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String audioFilePath = data.getStringExtra("filePath");
                    uploadFile(audioFilePath);
                    Log.d(TAG, "onActivityResult: " + audioFilePath);
                }
            } else {
                Toast.makeText(this, "Unable to generate audio file", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == QUESTIONNAIRE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    ArrayList<UserAnswer> userAnswers = data.getParcelableArrayListExtra(QuestionnaireActivity.EXTRA_KEY_ANSWERS);
                    postAnswerResponse(userAnswers);
                }
            } else {
                Toast.makeText(this, "Unable to generate answer file", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void uploadFile(String filePath) {
        mProgressDialog.show();
        File file = new File(filePath);
        getS3SignedUrl(file);
    }

    private void getS3SignedUrl(File file) {
        mProgressDialog.setMessage("Getting signed URL..");
        S3FilePathRequest s3FilePathRequest = new S3FilePathRequest("wav", "US", userIdentifier);

        SondeApi sondeApi = RetrofitClient.getInstance().create(SondeApi.class);
        Call<S3FilePathResponse> call = sondeApi.getS3FilePath(accessToken, s3FilePathRequest);
        call.enqueue(new Callback<S3FilePathResponse>() {
            @Override
            public void onResponse(Call<S3FilePathResponse> call, Response<S3FilePathResponse> response) {
                S3FilePathResponse s3PathResponse = response.body();
                if (s3PathResponse != null) {
                    resSymptomsRiskAudioFilePath = s3PathResponse.getFilePath();
                    uploadFileToS3(s3PathResponse, file);
                } else {
                    dismissDialog();
                    Log.d(TAG, "getS3SignedUrl error : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<S3FilePathResponse> call, Throwable t) {
                dismissDialog();
                Log.d(TAG, "getS3SignedUrl onFailure : " + t);
            }
        });
    }

    private void uploadFileToS3(S3FilePathResponse s3PathResponse, File file) {
        mProgressDialog.setMessage("Uploading file...");
        MediaType MEDIA_TYPE_OCTET_STREAM = MediaType.parse("application/octet-stream");
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_OCTET_STREAM, file);

        SondeApi sondeApi = RetrofitClient.getInstance().create(SondeApi.class);
        Call<ResponseBody> call = sondeApi.uploadFileToS3(s3PathResponse.getSignedURL(), requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    Log.i(TAG, " : File Uploaded successfully " + file.getName());
                    getRespiratorySymptomsRiskDetails();
                } else {
                    dismissDialog();
                    Log.d(TAG, "uploadFileToS3 error : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismissDialog();
                Log.d(TAG, "uploadFileToS3 onFailure : " + t);
            }
        });

    }

    private void requestScore(String filePath, String questionnaireResponseId) {
        mProgressDialog.setMessage("Getting score..");
        ScoreRequest scoreRequest = new ScoreRequest(userIdentifier, filePath, RESPIRATORY_SYMPTOMS_RISK);
        if (questionnaireResponseId != null) {
            scoreRequest.setQuestionnaireResponseId(questionnaireResponseId);
        }
        SondeApi sondeApi = RetrofitClient.getInstance().create(SondeApi.class);
//        Call<ScoreResponse> call = sondeApi.requestScore(accessToken, scoreRequest);
//        call.enqueue(new Callback<ScoreResponse>() {
//            @Override
//            public void onResponse(Call<ScoreResponse> call, Response<ScoreResponse> response) {
//                if (response.body() != null) {
//                    dismissDialog();
////                    ((TextView) findViewById(R.id.textViewScore)).setText(String.valueOf(response.body().getScore()));
//                } else {
//                    dismissDialog();
//                    Log.d(TAG, "requestScore error : " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ScoreResponse> call, Throwable t) {
//                dismissDialog();
//                Log.d(TAG, "requestScore onFailure : " + t);
//            }
//        });
    }

    private void getRespiratorySymptomsRiskDetails() {
        mProgressDialog.show();
        mProgressDialog.setMessage("Getting measure details...");
        SondeApi sondeApi = RetrofitClient.getInstance().create(SondeApi.class);
        Call<MeasureDetailResponse> call = sondeApi.getMeasureDetail(accessToken, RESPIRATORY_SYMPTOMS_RISK);
        call.enqueue(new Callback<MeasureDetailResponse>() {
            @Override
            public void onResponse(Call<MeasureDetailResponse> call, Response<MeasureDetailResponse> response) {
                MeasureDetailResponse measureDetailResponse = response.body();
                if (measureDetailResponse != null) {
                    Log.i(TAG, "getRespiratorySymptomsRiskDetails onResponse: " + measureDetailResponse.getId());
                    getQuestionnaire(measureDetailResponse.getQuestionnaire().getId(), measureDetailResponse.getQuestionnaire().getLanguages().get(0));
                } else {
                    dismissDialog();
                    Log.d(TAG, "getRespiratorySymptomsRiskDetails error : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MeasureDetailResponse> call, Throwable t) {
                dismissDialog();
                Log.d(TAG, "getRespiratorySymptomsRiskDetails onFailure : " + t);
            }
        });
    }

    private void getQuestionnaire(String questionnaireId, String language) {
        mProgressDialog.setMessage("fetching questionnaire data...");
        SondeApi sondeApi = RetrofitClient.getInstance().create(SondeApi.class);
        Call<QuestionnaireResponse> call = sondeApi.getQuestionnaire(accessToken, questionnaireId, language);
        call.enqueue(new Callback<QuestionnaireResponse>() {
            @Override
            public void onResponse(Call<QuestionnaireResponse> call, Response<QuestionnaireResponse> response) {
                QuestionnaireResponse questionnaireResponse = response.body();
                if (questionnaireResponse != null) {
                    Log.i(TAG, "getQuestionnaire onResponse: " + questionnaireResponse.getId());

                    resSymptomsRiskQuestions = questionnaireResponse;
                    lunchQuestionnaireActivity(resSymptomsRiskQuestions);
                } else {
                    dismissDialog();
                    Log.d(TAG, "getQuestionnaire error : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionnaireResponse> call, Throwable t) {
                dismissDialog();
                Log.d(TAG, "getQuestionnaire onFailure : " + t);
            }
        });
    }

    private void lunchQuestionnaireActivity(QuestionnaireResponse resSymptomsRiskQuestions) {
        Intent intent = new Intent(RespiratorySymptomsRiskActivity.this, QuestionnaireActivity.class);
        intent.putParcelableArrayListExtra(QuestionnaireActivity.EXTRA_KEY_QUESTIONNAIRE_DATA, resSymptomsRiskQuestions.getQuestions());
        startActivityForResult(intent, QUESTIONNAIRE_REQUEST_CODE);
    }

    private void postAnswerResponse(ArrayList<UserAnswer> userAnswers) {
        mProgressDialog.setMessage("Posting answer response..");
        mProgressDialog.show();
        QuestionnaireUserResponse questionnaireUserResponse = new QuestionnaireUserResponse(resSymptomsRiskQuestions.getId(),
                resSymptomsRiskQuestions.getLanguage(),
                userIdentifier,
                DateUtils.getDateString(System.currentTimeMillis()),
                userAnswers);

        PostAnswersRequest postAnswersRequest = new PostAnswersRequest(questionnaireUserResponse);

        SondeApi sondeApi = RetrofitClient.getInstance().create(SondeApi.class);
        Call<PostAnswerResponse> call = sondeApi.postAnswerResponse(accessToken, postAnswersRequest);
        call.enqueue(new Callback<PostAnswerResponse>() {
            @Override
            public void onResponse(Call<PostAnswerResponse> call, Response<PostAnswerResponse> response) {
                PostAnswerResponse postAnswerResponse = response.body();
                if (postAnswerResponse != null) {
                    requestScore(resSymptomsRiskAudioFilePath, postAnswerResponse.getId());
                } else {
                    dismissDialog();
                    Log.d(TAG, "postAnswerResponse error : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PostAnswerResponse> call, Throwable t) {
                dismissDialog();
                Log.d(TAG, "postAnswerResponse onFailure : " + t);
            }
        });
    }
}