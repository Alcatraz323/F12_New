package io.alcatraz.f12;

import java.util.LinkedList;
import java.util.List;

import io.alcatraz.f12.beans.QueryElement;


public class Constants {
    public static String MY_PACKAGE_NAME = "io.alcatraz.f12";

    //===============================================

    //==============================================
    public static String BROADCAST_ACTION_UPDATE_PREFERENCES = "update_preferences";


    public static List<QueryElement> getOpenSourceProjects() {
        List<QueryElement> out = new LinkedList<>();

        //gson
        QueryElement o1 = new QueryElement();
        o1.setAuthor("google");
        o1.setUrl("https://github.com/google/gson");
        o1.setintro("A Java serialization/deserialization library to convert Java Objects into JSON and back");
        o1.setLicense("Apache 2.0");
        o1.setName("gson");
//        //OkHttp
//        QueryElement o2 = new QueryElement();
//        o2.setAuthor("square");
//        o2.setUrl("https://github.com/square/okhttp");
//        o2.setintro("An HTTP & HTTP/2 client for Android and Java applications. ");
//        o2.setLicense("Apache 2.0");
//        o2.setName("OkHttp");
//        //Okio
//        QueryElement o3 = new QueryElement();
//        o3.setAuthor("square");
//        o3.setUrl("https://github.com/square/okio");
//        o3.setintro("A modern I/O API for Java ");
//        o3.setLicense("Apache 2.0");
//        o3.setName("Okio");
//        //okhttputils
//        QueryElement o4 = new QueryElement();
//        o4.setAuthor("hongyangAndroid");
//        o4.setUrl("https://github.com/hongyangAndroid/okhttputils");
//        o4.setintro("okhttp的辅助类");
//        o4.setLicense("Apache 2.0");
//        o4.setName("okhttputils");

        //Adding Process
        out.add(o1);
//        out.add(o2);
//        out.add(o3);
//        out.add(o4);

        return out;
    }
}
