package com.example.user.demovolley3;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by user on 2018/6/19.
 */

public class ControlPHP {
    public static String del(String i){
        String result="";
        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://phpmyadmin-t106362512project.a3c1.starter-us-west-1.openshiftapps.com/examples/del.php?ID="+i);
            System.out.println("http://phpmyadmin-t106362512project.a3c1.starter-us-west-1.openshiftapps.com/examples/del.php?ID="+i);
            httpClient.execute(get);

        }catch(Exception e){
            result = e.toString();

        }
        return result;
    }

    public static String refelsh(){
        String result="";
        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://phpmyadmin-t106362512project.a3c1.starter-us-west-1.openshiftapps.com/examples/re.php");
            System.out.println("http://phpmyadmin-t106362512project.a3c1.starter-us-west-1.openshiftapps.com/examples/re.php");
            httpClient.execute(get);

        }catch(Exception e){
            result = e.toString();

        }
        return result;
    }

    public static String status(String i){
        String result="";
        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://phpmyadmin-t106362512project.a3c1.starter-us-west-1.openshiftapps.com/examples/status.php?status="+i);
            System.out.println("http://phpmyadmin-t106362512project.a3c1.starter-us-west-1.openshiftapps.com/examples/status.php?status="+i);

            httpClient.execute(get);

        }catch(Exception e){
            result = e.toString();

        }
        return result;
    }
}
