package com.example.user.demovolley3;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class status {
    public static String MySQLI(String i){
        String result="";
        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://phpmyadmin-t106362512project.a3c1.starter-us-west-1.openshiftapps.com/examples/status.php?status="+i);
            System.out.println("http://phpmyadmin-t106362512project.a3c1.starter-us-west-1.openshiftapps.com/examples/status.php?status="+i);
            //get.addHeader("Cookie","__test=6a3f1c5c9e70c6bca4ceeb64d97ea837;expires=Thu,31-Dec-37 23:55:55 GMT;path=/");

            HttpResponse response = httpClient.execute(get);
            HttpEntity resEntity = response.getEntity();
            //result = EntityUtils.toString(resEntity);

        }catch(Exception e){
            result = e.toString();

        }
        return result;
    }
}
