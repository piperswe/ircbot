package me.zebmccorkle.ircbot.irc;

import org.jetbrains.annotations.NotNull;
import sun.security.util.HostnameChecker;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class Connection {
    private Socket socket;
    private DataOutputStream outputStream;
    private BufferedReader reader;

    public Connection(@NotNull String host, int port, boolean ssl) throws IOException, KeyManagementException, CertificateException {
        if (ssl) {
            // Create the context.  Specify the SunJSSE provider to avoid
// picking up third-party providers.  Try the TLS 1.2 provider
// first, then fall back to TLS 1.0.
            SSLContext ctx;
            try {
                ctx = SSLContext.getInstance("TLSv1.2", "SunJSSE");
            } catch (NoSuchAlgorithmException e) {
                try {
                    ctx = SSLContext.getInstance("TLSv1", "SunJSSE");
                } catch (NoSuchAlgorithmException e1) {
                    // The TLS 1.0 provider should always be available.
                    throw new AssertionError(e1);
                } catch (NoSuchProviderException e1) {
                    throw new AssertionError(e1);
                }
            } catch (NoSuchProviderException e) {
                // The SunJSSE provider should always be available.
                throw new AssertionError(e);
            }
            ctx.init(null, null, null);
            // Prepare TLS parameters.  These have to applied to every TLS
// socket before the handshake is triggered.
            SSLParameters params = ctx.getDefaultSSLParameters();
// Do not send an SSL-2.0-compatible Client Hello.
            ArrayList<String> protocols = new ArrayList<String>(
                    Arrays.asList(params.getProtocols()));
            protocols.remove("SSLv2Hello");
            params.setProtocols(protocols.toArray(new String[protocols.size()]));
// Adjust the supported ciphers.
            ArrayList<String> ciphers = new ArrayList<String>(
                    Arrays.asList(params.getCipherSuites()));
            ciphers.retainAll(Arrays.asList(
                    "TLS_RSA_WITH_AES_128_CBC_SHA256",
                    "TLS_RSA_WITH_AES_256_CBC_SHA256",
                    "TLS_RSA_WITH_AES_256_CBC_SHA",
                    "TLS_RSA_WITH_AES_128_CBC_SHA",
                    "SSL_RSA_WITH_3DES_EDE_CBC_SHA",
                    "SSL_RSA_WITH_RC4_128_SHA1",
                    "SSL_RSA_WITH_RC4_128_MD5",
                    "TLS_EMPTY_RENEGOTIATION_INFO_SCSV"));
            params.setCipherSuites(ciphers.toArray(new String[ciphers.size()]));
            // Create the socket and connect it at the TCP layer.
            socket = ctx.getSocketFactory()
                    .createSocket(host, port);

// Disable the Nagle algorithm.
            socket.setTcpNoDelay(true);

// Adjust ciphers and protocols.
            ((SSLSocket)socket).setSSLParameters(params);

// Perform the handshake.
            ((SSLSocket)socket).startHandshake();

// Validate the host name.  The match() method throws
// CertificateException on failure.
            X509Certificate peer = (X509Certificate)
                    ((SSLSocket)socket).getSession().getPeerCertificates()[0];
// This is the only way to perform host name checking on OpenJDK 6.
            HostnameChecker.getInstance(HostnameChecker.TYPE_TLS).match(
                    host, peer);
        } else {
            socket = new Socket(host, port);
        }
        outputStream = new DataOutputStream(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private class DeferredSend implements Runnable {
        int sleep;
        ProtocolUtils.Message msg;
        public DeferredSend(int sleep, ProtocolUtils.Message msg) {
            this.sleep = sleep;
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                Thread.sleep((long) Math.pow(2, sleep));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                synchronized (outputStream) {
                    outputStream.writeBytes(msg.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private long lastMessage = 0;
    private int fastMessages = 0;
    public void send(ProtocolUtils.Message msg) throws IOException, InterruptedException {
        long sinceLastMessage = System.nanoTime() - lastMessage;
        if (sinceLastMessage < 4000000) {
            new Thread(new DeferredSend(fastMessages++, msg)).start();
        } else {
            fastMessages = 0;
            outputStream.writeBytes(msg.toString());
        }
        lastMessage = System.nanoTime();
    }
    public ProtocolUtils.Message recv() throws IOException {
        String line = reader.readLine();
        return line == null ? null : new ProtocolUtils.Message(line);
    }
}
