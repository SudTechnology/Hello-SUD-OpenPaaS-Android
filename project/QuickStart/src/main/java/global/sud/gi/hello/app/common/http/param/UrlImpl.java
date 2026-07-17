package global.sud.gi.hello.app.common.http.param;

public class UrlImpl implements IBaseUrl {

    private String[] urlArr = {
            "https://hello-op.sud.ltd/"
    };

    @Override
    public String getBaseUrl() {
        return urlArr[0];
    }

}
