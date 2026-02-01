package com.example.bankapp.api;

import com.example.bankapp.models.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ApiService {

    @POST("/api/users/create")
    Call<User> createUser(@Body User user);

    @POST("/api/users/login")
    Call<User> loginUser(@Body LoginRequest loginRequest);

    @POST("/api/users/deposit/pln")
    Call<User> depositPln(@Body DepositRequest depositRequest);

    @POST("/api/users/deposit/eur")
    Call<User> depositEur(@Body DepositRequest depositRequest);

    @POST("/api/users/deposit/usd")
    Call<User> depositUsd(@Body DepositRequest depositRequest);

    @POST("/api/users/withdraw/pln")
    Call<User> withdrawPln(@Body WithdrawRequest withdrawRequest);

    @POST("/api/users/withdraw/eur")
    Call<User> withdrawEur(@Body WithdrawRequest withdrawRequest);

    @POST("/api/users/withdraw/usd")
    Call<User> withdrawUsd(@Body WithdrawRequest withdrawRequest);

    @GET("/api/users/email/{email}")
    Call<User> getUserByEmail(@Path("email") String email);

    @GET("/api/users/email/{email}/response")
    Call<User> getUserResponseByEmail(@Path("email") String email);

    @GET("/api/users/{userId}/balances")
    Call<Map<String, BigDecimal>> getAllBalances(@Path("userId") Long userId);

    @GET("/api/users/{userId}")
    Call<User> getUserById(@Path("userId") Long userId);

    @POST("/api/users/{userId}/exchange")
    Call<User> exchangeCurrency(
            @Path("userId") Long userId,
            @Query("fromCurrency") String fromCurrency,
            @Query("toCurrency") String toCurrency,
            @Query("amount") BigDecimal amount
    );

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

    @POST("/api/virtual-cards/create")
    Call<VirtualCardResponse> createVirtualCard(@Body VirtualCardCreateRequest request);

    @GET("/api/virtual-cards/user/{userId}")
    Call<VirtualCardResponse> getVirtualCard(@Path("userId") Long userId);

    @PUT("/api/virtual-cards/{cardId}/settings")
    Call<VirtualCardResponse> updateCardSettings(
            @Path("cardId") Long cardId,
            @Body CardSettingsRequest request
    );

    @PUT("/api/virtual-cards/{cardId}/pin")
    Call<VirtualCardResponse> updatePin(
            @Path("cardId") Long cardId,
            @Body PinChangeRequest request
    );

    @PUT("/api/virtual-cards/{cardId}/block")
    Call<VirtualCardResponse> blockCard(@Path("cardId") Long cardId);

    @POST("/api/exchange")
    Call<User> exchangeCurrency(@Body CurrencyExchangeRequest request);

    @GET("exchange/rate")
    Call<ExchangeResponse> getExchangeRate(
            @Query("from") String fromCurrency,
            @Query("to") String toCurrency
    );

    @POST("exchange/perform")
    Call<ExchangeResponse> performExchange(@Body ExchangeRequest exchangeRequest);

}