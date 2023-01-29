package de.cleem.bm.tsdb.common.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class HttpHelper {


    public static String executePost(final HttpClient httpClient,
                                     final URI uri,
                                     final String body,
                                     final HashMap<String,String> headers,
                                     final int expectedHttpCode) throws HttpException {

            final HttpRequest request = createRequest(uri,"POST",body, headers);

            return sendRequest(httpClient,request,expectedHttpCode);

    }

    public static String executeGet(final HttpClient httpClient,
                                    final URI uri,
                                    final String body,
                                    final HashMap<String,String> headers,
                                    final int expectedHttpCode) throws HttpException {

            final HttpRequest request = createRequest(uri,"GET",body,headers);

            return sendRequest(httpClient,request,expectedHttpCode);

    }


    public static String executeDelete(final HttpClient httpClient,
                                    final URI uri,
                                    final String body,
                                    final HashMap<String,String> headers,
                                    final int expectedHttpCode) throws HttpException {

        final HttpRequest request = createRequest(uri,"DELETE",body,headers);

        return sendRequest(httpClient,request,expectedHttpCode);

    }


    ///////

    private static HttpRequest createRequest(final URI uri, final String method, final String body, final HashMap<String,String> headers) throws HttpException{

        if(uri == null){
            throw new HttpException("No URI provided");

        }

        if(method==null){
            throw new HttpException("No HTTP Method provided requesting URI: "+uri);
        }

        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri);

        if(body==null){
            requestBuilder.method(method,HttpRequest.BodyPublishers.noBody());
        }
        else{
            requestBuilder.method(method,HttpRequest.BodyPublishers.ofString(body));
        }

        if(headers!=null && headers.size()>0){

            for(Map.Entry<String, String> headerEntry : headers.entrySet()){

                requestBuilder.header(headerEntry.getKey(),headerEntry.getValue());


            }

        }

        return requestBuilder.build();


    }

    private static String sendRequest(final HttpClient httpClient, final HttpRequest request, final int expectedHttpCode) throws HttpException {

        if (httpClient == null) {
            throw new HttpException("Can not execute http "+request.method()+"Request to "+request.uri()+" - client is NULL");
        }
        if (request == null) {
            throw new HttpException("Can not execute http Request - request is NULL");
        }

        try {

            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            checkResponse(response,expectedHttpCode);

            return response.body();

        } catch (IOException | InterruptedException e) {
            throw new HttpException(e);
        }


    }

    private static <T> void checkResponse(final HttpResponse<T> response, final int expectedHttpCode) throws HttpException {

        if (response == null) {
            throw new HttpException("Can not check http response - response is NULL");
        }

        if(response.statusCode()!=expectedHttpCode){
            throw new HttpException("Error requesting: "+response.uri()+" - "+response.statusCode()+" - expected "+expectedHttpCode+" - body "+response.body());
        }

    }


}
