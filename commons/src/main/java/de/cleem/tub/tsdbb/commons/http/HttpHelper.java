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
    public static final String HEADER_KEY_CONTENT_TYPE = "Content-type";
    public static final String HEADER_KEY_CONTENT_TYPE_VALUE_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String HEADER_KEY_CONTENT_TYPE_VALUE_JSON = "application/json";
    private static final String HEADER_KEY_ACCEPT = "Accept";

    public static String executePost(final HttpClient httpClient,
                                     final URI uri,
                                     final String body,
                                     final HashMap<String, String> headers,
                                     final int expectedHttpCode) throws HttpException {

        final HttpRequest request = createRequest(uri, "POST", body, headers);

        return sendRequest(httpClient, request, expectedHttpCode,body);

    }

    public static String executeGet(final HttpClient httpClient,
                                    final URI uri,
                                    final String body,
                                    final HashMap<String, String> headers,
                                    final int expectedHttpCode) throws HttpException {

        final HttpRequest request = createRequest(uri, "GET", body, headers);

        return sendRequest(httpClient, request, expectedHttpCode,body);

    }


    public static String executeDelete(final HttpClient httpClient,
                                       final URI uri,
                                       final String body,
                                       final HashMap<String, String> headers,
                                       final int expectedHttpCode) throws HttpException {

        final HttpRequest request = createRequest(uri, "DELETE", body, headers);

        return sendRequest(httpClient, request, expectedHttpCode,body);

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

            log.debug("OUT: Http {} {} requestBody:\n{}\n",request.method(),request.uri(),requestBody);

            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            final String responseString = response.body();

            log.debug("IN: Http {} {} requestBody:\n{}\nresponseBody:\n{}\n\n",request.method(),request.uri(),requestBody,responseString);

            checkResponse(response, expectedHttpCode,requestBody);

            return responseString;

        } catch (IOException | InterruptedException e) {
            throw new HttpException(e);
        }


    }

    private static <T> void checkResponse(final HttpResponse<T> response, final int expectedHttpCode, final String requestBody) throws HttpException {

        if (response == null) {
            throw new HttpException("Can not check http response - response is NULL");
        }

        if (response.statusCode() != expectedHttpCode) {
            throw new HttpException("Error requesting: " + response.uri() + " - " + response.statusCode() + " - expected " + expectedHttpCode + " - response " + response.body()+" - "+requestBody);
        }

    }


    public static HashMap<String, String> getTokenAuthHeaderMap(final String token) {

        final HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put(HEADER_KEY_AUTHORIZATION, HEADER_KEY_AUTHORIZATION_VALUE_PREFIX_TOKEN + token);

        return headerMap;
    }


    public static HashMap<String, String> getAcceptContentTypeTokenHeaderMap(final String token, final String accept, final String contentType) {

        final HashMap<String, String> headerMap = getTokenAuthHeaderMap(token);

        if(accept!=null){
            headerMap.put(HEADER_KEY_ACCEPT, accept);
        }
        if(contentType!=null){
            headerMap.put(HEADER_KEY_CONTENT_TYPE, contentType);
        }

        return headerMap;
    }



}
