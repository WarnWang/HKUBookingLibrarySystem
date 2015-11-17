package hk.hku.cs.warn.hkubookinglibrary;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Author：warn on 11/16/15 09:04
 * Email：wangyouan@gmail.com
 */

public class HttpMethod {

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String COOKIES_HEADER = "Set-Cookie";
    private static final String LOCATION_HEADER = "Location";
    private static final int CONNECTION_TIMEOUT = 10000;

    private static HttpContent postContent, getContent;
    private CookieManager getCookieManager = new CookieManager();

    public HttpMethod(){
        postContent = new HttpContent();
        getContent = new HttpContent();
    }

    public HttpContent post(String link, final Map<String, String> data) {
        try {
            final URL url = new URL(link);
            Thread thread = new Thread() {
                public void run() {
                    try {
                        if (url.toString().startsWith("https"))
                            httpsPost(url, data);
                        else
                            httpPost(url, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            thread.join(CONNECTION_TIMEOUT);

        } catch (Exception e){
            e.printStackTrace();
            postContent.clear();
        }
        return postContent;
    }

    private void httpPost(URL url, Map<String, String> data) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(CONNECTION_TIMEOUT);

        //Set input and output stream:
        connection.setDoOutput(true);
        connection.setDoInput(true);

        //Post function cannot be cached
        connection.setUseCaches(false);

        List<String> params = new ArrayList<>();
        for (String key : data.keySet()) {
            params.add(URLEncoder.encode(key, "UTF-8") + "="
                    + URLEncoder.encode(data.get(key), "UTF-8"));
        }
        String param = TextUtils.join("&", params);

        // send the length of the headers to server
        connection.setFixedLengthStreamingMode(param.getBytes().length);
        connection.setRequestProperty("Content - Type", "application / x - www - form - urlencoded");

        // set the post out
        PrintWriter out = new PrintWriter(connection.getOutputStream());
        out.print(param);
        out.close();
        postContent.clear();
        postContent.setConnectStatusCode(connection.getResponseCode());
        postContent.setContent(readIt(connection.getInputStream()));
        postContent.setHeaders(connection.getHeaderFields());
    }

    private String readIt(InputStream stream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        String webPage = "", data;
        while ((data = reader.readLine()) != null) {
            webPage += data + "\n";
        }
        return webPage;
    }

    private void httpsPost(URL url, Map<String, String> data) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(CONNECTION_TIMEOUT);

        //Set input and output stream:
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(false);

        //Post function cannot be cached
        connection.setUseCaches(false);

        List<String> params = new ArrayList<>();
        for (String key : data.keySet()) {
            params.add(URLEncoder.encode(key, "UTF-8") + "="
                    + URLEncoder.encode(data.get(key), "UTF-8"));
        }
        String param = TextUtils.join("&", params);

        // send the length of the headers to server
        connection.setFixedLengthStreamingMode(param.getBytes().length);
        connection.setRequestProperty("Content - Type", "application / x - www - form - urlencoded");

        // set the post out
        PrintWriter out = new PrintWriter(connection.getOutputStream());
        out.print(param);
        out.close();
        postContent.clear();
        postContent.setConnectStatusCode(connection.getResponseCode());
        postContent.setHeaders(connection.getHeaderFields());
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            postContent.setContent(readIt(connection.getInputStream()));
    }

    public HttpContent get(String link, Map<String, String> data)
            throws UnsupportedEncodingException {
        URL url1;

        if (!data.isEmpty()) {
            if (!link.endsWith("?") && !link.contains("?"))
                link += "?";
            for (Map.Entry<String, String> entry : data.entrySet()) {
                if (link.endsWith("?")) {
                    link += URLEncoder.encode(entry.getKey(), DEFAULT_ENCODING)
                            + "=" + URLEncoder.encode(entry.getValue(), DEFAULT_ENCODING);
                } else {
                    link += "&" + URLEncoder.encode(entry.getKey(), DEFAULT_ENCODING)
                            + "=" + URLEncoder.encode(entry.getValue(), DEFAULT_ENCODING);
                }
            }
        }

        try {
            url1 = new URL(link);
            final URL myURL = url1;

            Thread thread = new Thread() {
                public void run() {
                    try {
                        if (myURL.toString().startsWith("https"))
                            httpsGet(myURL);
                        else
                            httpGet(myURL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
            getContent.clear();
        }
        return getContent;
    }

    private void httpGet(URL url) throws IOException, URISyntaxException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        URI uri = new URI(url.getProtocol(), null, url.getHost(), 80, null, null, null);

        // This is needed, as the default value in Android is true, set to false means to redirect
        // manually
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.addRequestProperty("Upgrade-Insecure-Requests", "1");
        if (getCookieManager.getCookieStore().getCookies().size() > 0) {
            conn.addRequestProperty("Cookie", TextUtils.join(";",
                    getCookieManager.getCookieStore().getCookies()));
        }

        getContent.clear();
        getContent.setHeaders(conn.getHeaderFields());
        getContent.setConnectStatusCode(conn.getResponseCode());

        // Means need to direct to some other place.
        if (conn.getResponseCode() == HttpURLConnection.HTTP_SEE_OTHER |
                conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            String anotherLocation = conn.getHeaderField(LOCATION_HEADER);
            if (conn.getHeaderField(COOKIES_HEADER) != null) {
                List<String> cookiesHeader = conn.getHeaderFields().get(COOKIES_HEADER);
                for (String cookie : cookiesHeader) {
                    getCookieManager.getCookieStore().add(uri, HttpCookie.parse(cookie).get(0));
                }
            }

            if (anotherLocation.startsWith("/")) {
                anotherLocation = url.getProtocol() + "://" + url.getHost() + anotherLocation;
            }
            get(anotherLocation, new HashMap<String, String>());
        } else {
            InputStream is = conn.getInputStream();
            getContent.setContent(this.readIt(is));
            is.close();
        }
    }

    private void httpsGet(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(CONNECTION_TIMEOUT);
        connection.connect();

        getContent.clear();
        getContent.setConnectStatusCode(connection.getResponseCode());
        getContent.setHeaders(connection.getHeaderFields());
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = connection.getInputStream();
            getContent.setContent(this.readIt(is));
            is.close();
        }
    }
}
