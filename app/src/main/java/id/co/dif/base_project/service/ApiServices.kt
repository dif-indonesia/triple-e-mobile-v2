package id.co.dif.base_project.service

import id.co.dif.base_project.base.BaseRca
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.data.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiServices {

    @GET("location")
    suspend fun getListLocation(): BaseResponse<Location>

    @GET("site")
    suspend fun getListSite(): BaseResponse<Location>

    @GET("site/{site_name}")
    suspend fun getSiteByName(
        @Path("site_name") siteName: String
    ): BaseResponse<List<Location>>

    @GET("ops_ticket/get-engineer")
    suspend fun getNearestTechnician(
        @Header("Authorization") bearerToken: String?,
        @Query("id_site") idSite: Int?,
    ): BaseResponseList<Location>

    @GET("project?category=TT Map Me")
    suspend fun mapmedetil(
        @Header("Authorization") bearerToken: String?,
    ): BaseResponseList<Location>

    @GET("ops_ticket/")
    suspend fun getListTroubleTicket(
//        @Query("search") search: String?,
//        @Query("sortBy") sortBy: String?,
//        @Query("status") status: String?,
//        @Query("start_date") from: String?,
//        @Query("end_date") until: String?,
        @Header("Authorization") bearerToken: String?,
        @QueryMap param: MutableMap<String, Any?>,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): BaseResponseList<TroubleTicket>


    @GET("ops_ticket/my_ticket")
    suspend fun getMyTicket(
        @Header("Authorization") bearerToken: String?,
        @QueryMap param: MutableMap<String, Any?>,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): BaseResponseList<TroubleTicket>


//    @GET("ops_ticket/")
//    suspend fun filterTroubleTicket(
//        @Query("status") status: String?,
//        @Query("start_date") from: String?,
//        @Query("end_date") until: String?,
////        @Body param: MutableMap<String, Any?>
////    ): BaseResponse<List<Location>>
//    ): BaseResponseList<TroubleTicket>


    @POST("trouble_ticket")
    suspend fun createTroubleTicket(
        @Body param: TroubleTicket
    ): BaseResponse<TroubleTicket>


    @POST("login/employee")
    suspend fun postLogin(
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<LoginData?>

    @PUT("reset_password")
    suspend fun resetPassword(
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Any>

    @POST("forget_password")
    suspend fun forgetpassword(
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<ResponseForgetPassword>


    @PUT("password/change")
    suspend fun changepassword(
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>,
    ): BaseResponse<Int>

    @POST("employee")
    suspend fun postRegister(
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Session>


    @Multipart
    @PUT("employee/profile/photo")
    suspend fun uploadphoto(
        @Header("Authorization") bearerToken: String?,
        @Part photo: MultipartBody.Part
    ): BaseResponse<MutableMap<String, Any?>>

//    @Multipart
//    @PUT("employee/")
//    suspend fun employe(
//        @Header("Authorization") bearerToken : String?,
//    ): BaseResponse<MutableMap<String, Any?>>


    @Multipart
    @PUT("employee/profile/cover")
    suspend fun uploadcover(
        @Header("Authorization") bearerToken: String?,
        @Part photo: MultipartBody.Part
    ): BaseResponse<MutableMap<String, Any?>>

    @Multipart
    @PUT("employee/profile/update/files")
    suspend fun uploadfile(
        @Header("Authorization") bearerToken: String?,
        @Part param: MutableList<MultipartBody.Part?>
    ): BaseResponse<Any>


    @PUT("employee/profile/update")
    suspend fun editprofile(
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<MutableMap<String, Any?>>

    //  Basic Info
    @GET("employee/{id}")
    suspend fun getDetailProfile(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: Int?
    ): BaseResponse<BasicInfo>

    @GET("{id}/verify_otp")
    suspend fun getresponotp(
        @Path("id") id: String?,
//        @Header("Authorization") bearerToken : String?
    ): BaseResponse<Session>


//
//    @PUT("editProfile")
//    suspend fun putEditProfile(
//        @Body param: MutableMap<String, Any?>
//    ): BaseResponse<MutableMap<String, Any?>>

    @POST("{id}/verify_otp")
    suspend fun postOtp(
        @Path("id") id: Int?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Session>


    @PUT("employee/work")
    suspend fun putworkadd(
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Work>


    @PUT("employee/work/edit/{id}")
    suspend fun putworkedit(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Work>


    @GET("employee/work/lists/{id}")
    suspend fun getWorkList(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: Int?
    ): BaseResponseList<Work>


    @DELETE("employee/work/delete/{id}")
    suspend fun deleteWork(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: String?
    ): BaseResponse<Work>

    @PUT("employee/education")
    suspend fun addeducation(
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<MutableMap<String, Any?>>

    @PUT("account-setting")
    suspend fun updateAccountSettings(
        @Header("Authorization") bearerToken: String?,
        @Body param: AccountSettings
    ): BaseResponse<Any?>
    @GET("employee/education/lists/{id}")
    suspend fun getEducationList(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: Int?
    ): BaseResponseList<Education>

    @DELETE("employee/education/delete/{id}")
    suspend fun deleteEducation(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: String?
    ): BaseResponse<Education>

    @PUT("employee/education/edit/{id}")
    suspend fun editeducation(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Education>

    @GET("ops_ticket/{id}")
    suspend fun getTicketDetails(
        @Path("id") id: String?,
        @Header("Authorization") bearerToken: String?,
//        @Body param: MutableMap<String, Any?>
    ): BaseResponse<TicketDetails>

    @GET("form_req_ret/list-sparepart-ticket")
    suspend fun getListSparePart(
        @Header("Authorization") bearerToken: String?,
        @Query("tic_id") ticketId: String?,
    ): BaseResponseList<SparePart>

    @GET("project/regional")
    suspend fun getListRegional(
        @Header("Authorization") bearerToken: String?,
    ): BaseResponseList<Regional>

    @PUT("project/my/project")
    suspend fun updateMyProject(
        @Header("Authorization") bearerToken: String?,
        @Query("project") project: String?,
    ): BaseResponse<Any>

    @GET("project/project")
    suspend fun getListProject(
        @Header("Authorization") bearerToken: String?,
    ): BaseResponseList<Any>
    @GET("project/my/regional")
    suspend fun getMyListRegional(
        @Header("Authorization") bearerToken: String?,
        @Query("id") id: String?,
    ): BaseResponseList<Regional>
    @PUT("project/my/regional")
    suspend fun updateMyListRegional(
        @Header("Authorization") bearerToken: String?,
        @Body param: HashMap<String, MutableList<String>>?,
    ): BaseResponse<Any>

    @DELETE("ops_ticket/notes/{id}")
    suspend fun deleteNote(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: String?,
        @Query("note_id") note_id: Int?
    ): BaseResponse<Any>

    @GET("project?category=TT Site All")
    suspend fun site(
        @Header("Authorization") bearerToken: String?,
        @Query("search") search: String?,
//        @Body param: MutableMap<String, Any?>
//    ): BaseResponse<List<Location>>
    ): BaseResponseList<Location>

    @PUT("ops_ticket/update_notes/{id}")
    suspend fun editNotes(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: Int?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<MutableMap<String, Any?>>

    @GET("ops_ticket/get-radius-upload")
    suspend fun getEngineerIsWithinRadius(
        @Header("Authorization") bearerToken: String?,
        @Query("id_site") siteId: Int?,
        @Query("emp_id") engineerId: Int?,
    ): BaseResponse<EngineerWithinRadiusStatus>
    @GET("project/site/{id}")
    suspend fun getSiteById(
        @Path("id") id: Int?,
        @Header("Authorization") bearerToken: String?,
    ): BaseResponse<SiteData>

    @GET("project/site_detail/{id}")
    suspend fun getTicketBySiteId(
        @Path("id") id: Int?,
        @Query("page") page: Int?,
        @Query("limit") limit: Int?,
        @Header("Authorization") bearerToken: String?,
    ): BaseResponseList<TroubleTicket>


    @GET("project/site/{id}")
    suspend fun getListSite(
        @Path("id") id: Int?,
        @Header("Authorization") bearerToken: String?,
    ): BaseResponse<SiteHistory>


    @GET("project?category=TT Map All")
    suspend fun mapAlarm(
        @Header("Authorization") bearerToken: String?,
        @Query("search") search: String?,
//        @Body param: MutableMap<String, Any?>
//    ): BaseResponse<List<Location>>
    ): BaseResponseList<Location>

    @GET("project/mapme")
    suspend fun mapMe(
        @Header("Authorization") bearerToken: String?,
        @Query("search") search: String?,
//        @Body param: MutableMap<String, Any?>
//    ): BaseResponse<List<Location>>
    ): BaseResponseList<Location>

    @GET("project/map_alarm")
    suspend fun mapalarm(
        @Header("Authorization") bearerToken: String?,
        @Query("search") search: String?,
//        @Body param: MutableMap<String, Any?>
//    ): BaseResponse<List<Location>>
    ): BaseResponseList<Location>



//    @GET("project?search=&category=TT Site All")
//    suspend fun getSiteById(
//        @Header("Authorization") bearerToken: String?,
////        @Body param: MutableMap<String, Any?>
////    ): BaseResponse<List<Location>>
//    ): BaseResponse<Location>

    @GET("project?search=&category=TT Map Me")
    suspend fun getEngineer(
        @Header("Authorization") bearerToken: String?,
    ): BaseResponse<SearchengineerData>

    @POST("ops_ticket/")
    suspend fun addticket(
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<TroubleTicket>


    @PUT(" ops_ticket/{id}")
    suspend fun editdetailticket(
        @Path("id") id: Int?,
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<SiteData>

    @PUT("ops_ticket/file_upload/{id}")
    suspend fun uploadfileticket1(
//        @Path("id") id: Int,
        @Header("Authorization") bearerToken: String?,
        @Part photo: MultipartBody.Part
    ): BaseResponse<MutableMap<String, Any?>>

    @Multipart
    @PUT("ops_ticket/file_upload/{id}")
    suspend fun uploadfileticket(
        @Path("id") id: String?,
        @Header("Authorization") bearerToken: String?,
        @Part param: MutableList<MultipartBody.Part?>
    ): BaseResponse<Any>

    @GET("ops_ticket/dashboard_ticket_info")
    suspend fun getTicketInfo(
        @Header("Authorization") bearerToken: String?,
    ): BaseResponse<TicketInfo>

    @GET("login_log/active_users")
    suspend fun getActiveUsers(
        @Header("Authorization") bearerToken: String?,
        @Query("limit") limit: Int,
        @Query("page") page: Int

    ): BaseResponseList<ActiveUser>

    @GET("login_log/team_active")
    suspend fun getHourlyTeamUsageTrend(
        @Header("Authorization") bearerToken: String?,
    ): BaseResponseList<Int>

    @GET("login_log/users-info")
    suspend fun getUsersInfoDashboard(
        @Header("Authorization") bearerToken: String?,
    ): BaseResponse<UsersInfoDashboard>

    @GET("login_log/team_usage_trend")
    suspend fun getUsageTrend(
        @Header("Authorization") bearerToken: String?,
        @Query("type") type: String,
    ): BaseResponseList<PlainValueLabel>

    @POST("form_req_ret")
    suspend fun requestSparePart(
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<SparepartData>


    @POST("sesaion_log")
    suspend fun postSesionLog(
        @Header("Authorization") bearerToken: String?,
        @Query("state") state: String
    ): BaseResponse<SessionLogin>

    @GET("form_req_ret/get-data-param")
    suspend fun searchSparePartByName(
        @Header("Authorization") bearerToken: String?,
        @Query("search") search: String?,
    ): BaseResponseList<SparePartByName>

    @GET("form_req_ret/get-data-param")
    suspend fun searchSparePartByCode(
        @Header("Authorization") bearerToken: String?,
        @Query("search") search: String?,
    ): BaseResponseList<SparePartByCode>

    @GET("/form_req_ret/check-name-sn")
    suspend fun checkSparepart(
        @Header("Authorization") bearerToken: String?,
        @Query("find") query: String?,
        @Query("sn") sn: String?,
    ): BaseResponse<Any?>


    @GET("messages/list-receive-message")
    suspend fun getNotification(
        @Header("Authorization") bearerToken: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): BaseResponseList<MessageNotification>

//    @GET("form_req_ret/get-by-sn/{sn}")
//    suspend fun getSparepartBySN(
//        @Header("Authorization") bearerToken: String?,
//        @Body param: MutableMap<String, Any?>
//    ) : BaseResponse<SparepartData>


    @PUT("employee/profile/update/location")
    suspend fun putUpdateLocation(
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Any>

    @GET("ops_ticket/tt_ticket_info")
    suspend fun getTtTicketInfo(
        @Header("Authorization") bearerToken: String?,
        @Query("id") id: Int?
    ): BaseResponse<TroubleTicketTicketInfo>

    @GET("ops_ticket/weekly_ticket_comparison")
    suspend fun getTicketTrends(
        @Header("Authorization") bearerToken: String?,
    ): BaseResponse<TicketTrend>


    @GET("ops_ticket/number_of")
    suspend fun getNumberOfTickets(
        @Header("Authorization") bearerToken: String?,
        @Query("id") id: Int?
    ): BaseResponse<TicketNumbers>

    @GET("ops_ticket/area_bechmark")
    suspend fun getAreaBenchmarkData(
        @Header("Authorization") bearerToken: String?,
        @Query("id") id: Int?
    ): BaseResponse<AreaBenchmark>

    @GET("employment_assigment/overall_rank")
    suspend fun getOveralRank(
        @Header("Authorization") bearerToken: String?,
    ): BaseResponseList<AreaBenchmark>

    @GET("chat/list-receive")
    suspend fun getMessageNotification(
        @Header("Authorization") bearerToken: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): BaseResponseList<MessageNotification>

    @GET("chat/list-receive")
    suspend fun getChatNotification(
        @Header("Authorization") bearerToken: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): BaseResponseList<ChatNotification>

    @GET("employee/profile/validation")
    suspend fun getCompletedProfile(
        @Header("Authorization") bearerToken: String?,
        @Query("id") id: Int?
    ): BaseResponse<CompletedProfile>

    @GET("employee/login_activity")
    suspend fun getUserActivityLog(
        @Header("Authorization") bearerToken: String?,
        @Query("id") id: Int?
    ): BaseResponseList<PlainValueLabel>

    @GET("employee/name")
    suspend fun listName(
        @Header("Authorization") bearerToken: String?,
        @Query("search") search: String?,
        @Query("limit") limit: Int?,
        @Query("page") page: Int?
    ): BaseResponseList<Employee>


    @POST("messages/send-chat")
    suspend fun sendMessages(
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<SentMessage>

    @POST("chat/send-chat")
    suspend fun sendChat(
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Chat>

    @PUT("messages/read-message")
    suspend fun markNotificationAsRead(
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Any>

    @POST("notifications/unread")
    suspend fun getNotificationUnreadStatus(
        @Header("Authorization") bearerToken: String?,
    ): BaseResponse<NotificationUnreadStatus>

    @GET("check_version")
    suspend fun getLatestAppVersion(
        @Header("Authorization") bearerToken: String?,
    ): BaseResponse<LatestAppVersion>


    @GET("notifications/num_unread")
    suspend fun getUnreadNumber(
        @Header("Authorization") bearerToken: String?,
    ): BaseResponse<UnreadNumber>


    @POST("maps/directions")
    suspend fun getDirection(
        @Body param: MutableMap<String, Any?>,
        @Header("Authorization") bearerToken: String?,
    ): DirectionsResponse

    @DELETE("messages/delete-message/{id}")
    suspend fun deleteMessage(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: String?
    ): BaseResponse<Any>

    @DELETE("account-setting")
    suspend fun settingActivity(
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Any>

    @DELETE("chat/delete/{id}")
    suspend fun deleteChat(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: Int?
    ): BaseResponse<Any>

    @PUT("messages/read-message")
    suspend fun markMessageAsRead(
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Any>

    @PUT("chat/read")
    suspend fun markChatAsRead(
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Any>


    @POST("ops_ticket/site_notification")
    suspend fun broadcastLocationRequest(
        @Header("Authorization") bearerToken: String?,
        @Query("id_site") siteId: Int?
    ): BaseResponse<Any>

    @POST("ping_location")
    suspend fun pingEngineerToSendTheirLocation(
        @Header("Authorization") bearerToken: String?,
        @Query("id_user") userId: Int?
    ): BaseResponse<Any>

    @GET("ops_ticket/tt_dashboard_progress_bar")
    suspend fun getProgressBarConclusion(
        @Header("Authorization") bearerToken: String?,
        @Query("id") id: Int?
    ): BaseResponse<ProgressBarConclusion>

    @GET("ops_ticket/dashboard_tt_time_to_closed")
    suspend fun getTtTimeToClosed(
        @Header("Authorization") bearerToken: String?,
        @Query("id") id: Int?,
        @Query("filter") filter: String?
    ): BaseResponse<TtTimeToClosed>

    @GET("ops_ticket/ticketcount")
    suspend fun getDailyTicketTrend(
        @Header("Authorization") bearerToken: String?,
        @Query("id") id: Int?,
        @Query("days") days: Int?
    ): BaseResponseList<PlainValueLabel>

    @GET("ops_ticket/trouble_ticket_quality")
    suspend fun getTicketQuality(
        @Header("Authorization") bearerToken: String?,
        @Query("id") id: Int?,
    ): BaseResponse<TicketQuality>

    @GET("employee/ticket_handling")
    suspend fun getTicketHandling(
        @Header("Authorization") bearerToken: String?,
        @Query("id") id: Int?,
    ): BaseResponse<TicketHandling>

    @DELETE("form_req_ret/delete-ops-sparepart-request/{tic_id}")
    suspend fun deleteSparePartID(
        @Header("Authorization") bearerToken: String?,
        @Path("tic_id") ticketId: String?,
        @Query("spreq_id") spreq_id: String?,
    ): BaseResponse<Any?>
    @PUT("form_req_ret/edit-sparepart/{tic_id}")
    suspend fun editSparePartID(
        @Header("Authorization") bearerToken: String?,
        @Path("tic_id") ticketId: String?,
        @Query("spreq_id") spreq_id: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Any?>

    @POST("form_req_ret/add-sparepart")
    suspend fun addSparePartID(
        @Header("Authorization") bearerToken: String?,
        @Query("tic_id") ticketId: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Any?>

    @GET("employment_assigment/overall_rank")
    suspend fun getUserOverallRank(
        @Header("Authorization") bearerToken: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("tic_area") ticArea: String?
    ): BaseResponseList<UserOverallRank>


    @PUT("ops_ticket/request-permit/{id}")
    suspend fun requestPermit(
        @Path("id") id: String?,
        @Header("Authorization") bearerToken: String?
    ): BaseResponse<Any?>

    @PUT("ops_ticket/request-permit/approve/{id}")
    suspend fun approvePermit(
        @Path("id") id: String?,
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Any?>

    @PUT("ops_ticket/check-in/{id}")
    suspend fun checkIn(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: String?,
        @Query("in_radius") inRadius: Boolean?
    ): BaseResponse<Any?>

    @Multipart
    @PUT("ops_ticket/check-in/{id}")
    suspend fun checkInEviden(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: String?,
        @Query("in_radius") inRadius: Boolean?,
        @Part param: MutableList<MultipartBody.Part?>
    ): BaseResponse<Any?>

    @PUT("ops_ticket/check-out/{id}")
    suspend fun checkOut(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: String?
    ): BaseResponse<Any?>

    @PUT("ops_ticket/check-in/approve/{id}")
    suspend fun approveCheckin(
        @Path("id") id: Int?,
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Any?>

    @PUT("ops_ticket/submit-ticket/{id}")
    suspend fun submitTicket(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: String?
    ): BaseResponse<Any?>

    @PUT("ops_ticket/submit-ticket/approve/{id}")
    suspend fun approveSubmitTicket(
        @Path("id") id: Int?,
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Any?>

    @PUT("ops_ticket/take-ticket-engineer/{id}")
    suspend fun takeTicket(
        @Path("id") id: String?,
        @Header("Authorization") bearerToken: String?
    ): BaseResponse<Any?>

    @PUT("ops_ticket/get-estimated-time-engineer/{id}")
    suspend fun getEstimateTimeEngineer(
        @Path("id") id: String?,
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Any?>

    @GET("master_rca/owner")
    suspend fun getMasterOwner(
        @Header("Authorization") bearerToken: String?
    ): BaseRca

    @GET("master_rca/category")
    suspend fun getMasterCategory(
        @Header("Authorization") bearerToken: String?,
        @Query("owner") owner: String?
    ): BaseRca

    @GET("master_rca/rc1")
    suspend fun getMasterRc1(
        @Header("Authorization") bearerToken: String?,
        @Query("owner") owner: String?,
        @Query("category") category: String?
    ): BaseRca

    @GET("master_rca/rc2")
    suspend fun getMasterRc2(
        @Header("Authorization") bearerToken: String?,
        @Query("owner") owner: String?,
        @Query("category") category: String?,
        @Query("rc1") rc1: String?
    ): BaseRca

    @GET("master_rca/resolution-action")
    suspend fun getMasterResolutionAction(
        @QueryMap param: MutableMap<String, Any?>,
        @Header("Authorization") bearerToken: String?
    ): BaseResponse<Any?>

    @PUT("ops_ticket/submit-rca/{id}")
    suspend fun submitRca(
        @Path("id") id: String?,
        @Header("Authorization") bearerToken: String?,
        @Body param: MutableMap<String, Any?>
    ): BaseResponse<Any?>

    @GET("ops_ticket/get-rca/{id}")
    suspend fun getRca(
        @Path("id") id: String?,
        @Header("Authorization") bearerToken: String?
    ): BaseResponse<RcaResponse>

    @Multipart
    @PUT("ops_ticket/submit-rca/photo/{id}")
    suspend fun submitRcaPhoto(
        @Header("Authorization") bearerToken: String?,
        @Path("id") id: String?,
        @Part param: MutableList<MultipartBody.Part?>
    ): BaseResponse<Any?>

}