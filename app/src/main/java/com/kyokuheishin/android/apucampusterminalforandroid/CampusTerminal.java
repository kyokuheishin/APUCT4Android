package com.kyokuheishin.android.apucampusterminalforandroid;
/**
 * Created by qbx on 2017/4/20.
 */

/*
APU垃圾前端吃屎吧。这是我写完这个库想对你们说的全部的话。
 */
import android.app.Application;

import okhttp3.*;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CampusTerminal extends Application{
    /*バックグラウンドで実行いているため、Applicationを*/

    private ArrayList<String> mCookies = new ArrayList<>();
    private List<Cookie> cookies;

    OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder()
            .cookieJar(new CookieJar() {

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    CampusTerminal.this.cookies = cookies;
                    if (cookies != null){
                        mCookies.clear();
                        mCookies.add(cookies.toString());
                    }
//                    System.out.println("okhttpcookies");
//                    System.out.println(cookies.toString());
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    if (cookies != null){
//                        System.out.println(cookies);

                        return cookies;}
                    return new ArrayList<>();
                }
            }).build();
    /*cookiesを保存するように設定*/

    private  HashMap<String,ArrayList<String>> mHashMap = new HashMap<>();
    private  HashMap<String,ArrayList<String>> noticeMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String username = br.readLine();
        String password = br.readLine();


        CampusTerminal ct = new CampusTerminal();
        ct.ctSpTop();

        ct.ctLogin(username,password);

        CampusTerminal.ctMessage cm = ct.new ctMessage();
        cm.ctInformation();

        System.out.println(cm.ctGetMessageList(0));



    }

    Void ctSpTop() throws IOException{


        Request request = new Request.Builder()
                .url("https://portal2.apu.ac.jp/campusp/sptop.do")
                .build();
        /*ログインするためには、まずキャンパスターミナルのトップページのcookiesを取得する必要がある。なお原因は不明。*/


        Response response = mOkHttpClient.newCall(request).execute();
        if (response.isSuccessful()){
//            System.out.println("sptop");
//            System.out.println(response.header("Set-Cookie").getClass());
//            System.out.println(response.header("Set-Cookie"));
//
//            System.out.println(response.header("Set-Cookie").getClass());
        }

        return null;
    }

    private String br2nl(String html) {
        if(html==null)
            return html;
        Document document = Jsoup.parse(html);
//        document.outputSettings(new Document.OutputSettings().prettyPrint(true));//makes html() preserve linebreaks and spacing
        document.select("br").append("\\n");
        document.select("p").prepend("");
//        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\n");
        return Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }


    Boolean ctLogin(String username, String password) throws IOException {
/*
  ログイン処理
 */

         FormBody formBody = new FormBody.Builder()
                .addEncoded("forceDevice","sp")
                .addEncoded("lang","1")
                .addEncoded("userId",username)
                .addEncoded("password",password)
                .addEncoded("login", "login")
                .build();
/*
  ログインフォーム構成
 */

         Request request = new Request.Builder()
                .url("https://portal2.apu.ac.jp/campusp/splogin.do")
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .post(formBody)
                .build();

/*POSTリクエストサーバーに転送*/

        Response response = mOkHttpClient.newCall(request).execute();
////        System.out.println(response.header("Set-Cookie"));
//        System.out.println("logincookies");
//        System.out.println(response.header("Set-Cookie"));
        String html = response.body().string();
        Document document = Jsoup.parse(html);
        Elements elements = document.select("[style=\"color:red\"]");
        if (elements.size()>0){
            return false;
        }
        else return true;
    }


     public class ctMessage {
         /*メッセージに関する処理*/



        public void getInformationFromUniversity() throws IOException {
            ctGetMessageList(0);
        }

        void getImportantMessageToYou() throws IOException{
            ctGetMessageList(1);
        }

         void ctInformation() throws IOException{
             Request request = new Request.Builder()
                     .url("https://portal2.apu.ac.jp/campusp/wbspmgjr.do?clearAccessData=true&contenam=wbspmgjr&kjnmnNo=9")
                     .build();
             Response response = mOkHttpClient.newCall(request).execute();
//             System.out.println("informationcookies");
//             System.out.println(response.header("Set-Cookie"));
             /*メッセージ受信一覧の画面へアクセスするためのGETリクエスト*/

         }
         HashMap<String,ArrayList<String>> ctGetMessageList(int type) throws IOException{

            HashMap<String,ArrayList<String>> messageMap = new HashMap<>();

            String msgsyucds = "";

             int page = 0;
             ArrayList<String> titleList = new ArrayList<>();//タイトルを保存するためのArrayList
             ArrayList<String> dateListSending =new ArrayList<>();//受信日時を保存するためのArrayList
             ArrayList<String> dateListReading = new ArrayList<>();//既読日時を保存するためのArrayList
             ArrayList<String> sourceList =new ArrayList<>();//送信元を保存するためのArrayList
             ArrayList<String> linkList = new ArrayList<>();//リンクを保存するためのArrayList
            titleList.clear();
            dateListSending.clear();
            dateListReading.clear();
            sourceList.clear();
            linkList.clear();

            /*上記のArrayListの初期化*/


            if (type == 0){
                msgsyucds = "03";
            }
            else if (type == 1){
                msgsyucds = "05";
            }
            /*パラメータtypeの値によってアクセスするページが変わる
            * 0の場合は“大学からの情報”
            * 1の場合は“あなた宛の重要なお知らせ”
            * */


            Request request = new Request.Builder()
                    .url("https://portal2.apu.ac.jp/campusp/wbspmgjr.do?buttonName=searchList&msgsyucds="+msgsyucds)
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
//             System.out.println("listcookies");
//             System.out.println(response.header("Set-Cookie"));
             /*メッセージのリストを取得するためのGETリクエスト*/

            String html = response.body().string();
            Document document = Jsoup.parse(html);
            Elements cells = document.select("li a");

            for (Element cell :cells) {
                /*タイトル、受信日時、既読日時、送信元、リンクを各自のArrayListに追加するための繰り返し処理
                * JsoupというHTML処理ライブラリを使っている*/


                String title = cell.getElementsByTag("h4").text();
                String dateSending = cell.select(".date:eq(1)").text().substring(5);
                String dateReading = cell.select(".date:eq(2)").text();
                String source = cell.select("[style=\"white-space: normal;\"]").text().substring(5);
                String link =cell.attr("href");
                titleList.add(title);
                dateListSending.add(dateSending);
                dateListReading.add(dateReading);
                sourceList.add(source);
                linkList.add(link);
            }

            messageMap.put("title",titleList);
            messageMap.put("dateSending",dateListSending);
            messageMap.put("dateReading",dateListReading);
            messageMap.put("source",sourceList);
            messageMap.put("link",linkList);
            /*すべてのArrayListをHashmapに追加する*/

            page = 1;
//            System.out.println(messageMap);

            mHashMap = (HashMap<String, ArrayList<String>>) messageMap.clone();

            return messageMap;

        }

         HashMap<String,ArrayList<String>>  ctGetMessageListNextPage() throws IOException{


             int page = 0;
             ArrayList<String> titleList = new ArrayList<>();//タイトルを保存するためのArrayList
             ArrayList<String> dateListSending =new ArrayList<>();//受信日時を保存するためのArrayList
             ArrayList<String> dateListReading = new ArrayList<>();//既読日時を保存するためのArrayList
             ArrayList<String> sourceList =new ArrayList<>();//送信元を保存するためのArrayList
             ArrayList<String> linkList = new ArrayList<>();//リンクを保存するためのArrayList
            /*次のページを読み込むに関する処理*/
            FormBody formBody = new FormBody.Builder()
                    .addEncoded("buttonName","backToList")

                    .addEncoded("changeStateList","次の5件を読み込む")

                    .build();

             Request request = new Request.Builder()
                    .url("https://portal2.apu.ac.jp/campusp/wbspmgjr.do")

                    .post(formBody)
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
//             System.out.println("nextpagecookies");
//             System.out.println(response.header("Set-Cookie"));
            String html = response.body().string();

            Document document = Jsoup.parse(html);

            Elements cells = document.select("li a");

             titleList.clear();
             dateListSending.clear();
             dateListReading.clear();
             sourceList.clear();
             linkList.clear();
             mHashMap.clear();
            for (Element cell :cells) {
                String title = cell.getElementsByTag("h4").text();
                String dateSending = cell.select(".date:eq(1)").text().substring(5);
                String dateReading = cell.select(".date:eq(2)").text();
                String source = cell.select("[style=\"white-space: normal;\"]").text().substring(5);
                String link =cell.attr("href");
                titleList.add(title);
                dateListSending.add(dateSending);
                dateListReading.add(dateReading);
                sourceList.add(source);
                linkList.add(link);
            }
             mHashMap.put("title",titleList);
             mHashMap.put("dateSending",dateListSending);
             mHashMap.put("dateReading",dateListReading);
             mHashMap.put("source",sourceList);
             mHashMap.put("link",linkList);
//            System.out.println(titleList);
//            System.out.println(dateListReading);
//            System.out.println(dateListSending);
//            System.out.println(sourceList);
//            System.out.println(linkList);

             return mHashMap;
        }

        HashMap ctGetMessageDetail(int messageNo) throws IOException{
            /*詳細メッセージに関する処理*/

            
            ArrayList<String> bodyList = new ArrayList<>();
            ArrayList<String> otherInformationList = new ArrayList<>();
            ArrayList<String> otherInformationContentList = new ArrayList<>();
            ArrayList<String> otherInformationLinkList = new ArrayList<>();
            ArrayList<String> otherInformationLinkTitleList = new ArrayList<>();
            ArrayList<String> otherInformationFileTitleList = new ArrayList<>();
            ArrayList<String> otherInformationFileLinkList = new ArrayList<>();
            ArrayList<String> links = mHashMap.get("link");

            Request request = new Request.Builder()
                    .url("https://portal2.apu.ac.jp/campusp/"+ links.get(messageNo))
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
//            System.out.println("detailcookies");
//            System.out.println(response.header("Set-Cookie"));
            String html = response.body().string();

            Document document = Jsoup.parse(html);

            document.outputSettings(new Document.OutputSettings().prettyPrint(false));

            Element detail = document.getElementById("detail");

            String body = detail.select("p:not(.content)").toString();
            bodyList.add(br2nl(body).trim());


            Elements otherInformations = detail.select("font.label");
            Elements otherInformationContents = detail.select("p.content:not(:has(font)):not(:has(a))");
            Elements otherInformationFileTitles = detail.select("p.content:has(a)");
            Elements otherInformationLinks = detail.select("a:not([data-role])").select("a:not([onclick])");
            Elements otherInformationFileLinks = detail.select("a:not([target])").select("a:not([data-role])");


            for (Element otherInformation : otherInformations){
                otherInformationList.add(otherInformation.text());
//                System.out.println(otherInformation.text());
            }

            for (Element otherInformationContent : otherInformationContents){
                if (! otherInformationContent.text().equals("")){
                        otherInformationContentList.add(otherInformationContent.text());
                    }

//                System.out.println(otherInformationContent.text());
            }
            for (Element otherInformationLink:otherInformationLinks){
//                System.out.println(otherInformationLink.text());
                otherInformationLinkTitleList.add(otherInformationLink.text());
                otherInformationLinkList.add(otherInformationLink.attr("href"));
            }

            for (Element otherInformationFileLink:otherInformationFileLinks){
//                System.out.println(otherInformationFileLink.attr("href"));
                otherInformationFileLinkList.add(otherInformationFileLink.attr("href"));
            }

            for (Element otherInformationFileTitle:otherInformationFileTitles){
//                System.out.println(otherInformationFileTitle.text());
                otherInformationFileTitleList.add(otherInformationFileTitle.text());
            }
//            mCookies.add(cookies.toString());
            mHashMap.put("body",bodyList);
            mHashMap.put("otherInformationTitle",otherInformationList);
            mHashMap.put("otherInformationContent",otherInformationContentList);
            mHashMap.put("otherInformationLinkTitle",otherInformationLinkTitleList);
            mHashMap.put("otherInformationLink",otherInformationLinkList);
            mHashMap.put("otherInformationFileTitle",otherInformationFileTitleList);
            mHashMap.put("otherInformationFileLink",otherInformationFileLinkList);
            mHashMap.put("cookies",mCookies);

//            System.out.println(mHashMap);

            return mHashMap;


        }
    }

    /*以下の機能はまだ実装していない。*/

     class ctCourseNotice{
         ArrayList<String> titleList = new ArrayList<>();
         ArrayList<String> timeList = new ArrayList<>();
         ArrayList<String> teacherList = new ArrayList<>();
        ArrayList<String> contentList = new ArrayList<>();
        ArrayList<String> noticeSendingDateList = new ArrayList<>();

        HashMap ctGetCourseNoticeList() throws IOException {
            noticeMap.clear();
            titleList.clear();
            timeList.clear();
            teacherList.clear();
            contentList.clear();
            noticeSendingDateList.clear();
            File input = new File("C:\\Users\\qbx\\Documents\\Campus Terminal.html");
            Document doc = Jsoup.parse(input,"UTF-8");
            Elements cells = doc.select("a.ui-link-inherit");
            for (Element cell : cells){
                String title = cell.select(".rsunamC").text();
                String time = cell.select(".yobijigen").text();
                String teacher = cell.select(".shimei").text();
                String content = cell.select(".ui-li-desc:eq(4)").text();
                String NoticeSendingDate = cell.select(".ui-li-desc:eq(5)").text();
                titleList.add(title);
                timeList.add(time);
                teacherList.add(teacher);
                contentList.add(content);
                noticeSendingDateList.add(NoticeSendingDate);
            }
            noticeMap.put("title",titleList);
            noticeMap.put("time",timeList);
            noticeMap.put("teacher",teacherList);
            noticeMap.put("content", contentList);
            noticeMap.put("noticeSendingDate",noticeSendingDateList);
//            System.out.println(noticeMap);

            return noticeMap;
        }

        void ctGetCourseNoticeDetail() throws IOException {
             ArrayList<String> titleList = new ArrayList<>();
             ArrayList<String> informationTitleList = new ArrayList<>();
            ArrayList<String> informationContentList = new ArrayList<>();
            File input = new File("C:\\Users\\qbx\\Documents\\Campus Terminal3.html");
            Document doc = Jsoup.parse(input,"UTF-8");
            Elements mainTitles = doc.select("[style=\"font-size: 1.8em; margin-top: 0px; margin-bottom: 0px;x\"]");
            Elements subTitles = doc.select("[style=\"font-size: 1em;\"]");
            Elements informationTitles = doc.select(".label");
            Elements informationContents = doc.select("p.content");
            titleList.add(mainTitles.text());
            titleList.add(subTitles.text());
            for (Element informationTitle:informationTitles){
                    informationTitleList.add(informationTitle.text());
                }
            for (Element informationContent:informationContents){
                informationContentList.add(informationContent.text());
            }



//            System.out.println(informationTitleList);
//            System.out.println(informationContentList);
        }
    }


}
