package de.alex.internet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Basic {
    @SuppressWarnings("HttpUrlsUsage")
    public static String getFromURL(String urlString, ArrayList<String> property) {
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
            BufferedReader bufferedReader;
            if(connection.getResponseCode()==200){
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
    public static String getRedirectUrl(String urlString, ArrayList<String> property) {
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
}
