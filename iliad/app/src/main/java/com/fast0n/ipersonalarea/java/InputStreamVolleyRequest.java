package com.fast0n.ipersonalarea.java;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Map;

public class InputStreamVolleyRequest extends Request<byte[]> {
    private final Response.Listener<byte[]> listener;
    private Map<String, String> params;

    public InputStreamVolleyRequest(int method, String url, Response.Listener<byte[]> listener, Response.ErrorListener errorListener, Map<String, String> params) {
        super(method, url, errorListener);
        setShouldCache(false);
        this.listener = listener;
        this.params = params;
    }

    @Override
    protected Map<String, String> getParams() {
        params.put("Content-Type", "audio/wav");
        return params;
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(byte[] response) {
        listener.onResponse(response);
    }
}