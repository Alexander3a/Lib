package de.alex.internet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Basic {
    public static HashMap<String,String> cookieStore = new HashMap<>();
    @SuppressWarnings("HttpUrlsUsage")
    public static String getFromURL(String urlString, ArrayList<String> property) {
        cookieStore.clear();
        StringBuilder output = new StringBuilder();
        try {
            Boolean https = null;
            if(urlString.startsWith("http://"))https=false;
            if(urlString.startsWith("https://"))https=true;
            String host = Boolean.FALSE.equals(https) ? urlString.split("http://")[1].split("/")[0] : urlString.split("https://")[1].split("/")[0];
            String port = Boolean.TRUE.equals(https) ? "443": "80";
            String org_host=host;
            if(host.contains(":")){
                port=host.split(":")[1];
                host=host.split(":")[0];
            }
            URL url = new URL(Boolean.TRUE.equals(https) ?"https":"http",host,Integer.parseInt(port),!https ? urlString.replace("http://"+org_host,"") : urlString.replace("https://"+org_host,""));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.79 Safari/537.36");
            if(property!=null){
                property.forEach(x-> connection.setRequestProperty(x.split(":")[0],x.replace(x.split(":")[0]+":","")));
            }
            connection.setAllowUserInteraction(true);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            handleCookies(headerFields);
            BufferedReader bufferedReader;
            if(connection.getResponseCode()>=200 && connection.getResponseCode() < 300){
                bufferedReader= new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }else{
                bufferedReader= new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                output.append(line).append("\n");
            }
            bufferedReader.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return output.toString();
    }
    @SuppressWarnings("HttpUrlsUsage")
    @Deprecated
    public static String postToUrl(String urlString,String content, ArrayList<String> property) {
        cookieStore.clear();
        StringBuilder output = new StringBuilder();
        try {
            Boolean https = null;
            if(urlString.startsWith("http://"))https=false;
            if(urlString.startsWith("https://"))https=true;
            String host = Boolean.FALSE.equals(https) ? urlString.split("http://")[1].split("/")[0] : urlString.split("https://")[1].split("/")[0];
            String port = Boolean.TRUE.equals(https) ? "443": "80";
            String org_host=host;
            if(host.contains(":")){
                port=host.split(":")[1];
                host=host.split(":")[0];
            }
            URL url = new URL(Boolean.TRUE.equals(https) ?"https":"http",host,Integer.parseInt(port),!https ? urlString.replace("http://"+org_host,"") : urlString.replace("https://"+org_host,""));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.79 Safari/537.36");
            if(property!=null){
                property.forEach(x-> connection.setRequestProperty(x.split(":")[0],x.replace(x.split(":")[0]+":","")));
            }
            connection.setRequestMethod("POST");
            connection.setAllowUserInteraction(true);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = content.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            connection.connect();
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            handleCookies(headerFields);
            if (connection.getResponseCode() > 300 && connection.getResponseCode() < 307) {
                final String loc = connection.getHeaderField("Location");
                return loc;
            }
            BufferedReader bufferedReader;
            if(connection.getResponseCode()>=200 && connection.getResponseCode() < 300){
                bufferedReader= new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }else{
                bufferedReader= new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                output.append(line).append("\n");
            }
            bufferedReader.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return output.toString();
    }
    public static String requestToUrl(String urlString,String content,String methode, ArrayList<String> property) {
        cookieStore.clear();
        StringBuilder output = new StringBuilder();
        try {
            HttpURLConnection connection = getHttpURLConnection(urlString);
            connection.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.79 Safari/537.36");
            if(property!=null){
                property.forEach(x-> connection.setRequestProperty(x.split(":")[0],x.replace(x.split(":")[0]+":","")));
            }
            connection.setRequestMethod(methode);
            connection.setAllowUserInteraction(true);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            if(content.isEmpty()){
                try(OutputStream os = connection.getOutputStream()) {
                    byte[] input = content.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            connection.connect();
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            handleCookies(headerFields);
            if (connection.getResponseCode() > 300 && connection.getResponseCode() < 307) {
                final String loc = connection.getHeaderField("Location");
                return loc;
            }
            BufferedReader bufferedReader;
            if(connection.getResponseCode()>=200 && connection.getResponseCode() < 300){
                bufferedReader= new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }else{
                bufferedReader= new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                output.append(line).append("\n");
            }
            bufferedReader.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return output.toString();
    }

    private static HttpURLConnection getHttpURLConnection(String urlString) throws IOException {
        Boolean https = null;
        if(urlString.startsWith("http://"))https=false;
        if(urlString.startsWith("https://"))https=true;
        String host = Boolean.FALSE.equals(https) ? urlString.split("http://")[1].split("/")[0] : urlString.split("https://")[1].split("/")[0];
        String port = Boolean.TRUE.equals(https) ? "443": "80";
        String org_host=host;
        if(host.contains(":")){
            port=host.split(":")[1];
            host=host.split(":")[0];
        }
        URL url = new URL(Boolean.TRUE.equals(https) ?"https":"http",host,Integer.parseInt(port),!https ? urlString.replace("http://"+org_host,"") : urlString.replace("https://"+org_host,""));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return connection;
    }

    @SuppressWarnings("HttpUrlsUsage")
    public static String getRedirectUrl(String urlString, ArrayList<String> property) {
        cookieStore.clear();
        try {
            Boolean https = null;
            if(urlString.startsWith("http://"))https=false;
            if(urlString.startsWith("https://"))https=true;
            String host = Boolean.FALSE.equals(https) ? urlString.split("http://")[1].split("/")[0] : urlString.split("https://")[1].split("/")[0];
            String port = Boolean.TRUE.equals(https) ? "443": "80";
            String org_host=host;
            if(host.contains(":")){
                port=host.split(":")[1];
                host=host.split(":")[0];
            }
            URL url = new URL(Boolean.TRUE.equals(https) ?"https":"http",host,Integer.parseInt(port),!https ? urlString.replace("http://"+org_host,"") : urlString.replace("https://"+org_host,""));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.79 Safari/537.36");
            if(property!=null){
                property.forEach(x-> connection.setRequestProperty(x.split(":")[0],x.replace(x.split(":")[0]+":","")));
            }
            connection.setAllowUserInteraction(true);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            handleCookies(headerFields);
            final int stat = connection.getResponseCode();
            if (stat > 300 && stat < 307) {
                final String loc = connection.getHeaderField("Location");
                return loc;
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    private static void handleCookies(HttpURLConnection connection){
        connection.getHeaderFields().forEach((header,value)->{
            if(header==null)return;
            if(header.equalsIgnoreCase("set-cookie")){
                value.forEach(x->{
                    for (String s1 : x.split("; ")) {
                        try {
                            String[] sp_strings = s1.split("=");
                            if(sp_strings[0].equals("Path"))continue;
                            if(sp_strings[0].equals("SameSite"))continue;
                            cookieStore.put(sp_strings[0],sp_strings[1]);
                        }catch (Exception ignored){
                        }
                    }
                });
            }
        });
    }
    private static void handleCookies(Map<String, List<String>> headerFields){
        headerFields.forEach((header,value)->{
            if(header==null)return;
            if(header.equalsIgnoreCase("set-cookie")){
                value.forEach(x->{
                    for (String s1 : x.split("; ")) {
                        try {
                            String[] sp_strings = s1.split("=");
                            if(sp_strings[0].equals("Path"))continue;
                            if(sp_strings[0].equals("SameSite"))continue;
                            cookieStore.put(sp_strings[0],sp_strings[1]);
                        }catch (Exception ignored){
                        }
                    }
                });
            }
        });
    }
    public static void buildFromCookieStore(ArrayList<String> parms,HashMap<String,String> cookieStore){
        if(cookieStore.isEmpty())return;
        StringBuilder cookieLine = new StringBuilder();
        cookieStore.forEach((cookieName,cookieValue)->{
            if(cookieName.startsWith("path")){
                return;
            }
            cookieLine.append(cookieName).append("=").append(cookieValue).append("; ");
        });
        parms.add("Cookie: "+ cookieLine.substring(0,cookieLine.length()-2));
    }
    public static void buildFromCookieStore(ArrayList<String> parms){
        if(cookieStore.isEmpty())return;
        StringBuilder cookieLine = new StringBuilder();
        cookieStore.forEach((cookieName,cookieValue)->{
            if(cookieName.startsWith("path")){
                return;
            }
            cookieLine.append(cookieName).append("=").append(cookieValue).append("; ");
        });
        parms.add("Cookie: "+ cookieLine.substring(0,cookieLine.length()-2));
    }
}
