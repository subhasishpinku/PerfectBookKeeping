package perfect.book.keeping.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import perfect.book.keeping.request.AddCard;
import perfect.book.keeping.request.AmountUpdateRequest;
import perfect.book.keeping.request.CompanyMethodRequest;
import perfect.book.keeping.request.CompanyUpdateRequest;
import perfect.book.keeping.request.CreateCompanyRequest;
import perfect.book.keeping.request.FileUpdateRequest;
import perfect.book.keeping.request.RemoveSubUser;
import perfect.book.keeping.request.SubUserModifyRequest;
import perfect.book.keeping.request.SubUserRequest;
import perfect.book.keeping.request.UpdateCard;
import perfect.book.keeping.request.UpdateImageRequest;
import perfect.book.keeping.request.UpdateUserRequest;
import perfect.book.keeping.request.Verify;
import perfect.book.keeping.response.AccessCode;
import perfect.book.keeping.response.AddCardResponse;
import perfect.book.keeping.response.AddCompany;
import perfect.book.keeping.response.AmountUpdateResponse;
import perfect.book.keeping.response.AuthResponse;
import perfect.book.keeping.response.BookKeeperResponse;
import perfect.book.keeping.response.ChangePassword;
import perfect.book.keeping.response.CompanyModifyResponse;
import perfect.book.keeping.response.CompanyResponse;
import perfect.book.keeping.response.CountryStateResponse;
import perfect.book.keeping.response.CurrencyResponse;
import perfect.book.keeping.response.DateFormat;
import perfect.book.keeping.response.DeleteFile;
import perfect.book.keeping.response.FileUpdateResponse;
import perfect.book.keeping.response.FilterSubUserResponse;
import perfect.book.keeping.response.ForgotPassResponse;
import perfect.book.keeping.response.AddressInfo;
import perfect.book.keeping.response.LogoutResponse;
import perfect.book.keeping.response.ModifyCompany;
import perfect.book.keeping.response.OTPResponse;
import perfect.book.keeping.response.PNLResponse;
import perfect.book.keeping.response.Packages;
import perfect.book.keeping.response.ReSubmitResponse;
import perfect.book.keeping.response.ReceiptResponse;
import perfect.book.keeping.response.RefreshTokenResponse;
import perfect.book.keeping.response.RemoveCards;
import perfect.book.keeping.response.Role;
import perfect.book.keeping.response.SendInvite;
import perfect.book.keeping.response.Signup;
import perfect.book.keeping.response.SnapResponse;
import perfect.book.keeping.response.StateResponse;
import perfect.book.keeping.response.SubPackage;
import perfect.book.keeping.response.SubUser;
import perfect.book.keeping.response.SubUserList;
import perfect.book.keeping.response.UpdateCardResponse;
import perfect.book.keeping.response.UserProfileUpdate;
import perfect.book.keeping.response.UserResponse;
import perfect.book.keeping.response.UserUpdateResponse;
import perfect.book.keeping.response.ValidTokenResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    //Sign Up
    @FormUrlEncoded
    @POST("signup")
    Call<Signup> createAccount(
            @Field("email") String email,
            @Field("password") String password,
            @Field("name") String name,
            @Field("phone") String phone
    );

    //Verify Email
    @POST("login/access-code")
    Call<AccessCode> accessCode(
            @Body Verify verify
    );
    //Login API
    @FormUrlEncoded
    @POST("login")
    Call<AuthResponse> authUser(
            @Field("access_code") String access_code,
            @Field("password") String password,
            @Field("email") String email,
            @Field("fcm_token") String fcm_token

    );

    //Refresh Token
    @FormUrlEncoded
    @POST("refresh")
    Call<RefreshTokenResponse> refreshToken(
            @Field("token") String token
    );
    //Company List
    @GET("companies")
    Call<CompanyResponse> companies(
            @Header("Authorization") String auth,
            @Query("ids") String ids
    );

    //Get Single Company
    @GET("companies")
    Call<CompanyModifyResponse> getCompany(
            @Header("Authorization") String auth,
            @Query("ids") String ids
    );

    //Request OTP For Forgot Password
    @FormUrlEncoded
    @POST("request-reset-password")
    Call<ForgotPassResponse> requestOTP(
            @Field("email") String email,
            @Field("mac_address") String mac_address,
            @Field("device_type") String device_type
    );
    // Verify OTP
    @FormUrlEncoded
    @POST("verify-otp")
    Call<OTPResponse> verify(
            @Field("email") String email,
            @Field("otp") String otp,
            @Field("access_code") String access_code,
            @Field("mac_address") String mac_address,
            @Field("device_type") String device_type
    );
    //Update Password
    @FormUrlEncoded
    @POST("reset-password")
    Call<UserUpdateResponse> updatePass(
            @Field("email") String email,
            @Field("access_code") String access_code,
            @Field("password") String password
    );
    //Get User Records
    @GET("users")
    Call<UserResponse> user(
            @Header("Authorization") String auth
    );

    //Get State and Country
    @GET("country-state")
    Call<CountryStateResponse> countryState(
            @Query("filter") String type
    );

    @GET("country-state")
    Call<StateResponse> state(
            @Query("filter") String type
    );

    //User Address
    @GET("address")
    Call<AddressInfo> addressInfo(
            @Header("Authorization") String auth
    );

    //User Profile
    @PUT("users/profile")
    Call<UserProfileUpdate> profileUpdate(
            @Header("Authorization") String auth,
            @Body UpdateUserRequest updateUsers
    );

    @GET("users/profile")
    Call<UserResponse> userProfile(
            @Header("Authorization") String auth
    );

    @PUT("users/profile")
    Call<UserUpdateResponse> updateProfileImage(
            @Body UpdateImageRequest imageRequest,
            @Header("Authorization") String auth
    );

    @POST("files/uploads")
    Call<SnapResponse> uploadSnap(
            @Header("Authorization") String auth,
            @Body JsonElement imageData
    );

    @PUT("files/{fileid}")
    Call<ReSubmitResponse> reSubmitFile(
            @Header("Authorization") String auth,
            @Path("fileid") int fileid,
            @Body JsonElement imageData
    );

    @GET("files")
    Call<JsonObject> getGallery(
            @Header("Authorization") String auth,
            @Query("ids") String imageId,
            @Query("orderType") String orderType,
            @Query("orderField") String orderField,
            @Query("from_date") String fromDate,
            @Query("to_date") String toDate,
            @Query("company_id") int company_id,
            @Query("approval_status") String approval_status,
            @Query("created_by") String created_by,
            @Query("title") String title
    );
    @GET("files")
    Call<JsonObject> getGallery1(
            @Header("Authorization") String auth,
            @Query("ids") String imageId,
            @Query("orderType") String orderType,
            @Query("orderField") String orderField,
            @Query("from_date") String fromDate,
            @Query("to_date") String toDate,
            @Query("company_id") int company_id
//            @Query("approval_status") String approval_status,
//            @Query("created_by") String created_by
    );
    @GET("files")
    Call<JsonObject> singleImage(
            @Header("Authorization") String auth,
            @Query("ids") Integer imageId
    );

    @GET("payments")
    Call<ReceiptResponse> receipt(
            @Header("Authorization") String auth,
            @Query("year") String year,
            @Query("company_id") int company_id
    );

    @GET("files")
    Call<JsonObject> getZip(
            @Header("Authorization") String auth,
            @Query("ids") String imageId,
            @Query("zip") boolean zip
    );

    //Save Sub Users
    @POST("users/subuser")
    Call<SubUser> subUser(
            @Header("Authorization") String auth,
            @Body SubUserRequest subUserRequest
    );

    @GET("users/subuser")
    Call<SubUserList> subUserList(
            @Header("Authorization") String auth,
            @Query("company_id") String company_id,
            @Query("sub_user_id") String ids,
            @Query("role_id") String role_id
    );

    //ALT SUB USER LIST
    @GET("users/subuser")
    Call<SubUserList> subUsersList(
            @Header("Authorization") String auth,
            @Query("") String company_id
    );

    @PUT("users/subuser")
    Call<SubUser> updateSubUser(
            @Header("Authorization") String auth,
            @Body SubUserModifyRequest subUserModifyRequest
    );

    @HTTP(method = "DELETE", path = "users/subuser", hasBody = true)
    Call<SubUser> removeSubUser(
            @Header("Authorization") String auth,
            @Body RemoveSubUser removeSubUser
    );

    @PUT("users/subuser/transaction")
    Call<AmountUpdateResponse> updateAmount(
            @Header("Authorization") String auth,
            @Body AmountUpdateRequest amountUpdateRequest
    );

    //Valid Token
    @GET("valid-token")
    Call<ValidTokenResponse> checkValidToken(
            @Header("Authorization") String auth
    );

    //Change Password
    @FormUrlEncoded
    @POST("change-password")
    Call<ChangePassword> changePassword(
            @Header("Authorization") String auth,
            @Field("old_password") String old_password,
            @Field("new_password") String new_password
    );

    //Remove files
    @DELETE("files")
    Call<DeleteFile> removeFile(
            @Header("Authorization") String auth,
            @Query("ids") String ids
    );

    @POST("files/change-status")
    Call<FileUpdateResponse> updateFile(
            @Header("Authorization") String auth,
            @Body FileUpdateRequest fileUpdateRequest
            );

    //Add New Card
    @POST("cards")
    Call<AddCardResponse> addCard(
            @Header("Authorization") String auth,
            @Body AddCard addCard
            );

    //UPDATE EXISTING CARD
    @PUT("cards/{id}")
    Call<UpdateCardResponse> updateCard(
            @Header("Authorization") String auth,
            @Path("id") int cardId,
            @Body UpdateCard updateCard
    );

    //DELETE CARD
    @DELETE("cards")
    Call<RemoveCards> removeCard(
            @Header("Authorization") String auth,
            @Query("ids") int ids
    );

    //GET ALL Packages
    @GET("packages")
    Call<Packages> packages();

    //GEt Sub Package
    @GET("packages")
    Call<SubPackage> subPackages(
            @Query("subs_package_id") String subs_package_id
    );

    //ADD COMPANY
    @POST("companies")
    Call<AddCompany> addCompany(
            @Header("Authorization") String auth,
            @Body CreateCompanyRequest createComp
    );
    //Company Update
    @PUT("companies/{id}")
    Call<ModifyCompany> updateCompany(
            @Header("Authorization") String auth,
            @Path("id") int userId,
            @Body CompanyUpdateRequest updateComp
    );
    @PUT("cards/{id}")
    Call<ModifyCompany> updateCompanyMethod(
            @Header("Authorization") String auth,
            @Path("id") int userId,
            @Body CompanyMethodRequest updateComp
    );

    //ROLE
    @GET("roles/adduser")
    Call<Role> getRole(
            @Header("Authorization") String auth,
            @Query("company_id") int company_id
    );

    //Currency
    @GET("currency")
    Call<CurrencyResponse> getCurrency();

    //Date Format
    @GET("dateformats")
    Call<DateFormat> getDateFormat(
            @Header("Authorization") String auth
    );
    //Profit and Loss
    @GET("profitloss")
    Call<PNLResponse> pnl(
            @Header("Authorization") String auth,
            @Query("company_id") String company_id,
            @Query("status") String status,
            @Query("year") String year
    );

    //Book Keepers
    @GET("users/bookkeeper")
    Call<BookKeeperResponse> bookKeeper(
            @Header("Authorization") String auth,
            @Query("company_id") String company_id,
            @Query("ids") String status
    );

    //Log Out
    @FormUrlEncoded
    @POST("logout")
    Call<LogoutResponse> logOut(
            @Header("Authorization") String auth,
            @Field("fcm_token") String fcm_token,
            @Field("mac_address") String mac_address
    );

    // Filter Sub User List
    @GET("users/filtersubusers")
    Call<FilterSubUserResponse> filterSubUsers(
            @Header("Authorization") String auth,
            @Query("company_id") String company_id
    );

    //Send Invitation
    @POST("users/subuser/inviteresend")
    Call<SendInvite> sendInvitation(
            @Header("Authorization") String auth,
            @Query("company_id") String company_id,
            @Query("subuser_id") String subuser_id
    );


}
