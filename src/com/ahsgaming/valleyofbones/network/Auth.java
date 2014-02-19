package com.ahsgaming.valleyofbones.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 2/9/14
 * Time: 9:56 AM
 */
public class Auth {
    public static String LOG = "Auth";

    public static class AuthPlayer {
        public String username;
        public String key;
    }

    public static class AuthError {
        public String message;
    }

    public static void authenticate(final String username, String token, final Callback callback) {
        Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.GET);
        httpGet.setUrl(String.format("%s/users/auth/%s/%s", GameServer.globalServerUrl, username, token));

        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString();
                switch(httpResponse.getStatus().getStatusCode()) {
                    case 401:
                    case 404:
                    default:
                        Gdx.app.log(LOG, "Response code " + httpResponse.getStatus().getStatusCode());
                        Gdx.app.log(LOG, response);
                        AuthError e = new AuthError();
                        e.message = response;
                        callback.error(e);
                        break;
                    case 200:
                        AuthPlayer p = new AuthPlayer();
                        p.username = username;
                        JsonReader jsonReader = new JsonReader();
                        JsonValue result = jsonReader.parse(response);
                        p.key = result.getString("user_key");
                        callback.result(p);
                        break;
                }
            }

            @Override
            public void failed(Throwable t) {
                AuthError e = new AuthError();
                e.message = "Http Error";
                callback.error(e);
                t.printStackTrace();
            }
        });
    }

    public interface Callback {
        public void result(Object result);
        public void error(Object error);
    }
}
