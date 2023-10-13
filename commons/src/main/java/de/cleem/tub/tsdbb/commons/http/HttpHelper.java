package de.cleem.tub.tsdbb.commons.http;

import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j

public class HttpHelper extends BaseClass {

    private static final String HEADER_KEY_AUTHORIZATION = "Authorization";
    private static final String HEADER_KEY_AUTHORIZATION_VALUE_PREFIX_TOKEN = "Token ";
    public static final String HEADER_KEY_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_KEY_CONTENT_TYPE_CSV = "application/csv";

    public static final String HEADER_KEY_CONTENT_TYPE_VALUE_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String HEADER_KEY_CONTENT_TYPE_VALUE_JSON = "application/json";
    public static final String HEADER_KEY_ACCEPT = "Accept";
    private static final boolean DEBUG_HTTP = true;

    public static String executePost(final HttpClient httpClient,
                                     final URI uri,
                                     final String body,
                                     final HashMap<String, String> headers,
                                     final int expectedHttpCode) throws HttpException {

        final HttpRequest request = createRequest(uri, "POST", body, headers);

        return sendRequest(httpClient, request, expectedHttpCode, body);

    }

    public static String executeGet(final HttpClient httpClient,
                                    final URI uri,
                                    final String body,
                                    final HashMap<String, String> headers,
                                    final int expectedHttpCode) throws HttpException {

        final HttpRequest request = createRequest(uri, "GET", body, headers);

        return sendRequest(httpClient, request, expectedHttpCode, body);

    }


    public static String executeDelete(final HttpClient httpClient,
                                       final URI uri,
                                       final String body,
                                       final HashMap<String, String> headers,
                                       final int expectedHttpCode) throws HttpException {

        final HttpRequest request = createRequest(uri, "DELETE", body, headers);

        return sendRequest(httpClient, request, expectedHttpCode, body);

    }


    ///////

    private static HttpRequest createRequest(final URI uri, final String method, final String body, final HashMap<String, String> headers) throws HttpException {

        if (uri == null) {
            throw new HttpException("No URI provided");

        }

        if (method == null) {
            throw new HttpException("No HTTP Method provided requesting URI: " + uri);
        }

        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri);

        if (body == null) {
            requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(body));
        }

        if (headers != null && headers.size() > 0) {

            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {

                requestBuilder.header(headerEntry.getKey(), headerEntry.getValue());


            }

        }

        return requestBuilder.build();


    }

    private static String sendRequest(final HttpClient httpClient, final HttpRequest request, final int expectedHttpCode, final String requestBody) throws HttpException {

        if (httpClient == null) {
            throw new HttpException("Can not execute http " + request.method() + "Request to " + request.uri() + " - client is NULL");
        }
        if (request == null) {
            throw new HttpException("Can not execute http Request - request is NULL");
        }

        try {

            logHttp(request, requestBody, null);

            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            final String responseBody = response.body();

            logHttp(request, requestBody, response);

            checkResponse(response, expectedHttpCode, requestBody);

            return responseBody;

        } catch (IOException | InterruptedException e) {
            throw new HttpException(e);
        }


    }

    private static void logHttp(final HttpRequest request, final String requestBody, final HttpResponse<String> response) {

        if (DEBUG_HTTP == true) {

            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("HTTP ");
            if (response != null) {
                stringBuilder.append("IN");
            } else {
                stringBuilder.append("OUT");
            }




            stringBuilder.append("\n");
            stringBuilder.append("method:");
            stringBuilder.append(" ");
            stringBuilder.append(request.method());

            stringBuilder.append("\n");
            stringBuilder.append("uri:");
            stringBuilder.append(" ");
            stringBuilder.append(request.uri());


            if (requestBody != null) {
                stringBuilder.append("\n");
                stringBuilder.append("requestBody:");
                stringBuilder.append(" ");
                stringBuilder.append(requestBody);
            }

            if (response!=null) {
                stringBuilder.append("\n");
                stringBuilder.append("responseStatus:");
                stringBuilder.append(" ");
                stringBuilder.append(response.statusCode());

                if (response.body() != null && response.body().length()>0) {
                    stringBuilder.append("\n");
                    stringBuilder.append("responseBody:");
                    stringBuilder.append(" ");
                    stringBuilder.append(response.body());
                }

            }

            stringBuilder.append("\n");

            log.debug(stringBuilder.toString());

        }
    }

    private static <T> void checkResponse(final HttpResponse<T> response, final int expectedHttpCode, final String requestBody) throws HttpException {

        if (response == null) {
            throw new HttpException("Can not check http response - response is NULL");
        }

        if (response.statusCode() != expectedHttpCode) {
            throw new HttpException("Error requesting: " + response.uri() + " - " + response.statusCode() + " - expected " + expectedHttpCode + " - response " + response.body() + " - " + requestBody);
        }

    }


    public static HashMap<String, String> getTokenAuthHeaderMap(final String token) {

        final HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put(HEADER_KEY_AUTHORIZATION, HEADER_KEY_AUTHORIZATION_VALUE_PREFIX_TOKEN + token);

        return headerMap;
    }


    public static HashMap<String, String> getAcceptContentTypeTokenHeaderMap(final String token, final String accept, final String contentType) {

        final HashMap<String, String> headerMap = getTokenAuthHeaderMap(token);

        if (accept != null) {
            headerMap.put(HEADER_KEY_ACCEPT, accept);
        }
        if (contentType != null) {
            headerMap.put(HEADER_KEY_CONTENT_TYPE, contentType);
        }

        return headerMap;
    }


}
