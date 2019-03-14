package com.example.robin.papers.impl;

import com.example.robin.papers.model.AcademiesOrCollegesRes;
import com.example.robin.papers.model.CollectionListData;
import com.example.robin.papers.model.CommentListData;
import com.example.robin.papers.model.FileDetailRes;
import com.example.robin.papers.model.FileRes;
import com.example.robin.papers.model.FileUrlRes;
import com.example.robin.papers.model.MyCommentListData;
import com.example.robin.papers.model.PostInfo;
import com.example.robin.papers.model.ResponseInfo;
import com.example.robin.papers.model.UserInfoRes;
import com.example.robin.papers.requestModel.AuthPerInfoRequest;
import com.example.robin.papers.requestModel.CommentAddRequest;
import com.example.robin.papers.requestModel.ExitLoginRequest;
import com.example.robin.papers.requestModel.FileRequest;
import com.example.robin.papers.requestModel.GetCodeRequest;
import com.example.robin.papers.requestModel.GetCommentListRequest;
import com.example.robin.papers.requestModel.LikeDisLikeRequest;
import com.example.robin.papers.requestModel.LoginRequest;
import com.example.robin.papers.requestModel.PerfectInfoRequest;
import com.example.robin.papers.requestModel.PostAddRequest;
import com.example.robin.papers.requestModel.PostIsLikeRequest;
import com.example.robin.papers.requestModel.PostRequest;
import com.example.robin.papers.requestModel.QueryAcademiesRequest;
import com.example.robin.papers.requestModel.RegisterRequest;
import com.example.robin.papers.requestModel.SetNewPswRequest;
import com.example.robin.papers.requestModel.Token;
import com.example.robin.papers.requestModel.TokenRequest;
import com.example.robin.papers.requestModel.UMengLoginRequest;
import com.example.robin.papers.requestModel.UpdateUserRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 *
 */
public interface PostQmkl {

    /*

    （一）评论表

     */
    //查询用户的评论
    @POST("qmkl1.0.0/comment/user/list")
    Call<ResponseInfo<MyCommentListData[]>> getCommentUserList(@Body PostRequest postRequest);

    //添加评论
    @POST("qmkl1.0.0/comment/add")
    Call<ResponseInfo> getCommentAdd(@Body CommentAddRequest commentAddRequest);

    //返回对应帖子的评论
    @POST("qmkl1.0.0/comment/list")
    Call<ResponseInfo<CommentListData>> getCommentList(@Body GetCommentListRequest getCommentListRequest);


    /*

    （二）收藏表

     */

    //添加收藏
    @POST("qmkl1.0.0/collect/aoc")
    Call<ResponseInfo> getCollectAoc(@Body PostIsLikeRequest postIsLikeRequest);

    //列举用户的收藏
    @POST("qmkl1.0.0/collect/list")
    Call<ResponseInfo<CollectionListData[]>> getCollectList(@Body PostRequest postRequest);

    //用户帖子是否收藏
    @POST("qmkl1.0.0/collect/iscollect")
    Call<ResponseInfo<Boolean>> getCollectIsCollect(@Body PostIsLikeRequest postIsLikeRequest);



    /*

    （三）学校学院

     */

    //查询某个学校专业
    @POST("qmkl1.0.0/academy/list/college")
    Call<AcademiesOrCollegesRes> getAcademyListCollege(@Body QueryAcademiesRequest academiesRequest);

    //列举所有学校
    @POST("qmkl1.0.0/college/list")
    Call<AcademiesOrCollegesRes> getCollegeList();


    /*

    （四）点赞表

     */


    //判断用户是否点赞和踩
    @POST("qmkl1.0.0/dislike/is/dislike")
    Call<ResponseInfo> getDislikeIsDislike(@Body LikeDisLikeRequest likeDisLikeRequest);

    @POST("qmkl1.0.0/like/is/like")
    Call<ResponseInfo> getLikeIsLike(@Body LikeDisLikeRequest likeDisLikeRequest);

    //点赞、点踩接口
    @POST("qmkl1.0.0/like/addordesc")
    Call<ResponseInfo> getLikeAddOrDesc(@Body LikeDisLikeRequest likeDisLikeRequest);

    @POST("qmkl1.0.0/dislike/addordesc")
    Call<ResponseInfo> getDislikeAddOrDesc(@Body LikeDisLikeRequest likeDisLikeRequest);

    /*

    （五）文件表

     */

    //文件列表
    @POST("qmkl1.0.0/file/list")
    Call<FileRes> getFileList(@Body FileRequest fileRequest);

    //文件详细信息
    @POST("qmkl1.0.0/file/list/detail")
    Call<FileDetailRes> getFileListDetail(@Body FileRequest fileRequest);

    //返回下载链接
    @POST("qmkl1.0.0/file/download/url")
    Call<FileUrlRes> getFileDownloadUrl(@Body FileRequest fileDetailRequest);

    //上传文件
    @Multipart
    @POST("finalexam/app/upfile")
    Call<ResponseInfo<String>> finalexamAppUpfile(@Part("userId") RequestBody userId,
                                                  @Part MultipartBody.Part file,
                                                  @Part("spath") RequestBody spath,
                                                  @Part("note") RequestBody note,
                                                  @Part("anonymous") RequestBody anonymous);


    /*

    （六）用户表

     */
    //用户登录
    @POST("qmkl1.0.0/user/login")
    Call<ResponseInfo> getUserLogin(@Body LoginRequest request);

    @POST("qmkl1.0.0/user/login")
    Call<ResponseInfo> getUserLogin2(@Body TokenRequest token);

    @POST("qmkl1.0.0/user/info")
    Call<UserInfoRes> getUserInfo(@Body TokenRequest token);

    //短信注册、短信修改密码
    @POST("qmkl1.0.0/sms/send")
    Call<ResponseInfo<String>> getSmsSend(@Body GetCodeRequest request);

    //短信注册二
    @POST("qmkl1.0.0/user/vercode")
    Call<ResponseInfo> getUserVerCode(@Body RegisterRequest registerRequest);

    //修改密码二
    @POST("qmkl1.0.0/user/update/password")
    Call<ResponseInfo> getUserUpdatePassword(@Body SetNewPswRequest newPswRequest);

    //更新用户信息
    @POST("qmkl1.0.0/user/update/info")
    Call<ResponseInfo> getUserUpdateInfo(@Body UpdateUserRequest userRequest);

    //更新头像
    @Multipart
    @POST("qmkl1.0.0/user/update/avatar")
    Call<ResponseInfo<String>> getUserUpdateAvatar(@Part MultipartBody.Part avatar, @Part("token") RequestBody token);

    //退出登录
    @POST("qmkl1.0.0/user/out")
    Call<String> getUserOut(@Body ExitLoginRequest exitLoginRequest);

    //完善用户信息
    @POST("qmkl1.0.0/user/all/info")
    Call<ResponseInfo<String>> getUserAllInfo(@Body PerfectInfoRequest perfectInfoRequest);


    /*

    （七）帖子表

     */
    //查询所有帖子
    @POST("qmkl1.0.0/post/list")
    Call<ResponseInfo<PostInfo[]>> getPostList(@Body PostRequest request);

    //查询当前用户的帖子
    @POST("qmkl1.0.0/post/user/list")
    Call<ResponseInfo<PostInfo[]>> getPostUserList(@Body PostRequest postRequest);

    //新增帖子
    @POST("qmkl1.0.0/post/add")
    Call<ResponseInfo> getPostAdd(@Body PostAddRequest postAddRequest);

    //删除帖子
    @POST("qmkl1.0.0/post/del")
    Call<ResponseInfo> getPostDel(@Body PostIsLikeRequest postIsLikeRequest);

    //查询某个帖子
    @POST("qmkl1.0.0/post/get/{postId}")
    Call<ResponseInfo<PostInfo>> getPostGetPost(@Path("postId") String postId, @Body Token token);

    /*

    （八）帖子点赞表

     */

    //用户帖子是否点赞
    @POST("qmkl1.0.0/post/like/islike")
    Call<ResponseInfo<Boolean>> getPostLikeIsLike(@Body PostIsLikeRequest postIsLikeRequest);

    @POST("qmkl1.0.0/post/like/add")
    Call<ResponseInfo> getPostLikeAdd(@Body PostIsLikeRequest postIsLikeRequest);


    /*

    （九）第三方登录

     */
    //第三方登录接口
    @POST("qmkl1.0.0/userauth/login")
    Call<ResponseInfo<String>> getUserAuthLogin(@Body UMengLoginRequest uMengLoginRequest);

    //完善信息接口
    @POST("qmkl1.0.0/userauth/update/info")
    Call<ResponseInfo<String>> getUserAuthUpdateInfo(@Body AuthPerInfoRequest authPerInfoRequest);

    /*

    （十）举报帖子

     */



















}
