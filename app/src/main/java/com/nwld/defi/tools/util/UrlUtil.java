package com.nwld.defi.tools.util;

import android.net.Uri;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UrlUtil {
    public static HashMap<String, String> getSchemeParams(String scheme) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        if (StringUtil.isEmpty(scheme)) {
            return parameters;
        }
        Uri uri = Uri.parse(scheme);
        if (uri == null) {
            return parameters;
        }
        String query = "";
        int index = scheme.indexOf("?");
        if (index >= 0) {
            query = scheme.substring(index);
            query = query.replace("?", "");
        }
        String paras[] = query.split("&");
        for (String s : paras) {
            if (s.indexOf("=") != -1) {
                String[] item = s.split("=");
                if (item.length == 2) {
                    try {
                        parameters.put(item[0], URLDecoder.decode(item[1], "UTF-8"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return parameters;
                    }
                }
            }
        }
        return parameters;
    }

    public static HashMap<String, String> getUrlParams(String url) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        if (StringUtil.isEmpty(url)) {
            return parameters;
        }
        Uri uri = Uri.parse(url);
        if (uri == null) {
            return parameters;
        }
        String query = null;
        int qsi = url.indexOf('?', url.indexOf(':'));
        int cachedFsi = url.indexOf('#', url.indexOf(':'));
        LogUtil.e("qsi", qsi);
        LogUtil.e("cachedFsi", cachedFsi);
        if (null == query) {
            if (url.contains("?")) {
                query = url.substring(url.indexOf("?") + 1);
            }
        }
        LogUtil.e("query", query);
        if (StringUtil.isEmpty(query)) {
            return parameters;
        }
        String paras[] = query.split("&");
        for (String s : paras) {
            if (s.indexOf("=") != -1) {
                String[] item = s.split("=");
                if (item.length == 2) {
                    try {
                        parameters.put(item[0], URLDecoder.decode(item[1], "UTF-8"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return parameters;
                    }
                }
            }
        }
        return parameters;
    }

    public static String getScheme(String url) {
        if (StringUtil.isEmpty(url)) {
            return "";
        }
        Uri uri = Uri.parse(url);
        if (uri == null) {
            return "";
        }
        return uri.getScheme();
    }

    public static String getHost(String url) {
        if (StringUtil.isEmpty(url)) {
            return null;
        }
        Uri uri = Uri.parse(url);
        if (uri == null) {
            return null;
        }
        //系统方法得到host
        String host = uri.getHost();
        //没有://，需要自己处理
        if (StringUtil.isEmpty(host) && !url.contains("://")) {
            host = url.replace(uri.getScheme() + ":", "");
            if (host.contains("/")) {
                int index = host.indexOf("/");
                host = host.substring(0, index);
            } else if (host.contains("?")) {
                int index = host.indexOf("?");
                host = host.substring(0, index);
            }
        }
        return host;
    }

    public static String getHostPath(String url) {
        if (StringUtil.isEmpty(url)) {
            return null;
        }
        Uri uri = Uri.parse(url);
        if (uri == null) {
            return null;
        }
        if (url.contains("#")) {
            url = url.substring(0, url.indexOf("#"));
        }
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        return url;
    }

    public static String getPath(String url) {
        if (StringUtil.isEmpty(url)) {
            return "";
        }
        Uri uri = Uri.parse(url);
        if (uri == null) {
            return "";
        }
        String path = uri.getPath();
        if (StringUtil.isEmpty(path)) {
            path = "";
        }
        return path;
    }

    public static String appendGetParams(String url, Map<String, String> params) {
        if (null == url) {
            return "";
        }

        if (null == params || 0 == params.size()) {
            return url;
        }
        //链接本来的参数
        Map<String, String> urlParams = getUrlParams(url);
        if (null != urlParams && 0 < urlParams.size()) {
            Set<String> keySet = urlParams.keySet();
            for (String key : keySet) {
                if (params.containsKey(key)) {
                    params.remove(key);
                }
            }
        }
        if (0 == params.size()) {
            return url;
        }
        StringBuilder stringBuilder = new StringBuilder(url);
        if (url.contains("?")) {
            stringBuilder.append("&");
        } else {
            stringBuilder.append("?");
        }
        Set<Map.Entry<String, String>> entrySet = params.entrySet();
        int index = 0;
        for (Map.Entry<String, String> entry : entrySet) {
            if (null == entry.getValue()) {
                continue;
            }
            if (0 != index) {
                stringBuilder.append("&");
            }
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            index++;
        }

        return stringBuilder.toString();
    }

}
