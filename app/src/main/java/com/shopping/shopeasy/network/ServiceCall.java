package com.shopping.shopeasy.network;

import android.net.http.HttpResponseCache;
import android.util.Log;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.shopping.shopeasy.util.Utils;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.message.BasicHeader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ServiceCall {

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";

    @SuppressWarnings("unused")
    public enum EMethodType {
        POST("POST"),
        GET("GET"),
        PUT("PUT"),
        DELETE("DELETE");

        private String methodName;
        EMethodType(final String methodName) {
            this.methodName = methodName;
        }

        public String getMethodName() {
            return methodName;
        }

        @Override
        public String toString() {
            return this.methodName;
        }
    }

    private EMethodType method;
    private List<HttpParam> params;
    private List<BasicHeader> headers;
    private ServiceCallback serviceCallback;
    private static final String TAG = ServiceCall.class.getSimpleName();
    private URL mUrl;
    private boolean shouldLog = true;
    //Below variable is responsible for hitting the api result from cache or force from network
    private boolean isOverrideCache;
    private Long connectionTimeout = 60000L;
    private Long socketTimeout = 60000L;
    private final HttpResponseCache cache = HttpResponseCache.getInstalled();
    private boolean isMultipartPost = false;
    private static final int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale

    private static final String MULTI_PART_BOUNDARY = "-----------------------";
    private static final String LINE_FEED = "\r\n";
    private static final String TWO_HYPHEN = "--";
    private static final int BUFFER_LENGTH = 8192;

    public ServiceCall(String url,
                       EMethodType method,
                       final List<HttpParam> params,
                       List<BasicHeader> headers,
                       final ServiceCallback serviceCallback,
                       final Long connectionTimeout,
                       final Long socketTimeout,
                       boolean shouldLog,
                       boolean isOverrideCache) {

        this.method = method;
        if(params!=null) {
            this.params = params;
        }
        if(headers!=null) {
            this.headers = headers;
        }
        this.serviceCallback = serviceCallback;
        this.connectionTimeout = connectionTimeout;
        this.socketTimeout = socketTimeout;
        this.shouldLog = shouldLog;
        this.isOverrideCache = isOverrideCache;
        if (method == EMethodType.POST && params != null ) {
            for (HttpParam httpParam : params) {
                if (httpParam.isBinary()) {
                    this.isMultipartPost = true;
                    break;
                }
            }
        }
        try {
            mUrl = new URL(URI.create(url).toString());
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    /**
     * A builder class which allows easy initialization of
     * parameters required for a service call.
     */
    public class ServiceCallBuilder {

        private String mUrl;
        private EMethodType method;
        private List<HttpParam> params;
        private List<BasicHeader> headers;
        private ServiceCallback serviceCallback;
        private Long connectionTimeout;
        private Long socketTimeout;
        private boolean shouldLog;
        private boolean isOverrideCache;

        public ServiceCallBuilder() {}

        public ServiceCallBuilder setUrl(final String mUrl) {
            this.mUrl = mUrl;
            return this;
        }

        public ServiceCallBuilder setMethod(final EMethodType methodType) {
            this.method = methodType;
            return this;
        }

        public ServiceCallBuilder setParams(final List<HttpParam> params) {
            this.params = params;
            return this;
        }

        public ServiceCallBuilder setHeaderElements(final List<BasicHeader> headers) {
            this.headers = headers;
            return this;
        }

        public ServiceCallBuilder shouldLog(final boolean shouldLog) {
            this.shouldLog = shouldLog;
            return this;
        }

        public ServiceCallBuilder overrideCache(final boolean isOverrideCache) {
            this.isOverrideCache = isOverrideCache;
            return this;
        }

        public ServiceCallBuilder setServiceCallBack(final ServiceCallback serviceCallBack) {
            this.serviceCallback = serviceCallBack;
            return this;
        }

        public ServiceCallBuilder setConnectionTimeOut(final Long connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public ServiceCallBuilder setSocketTimeOut(final Long socketTimeout) {
            this.socketTimeout = socketTimeout;
            return this;
        }

        public ServiceCall build() {
            return new ServiceCall(this.mUrl,
                    this.method,
                    this.params,headers,
                    serviceCallback,
                    this.connectionTimeout,
                    this.socketTimeout,
                    shouldLog,
                    isOverrideCache);
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,31).append(mUrl)
                .append(params)
                .append(method)
                .build();
    }

    /**
     * Response from the service call.
     * @return {@link Response}
     */
    public Response executeRequest() throws Exception {
        HttpURLConnection mConn;
        mConn = (HttpURLConnection)mUrl.openConnection();

        mConn.addRequestProperty("Connection", "close");
        if(isOverrideCache) {
            mConn.addRequestProperty("Cache-Control", "no-cache");
        } else {
            mConn.setDefaultUseCaches(true);
            mConn.setUseCaches(true);
        }

        mConn.setConnectTimeout(Integer.parseInt(connectionTimeout.toString()));
        mConn.setReadTimeout(Integer.parseInt(socketTimeout.toString()));


        if(headers != null) {
            setHeaders(mConn);
        }

        final String methodName = method.toString();
        switch ( methodName ) {

            case POST:
                doPost(mConn);
                break;

            case GET:
                mConn.setRequestMethod(methodName);
                mConn.setDoInput(true);
                break;

            case PUT:
                //doPost();
                //NO-OP
                break;

            case DELETE:
                mConn.setRequestMethod(methodName);
                break;

            default:
                throw new UnsupportedOperationException("Method '" + methodName + "' isn't allowed");
        }

        int responseCode = mConn.getResponseCode();
        final String out = getResponseAsString(mConn);
        protocolLog(mConn, responseCode, out);
        return new Response(out);
    }

    /**
     * An Async network call which wraps the resulting response
     * into the {@link Response} class. If the {@link ServiceCallback}
     * is not initialized, this method will throw a {@link RuntimeException}.
     * @throws Exception
     */
    public void executeRequestAsync() throws Exception {
        if ( this.serviceCallback == null ) {
            throw new RuntimeException("A service call back method has to be specified for " +
                    "all Async Requests. Use the ServiceCallBuilder class to initialize a " +
                    "service call back");
        }
        this.serviceCallback.OnServiceCallBack(executeRequest());
    }

    private void protocolLog(HttpURLConnection connection, int responseCode, String out) {
        if (shouldLog) {
            Log.d(TAG, "Request Method: " + connection.getRequestMethod());
            Log.d(TAG, "Request URL: " + connection.getURL().toString());
            Log.d(TAG, "Response Code: " + responseCode);
            if (!Strings.isNullOrEmpty(out)) {
                Log.d(TAG, "Api Response : " + out);
            }
        }
    }

    /**
     * Response from the service call.
     * @return {@link java.io.InputStream}
     *          of the requested file or
     *          binary content.
     *          The header passed will be
     *          of Content-Type : application/octet-stream
     */
    public InputStream executeRequestForStream() throws Exception {
        HttpURLConnection mConn;
        mConn = (HttpURLConnection)mUrl.openConnection();

        if ( headers != null ) {
            setHeaders(mConn);
        }

        if(isOverrideCache) {
            mConn.addRequestProperty("Cache-Control", "no-cache");
        } else {
            mConn.setDefaultUseCaches(true);
            mConn.setUseCaches(true);
        }

        mConn.setConnectTimeout(Integer.parseInt(connectionTimeout.toString()));
        mConn.setReadTimeout(Integer.parseInt(socketTimeout.toString()));

        mConn.setRequestMethod("GET");
        mConn.setDoInput(true);
        mConn.connect();

        int responseCode = mConn.getResponseCode();
        protocolLog(mConn, responseCode, null);

        //Cache the binary field.
        if(cache!= null) {
            cache.flush();
        }

        if (responseCode == 200) {
            return mConn.getInputStream();
        }

        return null;
    }

    /**
     * An asynchronous method of getting an input stream.
     * Uses {@link #executeRequestForStream()} and uses the
     * {@link #serviceCallback} to notify the caller of the
     * result wrapped in {@link Response} class.
     * @throws Exception
     */
    public void executeRequestForStreamAsync() throws Exception {
        if ( this.serviceCallback == null ) {
            throw new RuntimeException("A service call back method has to be specified for " +
                    "all Async Requests. Use the ServiceCallBuilder class to initialize a " +
                    "service call back");
        }
        final InputStream inputStream = executeRequestForStream();
        final Response response = new Response(inputStream);
        serviceCallback.OnServiceCallBack(response);
    }

    private void setHeaders(HttpURLConnection mConn) {
        if(headers!=null) {
            for(BasicHeader header : headers) {
                mConn.setRequestProperty(header.getName(), header.getValue());
            }
        }
    }

    private String readInputStream(InputStream is) {

        try {
            InputStreamReader inputstreamreader = new InputStreamReader(is);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
            String string;
            StringBuilder builder = new StringBuilder();

            while ((string = bufferedreader.readLine()) != null) {
                builder.append(string);
            }

            bufferedreader.close();
            return builder.toString();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            if (e.getLocalizedMessage() != null ) {
                return e.getLocalizedMessage();
            }

            return null;
        } finally {
            try {
                if ( is  != null ){
                    is.close();
                }
            } catch (Exception ignore) {
                Log.e(TAG, ignore.getLocalizedMessage(), ignore);
            }
        }
    }

    private String getResponseAsString(HttpURLConnection conn) {
        InputStream inputstream;
        try {
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <400) {
                inputstream = conn.getInputStream();
                if(cache!= null) {
                    cache.flush();
                    Log.i(TAG, "Hit Count " + cache.getHitCount());
                    Log.i(TAG, "Network Count " + cache.getNetworkCount());
                }
            } else {
                inputstream = conn.getErrorStream();
            }

            return readInputStream(inputstream);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }


    private void doPost(HttpURLConnection mConn) throws IOException{
        mConn.setRequestMethod(POST);
        mConn.setDoOutput(true); // indicates POST method
        mConn.setDoInput(true);
        if (!isMultipartPost) {
            String param = Utils.appendParams(this.params);
            mConn.setFixedLengthStreamingMode(param.getBytes().length);
            mConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            mConn.setRequestProperty("Accept", "application/json");
            mConn.connect();
            PrintWriter out = new PrintWriter(mConn.getOutputStream());
            out.print(param);
            out.close();
        } else {
            mConn.setUseCaches(false);
            mConn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + MULTI_PART_BOUNDARY);
            mConn.setRequestProperty("Accept", "application/json");
            mConn.connect();

            OutputStream outputStream = mConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(outputStream, "UTF-8"),
                    BUFFER_LENGTH);

            for (HttpParam httpParam : params ) {

                final String param = httpParam.getName();
                final Object value = httpParam.getValue();
                writer.write(TWO_HYPHEN);
                writer.write(MULTI_PART_BOUNDARY);
                writer.write(LINE_FEED);
                writer.flush();

                if (value instanceof byte[] || value instanceof InputStream) {

                    writer.write("Content-Type: application/octet-stream");
                    writer.write(LINE_FEED);
                    writer.write("Content-Transfer-Encoding: binary");
                    writer.write(LINE_FEED);
                    writer.write("Content-Disposition: form-data; name=\"");
                    writer.write(param);
                    writer.write("\"");
                    writer.write(LINE_FEED);
                    writer.write(LINE_FEED);
                    writer.flush();

                    if (value instanceof byte[]) {
                        outputStream.write((byte[]) value);
                    } else {
                        ByteStreams.copy((InputStream) value, outputStream);
                    }
                } else if (value instanceof List) {
                    List listValues = (List) value;
                    for (Object objVal : listValues) {
                        writeParam(writer, outputStream, param, objVal);
                    }
                } else {
                    writeParam(writer, outputStream, param, value);
                }
                outputStream.flush();
                writer.write(LINE_FEED);
            }

            writer.write(TWO_HYPHEN);
            writer.write(MULTI_PART_BOUNDARY);
            writer.write(TWO_HYPHEN);
            writer.write(LINE_FEED);
            writer.flush();

            /**
             * For some reason, multipart upload gives an
             * EOF exception if we try to read the response code
             * before reading the input stream. Maybe it executes the request
             * and reads the response when doing conn.getInputStream().
             *
             */

            //If this doesn't give any exceptions, then our
            //upload succeeded.
            mConn.getInputStream();
        }
    }

    private void writeParam(Writer writer, OutputStream outputStream, String param, Object value) throws IOException {
        writer.write("Content-Type: text/plain");
        writer.write(LINE_FEED);
        writer.write("Content-Disposition: form-data; name=\"");
        writer.write(param);
        writer.write("\"");
        writer.write(LINE_FEED);
        writer.write(LINE_FEED);
        writer.flush();

        outputStream.write(value.toString().getBytes());
    }


    /**
     *  Only for test.
     *  Call after mConn.getResponseCode().
     *  @param mConn
     */
    @SuppressWarnings("unused")
    private void printHeaders(HttpURLConnection mConn) {

        if ( mConn.getHeaderFields() != null ) {
            for (Map.Entry<String, List<String>> header : mConn.getHeaderFields().entrySet()) {

                if ( header.getKey() != null ) {
                    Log.d(TAG,header.getKey() + "=" + header.getValue() + "\n");
                }
            }
        }
    }

    /**
     * Call back interface to notify caller the result of
     * a network call.
     */
    public interface ServiceCallback {
        void OnServiceCallBack(Response response);
    }

}
