package com.example.sentivibe.network;

import okhttp3.*;
import okio.ByteString;
import android.util.Log;

public class WebSocketClient {
    private WebSocket ws;
    private OkHttpClient client;

    public WebSocketClient(String url) {
        client = new OkHttpClient();
        Request req = new Request.Builder().url(url).build();
        ws = client.newWebSocket(req, new WebSocketListener() {
            @Override public void onOpen(WebSocket webSocket, Response response) { Log.d("WS","open"); }
            @Override public void onMessage(WebSocket webSocket, String text) { Log.d("WS","msg:"+text); }
            @Override public void onMessage(WebSocket webSocket, ByteString bytes) { }
            @Override public void onFailure(WebSocket webSocket, Throwable t, Response response) { Log.d("WS","fail:"+t.getMessage()); }
        });
    }
    public void close() { if (ws != null) ws.close(1000, null); }
}
