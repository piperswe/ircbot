package me.zebmccorkle.kindajs;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright (c) 2016 Telcial
 * 
 * All Rights Reserved
 */
public class XMLHttpRequest {
    /**
     * Returns a {@link ReadyState}, the state of the request
     */
    public ReadyState readyState = ReadyState.UNSENT;
    /**
     * Returns a {@link Iterable<Byte> Iterable<Byte>}, {@link String}, {@link Document}, or {@link org.json.JSONObject JSONObject}, depending of the value of {@link #responseType} that contains the response entity body. This is <code>null</code> if the request is not complete or was not successful.
     */
    public Object response;
    /**
     * Returns a {@link String} that contains the response to the request as text, or <code>null</code> if the request was unsuccessful or has not yet been sent.
     */
    public String responseText;
    /**
     * Is an enumerated value that defines the response type. It can have the following values:
     * <table>
     * <tr>
     * <th>Value</th>
     * <th>Data type of {@link #response} property</th>
     * </tr>
     * <tr>
     * <td><code>""</code></td>
     * <td>{@link String} (this is the default value)</td>
     * </tr>
     * <tr>
     * <td><code>"arraybuffer"</code></td>
     * <td>{@link Iterable<Byte>}</td>
     * </tr>
     * <tr>
     * <td><code>"document"</code></td>
     * <td>{@link Document}</td>
     * </tr>
     * <tr>
     * <td><code>"json"</code></td>
     * <td>{@link org.json.JSONObject}, parsed from a JSON string returned by the server</td>
     * </tr>
     * <tr>
     * <td><code>"text"</code></td>
     * <td>{@link String}</td>
     * </tr>
     * </table>
     */
    public String responseType;
    /**
     * Returns a {@link Document} containing the response to the request, or <code>null</code> if the request was unsuccessful, has not yet been sent, or cannot be parsed as XML or HTML. The response is parsed as if it were a <code>text/xml</code> stream. When the {@link #responseType} is set to <code>"document"</code> and the request has been made asynchronously, the response is parsed as a <code>text/html</code> stream.
     */
    public Document responseXML;
    /**
     * Returns a {@link Short} with the status of the response of the request. This is the HTTP result code (for example, {@link #status} is 200 for a successful request).
     */
    public short status;
    /**
     * Returns a {@link String} containing the response string returned by the HTTP server. Unlike {@link #status}, this includes the entire text of the response message ("<code>200 OK</code>", for example).
     */
    public String statusText;
    /**
     * Is a {@link Long} representing the number of milliseconds a request can take before automatically being terminated. A value of 0 (which is the default) means there is no timeout.
     *
     * @implNote Not yet implemented
     */
    public long timeout = 0;
    /**
     * Is a {@link Boolean} that indicates whether or not cross-site <code>Access-Control</code> requests should be made using credentials such as cookies or authorization headers.
     * <p>
     * In addition, this flag is also used to indicate when cookies are to be ignored in the response.
     * <p>
     * The default is <code>false</code>.
     *
     * @implNote Not completely sure what this is supposed to do.
     */
    public boolean withCredentials = false;
    /**
     * A {@link Runnable} that is called whenever the {@link #readyState} attribute changes. The callback is called from the user interface thread.
     * <strong>Warning:</strong> This should not be used with synchronous requests and must not be used from native code.
     */
    public Runnable onreadystatechange = new Runnable() {
        @Override
        public void run() {
            // noop
        }
    };
    /**
     * Is a {@link Runnable} that is called whenever the request times out.
     */
    public Runnable ontimeout = new Runnable() {
        @Override
        public void run() {
            // noop
        }
    };
    // TODO: add #upload
    private Map<String, String> responseHeaders = new HashMap<>();
    private Map<String, String> requestHeaders = new HashMap<>();
    private String mime;
    private Thread thread;
    private URL url;
    private boolean async;
    private String user;
    private String password;
    private String method;

    /**
     * Aborts the request if it has already been sent.
     */
    public void abort() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    /**
     * @return All the response headers, separated by <a href="https://developer.mozilla.org/en-US/docs/Glossary/CRLF">CRLF</a>, as a string, or <code>null</code> if no response has been received.
     * <p>
     * <strong>Note:</strong> For multipart requests, this returns the headers from the <em>current</em> part of the request, not from the original channel.
     */
    public String getAllResponseHeaders() {
        // TODO: use java 8
        String result = "";
        for (Map.Entry<String, String> header : responseHeaders.entrySet())
            result += String.format("\r\n%s: %s", header.getKey(), header.getValue());
        return result.substring(2);
    }

    /**
     * @param header Header to get
     * @return The string containing the text of the specified header, or <code>null</code> if either the response has not yet been received or the header doesn't exist in the response. If there are multiple response headers with the same name, then their values are returned as a single concatenated string, where each value is separated from the previous one by a pair of comma and space. The {@link #getResponseHeader(String) getResponseHeader()} method returns the value as a UTF byte sequence.
     */
    public String getResponseHeader(String header) {
        return responseHeaders.get(header);
    }

    /**
     * Initializes a request.
     *
     * @param method   The HTTP method to use, such as "GET", "POST", "PUT", "DELETE", etc. Ignored for non-HTTP(S) URLs.
     * @param url      The URL to send the request to.
     * @param async    An optional boolean parameter, defaulting to true, indicating whether or not to perform the operation asynchronously. If this value is <code>false</code>, the {@link #send() send()} method does not return until the response is received. If <code>true</code>, notification of a completed transaction is provided using event listeners. This <em>must</em> be true if the <code>multipart</code> attribute is <code>true</code>, or an exception will be thrown.
     * @param user     The optional user name to use for authentication purposes; by default, this is an empty string.
     * @param password The optional password to use for authentication purposes; by default, this is an empty string.
     */
    public void open(String method, String url, Boolean async, String user, String password) throws InterruptedException, IOException {
        if (async == null)
            async = true;
        if (user == null)
            user = "";
        if (password == null)
            password = "";

        mime = null;
        responseHeaders.clear();
        requestHeaders.clear();
        abort();

        this.method = method;
        this.url = new URL(url);
        this.async = async;
        this.user = user;
        this.password = password;

        this.readyState = ReadyState.OPENED;
        onreadystatechange.run();

        // TODO: Implement
    }

    /**
     * Overrides the MIME type returned by the server. This may be used, for example, to force a stream to be treated and parsed as text/xml, even if the server does not report it as such. This method must be called before {@link #send() send()}.
     */
    public void overrideMimeType(String mimetype) {
        mime = mimetype;
    }

    /**
     * Sends the request. If the request is asynchronous (which is the default), this method returns as soon as the request is sent. If the request is synchronous, this method doesn't return until the response has arrived.
     */
    public void send() throws IOException, InterruptedException {
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod(method);
                    for (Map.Entry<String, String> header : requestHeaders.entrySet())
                        connection.addRequestProperty(header.getKey(), header.getValue());
                    status = (short) connection.getResponseCode();
                    statusText = connection.getResponseMessage();
                    for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet())
                        responseHeaders.put(header.getKey(), header.getValue().get(0));
                    readyState = ReadyState.HEADERS_RECEIVED;
                    onreadystatechange.run();
                    InputStream inputStream = connection.getInputStream();
                    mime = mime == null ? responseHeaders.get("Content-Type") : mime;
                    if (responseHeaders.get("Content-Length") != null) {
                        byte[] bytes = new byte[Integer.parseInt(responseHeaders.get("Content-Length"))];
                        DataInputStream dataInputStream = new DataInputStream(inputStream);
                        dataInputStream.readFully(bytes);
                        process(bytes);
                        readyState = ReadyState.DONE;
                        onreadystatechange.run();
                    } else {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        byte[] bytes = new byte[16384];
                        int nRead;
                        readyState = ReadyState.LOADING;
                        onreadystatechange.run();
                        while ((nRead = inputStream.read(bytes, 0, bytes.length)) != -1) {
                            outputStream.write(bytes, 0, nRead);
                            process(outputStream.toByteArray());
                        }
                        readyState = ReadyState.DONE;
                        onreadystatechange.run();
                    }
                } catch (IOException e) {
                    try {
                        status = (short) connection.getResponseCode();
                        statusText = connection.getResponseMessage();
                        readyState = ReadyState.DONE;
                        onreadystatechange.run();
                        return;
                    } catch (Exception ex) {
                        e.printStackTrace();
                    }
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            }

            private void process(byte[] bytes) throws ParserConfigurationException, IOException, SAXException {
                switch (mime) {
                    case "text/xml":
                    case "application/xml":
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        responseXML = builder.parse(new String(bytes));
                        response = responseXML;
                        responseType = "document";
                        responseText = new String(bytes);
                        break;
                    case "text/json":
                    case "application/json":
                        JSONObject object = new JSONObject(new String(bytes));
                        response = object;
                        responseType = "json";
                        responseText = new String(bytes);
                        break;
                    default:
                        responseType = "arraybuffer";
                        response = bytes;
                        responseText = new String(bytes);
                        break;
                }
            }
        });
        thread.run();
        if (!async)
            thread.wait();
    }

    /**
     * Sends the request. If the request is asynchronous (which is the default), this method returns as soon as the request is sent. If the request is synchronous, this method doesn't return until the response has arrived.
     *
     * @param data Data to send
     */
    public void send(byte[] data) {
        // TODO
    }

    /**
     * Sends the request. If the request is asynchronous (which is the default), this method returns as soon as the request is sent. If the request is synchronous, this method doesn't return until the response has arrived.
     *
     * @param data Data to send
     */
    public void send(String data) {
        send(data.getBytes());
    }

    /**
     * Sets the value of an HTTP request header. You must call {@link #setRequestHeader(String, String) setRequestHeader()} after {@link #open(String, String, Boolean, String, String) open()}, but before {@link #send() send()}. If this method is called several times with the same header, the values are merged into one single request header.
     *
     * @param header Header name
     * @param value  Header value
     */
    public void setRequestHeader(String header, String value) {
        requestHeaders.put(header, value);
    }

    /**
     * Possible ready states
     */
    public enum ReadyState {
        /**
         * Client has been created. {@link #open(String, String, Boolean, String, String) open()} not called yet.
         */
        UNSENT,
        /**
         * {@link #open(String, String, Boolean, String, String) open()} has been called.
         */
        OPENED,
        /**
         * {@link #send()} has been called, and headers and status are available.
         */
        HEADERS_RECEIVED,
        /**
         * Downloading; {@link #responseText} holds partial data.
         */
        LOADING,
        /**
         * The operation is complete.
         */
        DONE
    }
}
