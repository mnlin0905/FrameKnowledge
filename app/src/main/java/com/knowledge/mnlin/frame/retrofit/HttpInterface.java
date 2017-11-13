package com.knowledge.mnlin.frame.retrofit;

import com.knowledge.mnlin.frame.base.BaseHttpBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 功能----使用retrofit框架与http交换数据
 * <p>
 * Created by MNLIN on 2017/9/25.
 */

public interface HttpInterface {
    @GET("getJson/getData")
    Observable<BaseHttpBean> getJson(@Query("username") String username,
                                     @Query("password") String password);

}
