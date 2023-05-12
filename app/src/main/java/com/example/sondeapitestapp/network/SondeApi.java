package com.example.sondeapitestapp.network;

import com.example.sondeapitestapp.model.AccessTokenResponse;
import com.example.sondeapitestapp.model.AllMeasuresResponse;
import com.example.sondeapitestapp.model.MeasureDetailResponse;
import com.example.sondeapitestapp.model.PostAnswerResponse;
import com.example.sondeapitestapp.model.PostAnswersRequest;
import com.example.sondeapitestapp.model.QuestionnaireResponse;
import com.example.sondeapitestapp.model.S3FilePathRequest;
import com.example.sondeapitestapp.model.S3FilePathResponse;
import com.example.sondeapitestapp.model.ScoreRequest;
import com.example.sondeapitestapp.model.ScoreResponse;
import com.example.sondeapitestapp.model.SignUpRequest;
import com.example.sondeapitestapp.model.SignUpResponse;
import com.example.sondeapitestapp.model.VoiceFeatureJobResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface SondeApi {

    //------------For Token and Login /Signup----------------
    @FormUrlEncoded
    @POST("platform/v1/oauth2/token")
    Call<AccessTokenResponse> getAccessToken(@Header("Authorization") String authorization, @Field("grant_type") String grantType);

    @POST("platform/v2/users")
    Call<SignUpResponse> signUpUser(@Header("Authorization") String authorization, @Body SignUpRequest signUpRequest);

    //------------For Token and Login /Signup End----------------

    //------------For File upload ----------------
    @POST("platform/v1/storage/files")
    Call<S3FilePathResponse> getS3FilePath(@Header("Authorization") String authorization, @Body S3FilePathRequest s3FilePathRequest);

    @PUT
    Call<ResponseBody> uploadFileToS3(@Url String url, @Body RequestBody requestBody);

    //------------For File upload End----------------

    //------------For Mental Fitness Score----------------
    @POST("platform/async/v1/inference/voice-feature-scores")
    Call<VoiceFeatureJobResponse> getJobIdForVoiceFeature(@Header("Authorization") String authorization, @Body ScoreRequest scoreRequest);

    @GET("platform/async/v1/inference/voice-feature-scores/{asyncJobId}")
    Call<ScoreResponse> getVoiceFeatureScore(@Header("Authorization") String authorization, @Path("asyncJobId") String asyncJobId);

    //----------For Mental Fitness Score End----------------

    @GET("platform/v1/measures/name/{measureName}")
    Call<MeasureDetailResponse> getMeasureDetail(@Header("Authorization") String authorization, @Path("measureName") String measureName);

    @GET("platform/v1/questionnaires/{questionnaireId}")
    Call<QuestionnaireResponse> getQuestionnaire(@Header("Authorization") String authorization, @Path("questionnaireId") String measureName, @Query("language") String language);

    @POST("platform/v1/questionnaire-responses")
    Call<PostAnswerResponse> postAnswerResponse(@Header("Authorization") String authorization, @Body PostAnswersRequest postAnswersRequest);
}
