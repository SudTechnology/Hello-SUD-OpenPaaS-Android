package tech.sud.mgp.hello.runtime;

import android.app.Activity;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tech.sud.mgp.hello.QuickStartUtils;

public class QuickStartRuntimeGameViewModel extends BaseRuntimeGameViewModel {

    // TODO: Sud平台申请的appId
    // TODO: The appId obtained from Sud platform application.
    public static String SudGIP_APP_ID = "1461564080052506636";

    // TODO: Sud平台申请的appKey
    // TODO: The appKey obtained from Sud platform application.
    public static String SudGIP_APP_KEY = "03pNxK2lEXsKiiwrBQ9GbH541Fk2Sfnc";

    // TODO: 使用的UserId。这里随机生成作演示，开发者将其修改为业务使用的唯一userId
    // TODO: Used UserId. Here it is randomly generated for demonstration purposes. Developers should modify it to the unique userId used for the business.
    public static String userId = QuickStartUtils.genUserID();

    public final MutableLiveData<View> gameViewLiveData = new MutableLiveData<>();

    /**
     * 向接入方服务器获取code
     * Retrieve the code from the partner's server.
     */
    @Override
    protected void getCode(Activity activity, String userId, String appId, GameGetCodeListener listener) {
        // TODO: 2022/6/10 注意，这里是演示使用OkHttpClient请求hello-sud服务
        // TODO: 2022/6/10 开发者在与后端联调时需将其改成自己的网络请求方式向自己的服务器获取code
        // TODO: 2023/10/26 每次回调此方法都去自己的后端拿最新的code，不要缓存code

        // TODO: 2022/6/10 Note that this is a demonstration using OkHttpClient to request the hello-sud service.
        // TODO: 2022/6/10 Developers should modify this to their own network request method to retrieve the code from their own server during backend integration.
        // TODO: 2023/10/26 Retrieve the latest code from your own backend every time this method is called, and avoid caching the code.
        OkHttpClient client = new OkHttpClient();
        String req;
        try {
            JSONObject reqJsonObj = new JSONObject();
            reqJsonObj.put("user_id", userId);
            reqJsonObj.put("app_id", getAppId());
            req = reqJsonObj.toString();
        } catch (Exception e) {
            e.printStackTrace();
            req = "";
        }

        RequestBody body = RequestBody.create(req, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://prod-hellosud-base.s00.tech/login/v3")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailed(-1, "error:" + e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String dataJson = Objects.requireNonNull(response.body()).string();
                    JSONObject jsonObject = new JSONObject(dataJson);
                    int ret_code = jsonObject.getInt("ret_code");
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    String code = dataObject.getString("runtime_code");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (ret_code == 0) {
                                listener.onSuccess(code);
                            } else {
                                listener.onFailed(ret_code, "response fail");
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFailed(-1, "error:" + e);
                        }
                    });
                }
            }
        });
    }

    /**
     * 设置当前用户id(接入方定义)
     * Set the current user ID (defined by the partner).
     */
    @Override
    protected String getUserId() {
        return userId;
    }

    /**
     * 设置Sud平台申请的appId
     * Set the appId obtained from the Sud platform.
     */
    @Override
    protected String getAppId() {
        return SudGIP_APP_ID;
    }

    /**
     * 设置Sud平台申请的appKey
     * Set the appKey obtained from the Sud platform.
     */
    @Override
    protected String getAppKey() {
        return SudGIP_APP_KEY;
    }

    @Override
    protected void onAddGameView(View gameView) {
        gameViewLiveData.setValue(gameView);
    }

    @Override
    protected void onRemoveGameView() {
        gameViewLiveData.setValue(null);
    }

}
