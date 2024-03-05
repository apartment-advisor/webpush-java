package nl.martijndwars.webpush;

import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jose4j.lang.JoseException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;

public class PushAsyncService extends AbstractPushService<PushAsyncService> {

    private final OkHttpClient httpClient = new OkHttpClient();

    public PushAsyncService() {
    }

    public PushAsyncService(String gcmApiKey) {
        super(gcmApiKey);
    }

    public PushAsyncService(KeyPair keyPair) {
        super(keyPair);
    }

    public PushAsyncService(KeyPair keyPair, String subject) {
        super(keyPair, subject);
    }

    public PushAsyncService(String publicKey, String privateKey) throws GeneralSecurityException {
        super(publicKey, privateKey);
    }

    public PushAsyncService(String publicKey, String privateKey, String subject) throws GeneralSecurityException {
        super(publicKey, privateKey, subject);
    }

    /**
     * Send a notification asynchronously.
     *
     * @param notification
     * @param encoding
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws JoseException
     */
    public PushCallback send(Notification notification, Encoding encoding) throws GeneralSecurityException, IOException, JoseException {
        var postRequest = preparePost(notification, encoding);

        var callBack = new PushCallback();
        httpClient.newCall(postRequest).enqueue(callBack);
        return callBack;
    }

    public PushCallback send(Notification notification) throws GeneralSecurityException, IOException, JoseException {
        return send(notification, Encoding.AES128GCM);
    }

    /**
     * Prepare a POST request for AHC.
     *
     * @param notification
     * @param encoding
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws JoseException
     */
    public Request preparePost(Notification notification, Encoding encoding) throws GeneralSecurityException, IOException, JoseException {
        HttpRequest request = prepareRequest(notification, encoding);

        var builder = new Request.Builder().url(request.getUrl());
        request.getHeaders().forEach(builder::addHeader);
        if (request.getBody() != null) {
            builder.post(RequestBody.create(request.getBody()));
        }

        return builder.build();
    }

    public static final class PushCallback implements Callback {

        private static final Logger log = Logger.getLogger(PushCallback.class.getName());

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            log.log(Level.SEVERE, "Push notification failed", e);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            log.info(
                String.format("Push notification success, responseCode=%d, response=%s",
                response.code(),
                response.body() == null ? null : new String(response.body().bytes()))
            );
        }
    }
}
