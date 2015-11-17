package hk.hku.cs.warn.hkubookinglibrary;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Author：warn on 11/16/15 09:37
 * Email：wangyouan@gmail.com
 */

public class HttpContent {
    private String content;
    private Map<String, List<String>> headers;
    private int connectionStatusCode;

    public Map getHeaders(){
        return headers;
    }

    public List<String> getHeader(String header) {
        return headers.get(header);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String newContent) {
        content = newContent;
    }

    public void setHeaders(Map newHeaders) {
        headers = newHeaders;
//        Iterator<Map.Entry<String, List<String>>> iterator = headers.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, List<String>> entry = iterator.next();
//            if (entry.getKey() == null) {
//                iterator.remove();
//            }
//        }
    }

    public int getConnectStatusCode(){
        return connectionStatusCode;
    }

    public void setConnectStatusCode(int code){
        connectionStatusCode = code;
    }

    public void clear(){
        connectionStatusCode = 0;
        content = null;
        headers = null;
    }
}
