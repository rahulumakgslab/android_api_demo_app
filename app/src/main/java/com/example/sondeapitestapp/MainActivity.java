package com.example.sondeapitestapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sondeapitestapp.model.AccessTokenResponse;
import com.example.sondeapitestapp.model.AllMeasuresResponse;
import com.example.sondeapitestapp.model.S3FilePathRequest;
import com.example.sondeapitestapp.model.S3FilePathResponse;
import com.example.sondeapitestapp.model.ScoreRequest;
import com.example.sondeapitestapp.model.ScoreResponse;
import com.example.sondeapitestapp.model.SignUpRequest;
import com.example.sondeapitestapp.model.SignUpResponse;
import com.example.sondeapitestapp.model.VoiceFeatureJobResponse;
import com.example.sondeapitestapp.network.RetrofitClient;
import com.example.sondeapitestapp.network.SondeApi;
import com.example.sondeapitestapp.utils.Configuration;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private String accessToken;
    private ProgressDialog mProgressDialog;
    private String userIdentifier;
    private AllMeasuresResponse allMeasuresList;

    private int RECORD_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressDialog = new ProgressDialog(MainActivity.this);

        getAccessToken();

        findViewById(R.id.buttonEmotionalFitness).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RecordingActivity.class);
                startActivityForResult(intent, RECORD_REQUEST_CODE);
            }
        });

        findViewById(R.id.buttonRespiratorySymptomsRisk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RespiratorySymptomsRiskActivity.class);
                intent.putExtra(RespiratorySymptomsRiskActivity.EXTRA_KEY_ACCESS_TOKEN, accessToken);
                intent.putExtra(RespiratorySymptomsRiskActivity.EXTRA_KEY_USER_IDENTIFIER, userIdentifier);

                startActivity(intent);
            }
        });

    }

    private void getAccessToken() {
        mProgressDialog.setMessage("Getting access token..");
        String authorization = getAuthorizationHeader();
        SondeApi sondeApi = RetrofitClient.getInstance().create(SondeApi.class);
        Call<AccessTokenResponse> accessTokenCall = sondeApi.getAccessToken(authorization, Configuration.GRANT_TYPE);
        accessTokenCall.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                Log.d(TAG, "AccessToken Response : " + response.toString());
                AccessTokenResponse accessTokenResponse = response.body();
                if (accessTokenResponse != null) {
                    accessToken = accessTokenResponse.getAccessToken();
                    Log.d(TAG, "AccessToken  : " + accessToken);
                    registerUser();
                } else {
                    dismissDialog();
                    Log.d(TAG, "getAccessToken onResponse error : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                Log.d(TAG, "Access token onFailure: " + t);
                dismissDialog();
            }
        });

    }

    private void registerUser() {
        mProgressDialog.setMessage("Signing up user..");
        mProgressDialog.show();
        SignUpRequest signUpRequest = new SignUpRequest("1993", "MALE", "ENGLISH");
        SondeApi sondeApi = RetrofitClient.getInstance().create(SondeApi.class);
        Call<SignUpResponse> call = sondeApi.signUpUser(accessToken, signUpRequest);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.body() != null) {
                    userIdentifier = response.body().getUserIdentifier();
                    Log.d(TAG, "registerUser Response : " + userIdentifier);
//                    getMeasures();
                    dismissDialog();
                } else {
                    dismissDialog();
                    Log.d(TAG, "registerUser onResponse error : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                dismissDialog();
                Log.d(TAG, "registerUser onFailure : " + t);
            }
        });
    }


    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
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
        }
    }

    private String getAuthorizationHeader() {
        String data = Configuration.CLIENT_ID + ":" + Configuration.CLIENT_SECRET;
        return Configuration.AUTH_BASIC + Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);
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
//                    requestScore(s3PathResponse.getFilePath());
                    getJobIdForVoiceFeature(s3PathResponse.getFilePath());
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


    private void getJobIdForVoiceFeature(String filePath) {
        mProgressDialog.setMessage("Getting feature job..");
        ScoreRequest scoreRequest = new ScoreRequest(userIdentifier, filePath, "mental-fitness");
        SondeApi sondeApi = RetrofitClient.getInstance().create(SondeApi.class);
        Call<VoiceFeatureJobResponse> call = sondeApi.getJobIdForVoiceFeature(accessToken, scoreRequest);
        call.enqueue(new Callback<VoiceFeatureJobResponse>() {
            @Override
            public void onResponse(Call<VoiceFeatureJobResponse> call, Response<VoiceFeatureJobResponse> response) {
                if (response.body() != null) {
                    getVoiceFeatureScore(response.body());
                } else {
                    dismissDialog();
                    Log.d(TAG, "getJobIdForVoiceFeature error : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<VoiceFeatureJobResponse> call, Throwable t) {
                dismissDialog();
                Log.d(TAG, "getJobIdForVoiceFeature onFailure : " + t);
            }
        });
    }

    private void getVoiceFeatureScore(VoiceFeatureJobResponse featureJobResponse) {
        mProgressDialog.setMessage("Getting Scores..");
        SondeApi sondeApi = RetrofitClient.getInstance().create(SondeApi.class);
        Call<ScoreResponse> call = sondeApi.getVoiceFeatureScore(accessToken, featureJobResponse.getJobId());
        call.enqueue(new Callback<ScoreResponse>() {
            @Override
            public void onResponse(Call<ScoreResponse> call, Response<ScoreResponse> response) {
                if (response.body() != null) {
                    if (response.body().getStatus().equals("DONE")) {
                        dismissDialog();
                        String score=response.body().getResult().getInference().get(0).getScore().getValue();
                        ((TextView) findViewById(R.id.textViewScore)).setText("Your MF Score is "+score);
                    } else {
                        getVoiceFeatureScore(featureJobResponse); //Call this in loop until we get status=DONE and to get status DONE takes 5-6 sec.
                    }
                } else {
                    dismissDialog();
                    Log.d(TAG, "getVoiceFeatureScore error : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ScoreResponse> call, Throwable t) {
                dismissDialog();
                Log.d(TAG, "getVoiceFeatureScore onFailure : " + t);
            }
        });
    }


}