package com.example.bankapp.api;

import com.example.bankapp.models.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {

    @POST("/api/users/create")
    Call<User> createUser(@Body User user);

    @POST("/api/users/login")
    Call<User> loginUser(@Body LoginRequest loginRequest);

    @POST("/api/users/deposit")
    Call<User> deposit(@Body DepositRequest depositRequest);

    @POST("/api/users/withdraw")
    Call<User> withdraw(@Body WithdrawRequest withdrawRequest);

    @GET("/api/users/email/{email}")
    Call<User> getUserByEmail(@Path("email") String email);

    @GET("/api/transfers/history/{accountNumber}")
    Call<List<TransferHistory>> getTransferHistory(@Path("accountNumber") String accountNumber);

    @POST("/api/transfers/execute")
    Call<TransferResponse> executeTransfer(@Body TransferRequest transferRequest);

    @GET("/api/transactions/history/{accountNumber}")
    Call<List<Transaction>> getAccountTransactions(
            @Path("accountNumber") String accountNumber,
            @Query("days") int days
    );


    @GET("/api/users/profile")
    Call<User> getUserProfile(@Query("email") String email);

    @PUT("/api/users/update-profile")
    Call<Void> updateProfile(@Body ProfileUpdateRequest updateRequest);

    @POST("/api/users/change-password")
    Call<Void> changePassword(@Body PasswordChangeRequest passwordRequest);
}