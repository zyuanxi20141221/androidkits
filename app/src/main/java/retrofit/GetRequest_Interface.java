package retrofit;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Create by zheng on 2019/1/9
 */

public interface GetRequest_Interface {

    @GET("ajax.php?a=fy&f=auto&t=auto&w=你好")
    Call<Translation> getCall();
}
