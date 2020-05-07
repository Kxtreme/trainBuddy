package me.kxtre.trainbuddy.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Pair;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.android.volley.VolleyLog.TAG;

/**
 * Created by kxtreme on 08-11-2017.
 */

public class HttpUtils {
    public static Context context;

    public static void Get(final HttpCallBack callBack, final String url, Context context, boolean json, final List<Pair<String, String>> headers) {
        HttpUtils.context = context;
        Request getRequest;
        final RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        if (json) {
            getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                response.put("error", false);
                                    if (isNetworkAvailable(HttpUtils.context)) {
                                        callBack.onResult(response);
                                    }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("Response", response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                                try {
                                    JSONObject errorResponse = new JSONObject();
                                    errorResponse.put("error", true);
                                    errorResponse.put("error_message", "no Temporary Data Available");
                                    callBack.onFail(errorResponse.toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            Log.d("Error.Response", error.getMessage() != null ? error.getMessage() : "no message");
                        }
                    }
            );
        } else {
            getRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                                if (isNetworkAvailable(HttpUtils.context)) {
                                    callBack.onResult(response);
                                }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    if (headers == null) {
                        return super.getHeaders();
                    }

                    Map<String, String> params = new HashMap<>();
                    for (Pair<String, String> header : headers) {
                        params.put(header.first, header.second);
                    }

                    return params;
                }
            };

        }
        getRequest.setShouldCache(false);


// add it to the RequestQueue
        MyRequestQueue.add(getRequest);
    }

    public static void Get(HttpCallBack callback, String s, Context context) {
        HttpUtils.Get(callback, s, context, true, null);
    }

    public static void Get(HttpCallBack callback, String s, boolean json, Context context) {
        HttpUtils.Get(callback, s, context, json, null);
    }

    public static void Post(final HttpCallBack callBack, String url, final List<Pair<String, String>> params, Context context) {
        Post(callBack, url, params, context, null);
    }

    public static void Post(final HttpCallBack callBack, String url, final List<Pair<String, String>> params, Context context, final List<Pair<String, String>> headers) {

        final RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (callBack != null)
                        callBack.onResult(new JSONObject(response));
                } catch (JSONException e) {
                    callBack.onResult(response);
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFail(error.getMessage());
                final int status = error.networkResponse.statusCode;
                // Handle 30x
                if (HttpURLConnection.HTTP_MOVED_PERM == status || status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    final String location = error.networkResponse.headers.get("Location");
                    Log.d(TAG, "Location: " + location);
                }

                //This code is executed if there is an error.
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                for (Pair<String, String> param : params) {
                    MyData.put(param.first, param.second);
                }
                return MyData;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers == null) {
                    return super.getHeaders();
                }

                Map<String, String> MyData = new HashMap<>();
                for (Pair<String, String> header : headers) {
                    MyData.put(header.first, header.second);
                }

                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);

    }

    public static void Put(final HttpCallBack callBack, String url, final List<Pair<String, String>> params, Context context) {
        HttpUtils.Put(callBack, url, params, context, null);
    }

    public static void Put(final HttpCallBack callBack, String url, final List<Pair<String, String>> params, Context context, final List<Pair<String, String>> headers) {
        final RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        StringRequest MyStringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callBack.onResult(response);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {

                final int status = error.networkResponse.statusCode;
                // Handle 30x
                if (HttpURLConnection.HTTP_MOVED_PERM == status || status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    final String location = error.networkResponse.headers.get("Location");
                    Log.d(TAG, "Location: " + location);
                }

                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                for (Pair<String, String> param : params) {
                    MyData.put(param.first, param.second);
                }
                return MyData;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers == null) {
                    return super.getHeaders();
                }

                Map<String, String> MyData = new HashMap<>();
                for (Pair<String, String> header : headers) {
                    MyData.put(header.first, header.second);
                }

                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }

    public static void Delete(final HttpCallBack callBack, final String url, Context context, final List<Pair<String, String>> headers) {
        HttpUtils.context = context;
        Request getRequest;
        final RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        getRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callBack.onResult(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFail(error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers == null) {
                    return super.getHeaders();
                }

                Map<String, String> params = new HashMap<>();
                for (Pair<String, String> header : headers) {
                    params.put(header.first, header.second);
                }
                return params;
            }
        };
        getRequest.setShouldCache(false);


// add it to the RequestQueue
        MyRequestQueue.add(getRequest);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
