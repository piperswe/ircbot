package me.zebmccorkle.ircbot;

import me.zebmccorkle.kindajs.XMLHttpRequest;

import java.io.IOException;
import java.util.Map;

public class HTTP {
    public static Response get(String url, Map<String, String> headers) throws IOException, InterruptedException {
        XMLHttpRequest xhr = new XMLHttpRequest();
        xhr.open("GET", url, false, null, null);
        for (Map.Entry<String, String> header : headers.entrySet())
            xhr.setRequestHeader(header.getKey(), header.getValue());
        xhr.send();
        if (xhr.readyState != XMLHttpRequest.ReadyState.DONE)
            throw new IOException();
        return new Response(xhr.responseText, xhr.status, xhr.getAllResponseHeaders());
    }

    public static Response post(String url, Map<String, String> headers, byte[] data) throws IOException, InterruptedException {
        XMLHttpRequest xhr = new XMLHttpRequest();
        xhr.open("POST", url, false, null, null);
        for (Map.Entry<String, String> header : headers.entrySet())
            xhr.setRequestHeader(header.getKey(), header.getValue());
        xhr.send(data);
        if (xhr.readyState != XMLHttpRequest.ReadyState.DONE)
            throw new IOException();
        return new Response(xhr.responseText, xhr.status, xhr.getAllResponseHeaders());
    }

    public static Response put(String url, Map<String, String> headers, byte[] data) throws IOException, InterruptedException {
        XMLHttpRequest xhr = new XMLHttpRequest();
        xhr.open("PUT", url, false, null, null);
        for (Map.Entry<String, String> header : headers.entrySet())
            xhr.setRequestHeader(header.getKey(), header.getValue());
        xhr.send(data);
        if (xhr.readyState != XMLHttpRequest.ReadyState.DONE)
            throw new IOException();
        return new Response(xhr.responseText, xhr.status, xhr.getAllResponseHeaders());
    }

    public static Response delete(String url, Map<String, String> headers, byte[] data) throws IOException, InterruptedException {
        XMLHttpRequest xhr = new XMLHttpRequest();
        xhr.open("DELETE", url, false, null, null);
        for (Map.Entry<String, String> header : headers.entrySet())
            xhr.setRequestHeader(header.getKey(), header.getValue());
        xhr.send();
        if (xhr.readyState != XMLHttpRequest.ReadyState.DONE)
            throw new IOException();
        return new Response(xhr.responseText, xhr.status, xhr.getAllResponseHeaders());
    }

    public static class Response {
        public String response;
        public short responseCode;
        public String responseHeaders;

        public Response(String response, short responseCode, String responseHeaders) {
            this.response = response;
            this.responseCode = responseCode;
            this.responseHeaders = responseHeaders;
        }
    }
}
