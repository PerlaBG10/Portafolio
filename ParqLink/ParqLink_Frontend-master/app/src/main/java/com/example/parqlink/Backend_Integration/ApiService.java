package com.example.parqlink.Backend_Integration;

import com.example.parqlink.DTO.ParkingResponse;
import com.example.parqlink.DTO.LoginRequest;
import com.example.parqlink.DTO.NfcScanRequest;
import com.example.parqlink.DTO.ParkingSessionResponse;
import com.example.parqlink.DTO.ProfileResponse;
import com.example.parqlink.DTO.RegisterRequest;
import com.example.parqlink.DTO.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import java.util.List;

public interface ApiService {
    @GET("parking/all")
    Call<List<ParkingResponse>> getFilteredParkings(
            @Query("name") String name,
            @Query("address") String address,
            @Query("lat") Double latitude,
            @Query("lng") Double longitude,
            @Query("maxDistance") Double maxDistance,
            @Query("maxPrice") Double maxPrice,
            @Query("sortBy") String sort,
            @Query("order") String direction,
            @Query("page") int page,
            @Query("size") int size
    );

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("parking/scan")
    Call<ParkingSessionResponse> scan(@Header("Authorization") String authToken, @Body NfcScanRequest scan);

    @GET("/api/parking/sessions")
    Call<List<ParkingSessionResponse>> getUserSessions(@Header("Authorization") String bearerToken);

    @GET("/api/user/me")
    Call<ProfileResponse> getUserProfile(@Header("Authorization") String bearerToken);

}



