package com.kyokuheishin.android.apucampusterminalforandroid; /**
 * Created by qbx on 2017/4/20.
 */

/*
APU垃圾前端吃屎吧。这是我写完这个库想对你们说的全部的话。
 */
import android.app.Activity;
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
//    private static HashMap informationMap;
//    private static HashMap<String, ArrayList<String>> informationMap = new HashMap<>();
    private  HashMap<String,ArrayList<String>> messageMap = new HashMap<>();
    private  HashMap<String,ArrayList<String>> noticeMap = new HashMap<>();
    OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder()
            .cookieJar(new CookieJar() {
                List<Cookie> cookies;
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    this.cookies = cookies;
                    System.out.println(cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    if (cookies != null){
                        System.out.println(cookies);
                        return cookies;}
                    return new ArrayList<>();
                }
            }).build();

    Void ctSpTop() throws IOException{


        Request request = new Request.Builder()
                .url("https://portal2.apu.ac.jp/campusp/sptop.do")
//                .addHeader("Cookie",JsessionID)
//                .header("Host","portal2.apu.ac.jp")
//                .header("Origin","https://portal2.apu.ac.jp")
//                .header("Referer","https://portal2.apu.ac.jp/campusp/sptop.do")
//                .header("Upgrade-Insecure-Requests","1")
//                .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
//                .header("Content-Type","application/x-www-form-urlencoded")
//                .post(formBodya)
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        System.out.println(response.header("Set-Cookie"));
        return null;
    }
     public class ctMessage {
        private  int page = 0;
        private  ArrayList<String> titleList = new ArrayList<>();
        private  ArrayList<String> dateListSending =new ArrayList<String>();
        private  ArrayList<String> dateListReading = new ArrayList<String>();
        private  ArrayList<String> sourceList =new ArrayList<>();
        private  ArrayList<String> linkList = new ArrayList<>();

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
             String result = mOkHttpClient.newCall(request).execute().body().string();
             System.out.println(result);
//        response.header("Cookie")

         }
         HashMap<String,ArrayList<String>> ctGetMessageList(int type) throws IOException{
//            HashMap<String, ArrayList<String>> map = new HashMap<>();
            String msgsyucds = new String();
            titleList.clear();
            dateListSending.clear();
            dateListReading.clear();
            sourceList.clear();
            linkList.clear();
            messageMap.clear();
            if (type == 0){
                msgsyucds = "03";
            }
            else if (type == 1){
                msgsyucds = "05";
            }

//        HashMap informationMap = new HashMap();
            Request request = new Request.Builder()
                    .url("https://portal2.apu.ac.jp/campusp/wbspmgjr.do?buttonName=searchList&msgsyucds="+msgsyucds)
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
            String html = response.body().string();
//        System.out.println(response.body().toString());
            Document document = Jsoup.parse(html);
            Elements cells = document.select("li a");

            for (Element cell :cells) {
                String title = cell.getElementsByTag("h4").text();
                String dateSending = cell.select(".date:eq(1)").text();
                String dateReading = cell.select(".date:eq(2)").text();
                String source = cell.select("[style=\"white-space: normal;\"]").text();
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
            page = 1;
            System.out.println(messageMap);

            return messageMap;

        }

         HashMap<String,ArrayList<String>>  ctGetMessageListNextPage() throws IOException{
//            System.out.println(String.valueOf(System.currentTimeMillis()));
            FormBody formBody = new FormBody.Builder()
                    .addEncoded("buttonName","backToList")
//                    .addEncoded("timestamp",String.valueOf(System.currentTimeMillis()))
                    .addEncoded("changeStateList","次の5件を読み込む")
//                    .addEncoded("value(mssgcmnt)","")
                    .build();

             Request request = new Request.Builder()
                    .url("https://portal2.apu.ac.jp/campusp/wbspmgjr.do")
//                    .addHeader("Referer","https://portal2.apu.ac.jp/campusp/wbspmgjr.do?buttonName=selectDetail&selectDetailIndex=0")
                    .post(formBody)
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
            String html = response.body().string();
//            System.out.println(html);
            Document document = Jsoup.parse(html);
//            Elements title = document.getElementsByTag("h4");
//            System.out.println(title.size());
//            System.out.println(title.text());
//            Elements cells = document.select("li a:gt("+String.valueOf(page*5-1)+")");
            Elements cells = document.select("li a");
//            for (int i = page*5-1;i>=0;i--){
//                cells.remove(i);
//            }
             titleList.clear();
             dateListSending.clear();
             dateListReading.clear();
             sourceList.clear();
             linkList.clear();
             messageMap.clear();
            for (Element cell :cells) {
                String title = cell.getElementsByTag("h4").text();
                String dateSending = cell.select(".date:eq(1)").text();
                String dateReading = cell.select(".date:eq(2)").text();
                String source = cell.select("[style=\"white-space: normal;\"]").text();
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
            System.out.println(titleList);
            System.out.println(dateListReading);
            System.out.println(dateListSending);
            System.out.println(sourceList);
            System.out.println(linkList);

             return messageMap;
        }

        HashMap ctGetMessageDetail(int messageNo) throws IOException{

            HashMap<String,ArrayList<String>> detailMap = new HashMap();
            ArrayList<String> bodyList = new ArrayList<>();
            ArrayList<String> otherInformationList = new ArrayList<>();
            ArrayList<String> otherInformationContentList = new ArrayList<>();
            ArrayList<String> otherInformationLinkList = new ArrayList<>();
            ArrayList<String> otherInformationFileLinkList = new ArrayList<>();
            ArrayList<String> links = messageMap.get("link");

            Request request = new Request.Builder()
                    .url("https://portal2.apu.ac.jp/campusp/"+ links.get(messageNo))
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
            String html = response.body().string();
//        System.out.println(html);
//        System.out.println(response.body().toString());
            Document document = Jsoup.parse(html);
//        System.out.println(document.body().text());
            document.outputSettings(new Document.OutputSettings().prettyPrint(false));
//        System.out.println(document.text());
            Element detail = document.getElementById("detail");
//        System.out.println(detail.text());
//        String content = detail.select("h3:eq(1)").text();
//        System.out.println(content);
            String body = detail.select("p:not(.content)").toString();
            bodyList.add(br2nl(body));

//        System.out.println(br2nl(body));
            Elements otherInformations = detail.select("font.label");
            Elements otherInformationContents = detail.select(".content");
            Elements otherInformationLinks = detail.select("a:not([data-role])").select("a:not([onclick])");
            Elements otherInformationFileLinks = detail.select("a:not([target])").select("a:not([data-role])");

//        System.out.println(otherInformationContent.text());
            for (Element otherInformation : otherInformations){
                otherInformationList.add(otherInformation.text());
                System.out.println(otherInformation.text());
            }

            for (Element otherInformationContent : otherInformationContents){
                otherInformationContentList.add(otherInformationContent.text());
                System.out.println(otherInformationContent.text());
            }
            for (Element otherInformationLink:otherInformationLinks){
                System.out.println(otherInformationLink.text());
                otherInformationLinkList.add(otherInformationLink.attr("href"));
            }

            for (Element otherInformationFileLink:otherInformationFileLinks){
                System.out.println(otherInformationFileLink.attr("href"));
                otherInformationFileLinkList.add(otherInformationFileLink.attr("href"));
            }
            detailMap.put("body",bodyList);
            detailMap.put("otherInformationTitle",otherInformationList);
            detailMap.put("otherInformationContent",otherInformationContentList);
            detailMap.put("otherInformationLink",otherInformationLinkList);
            detailMap.put("otherInformationFileLink",otherInformationFileLinkList);
            System.out.println(detailMap);

            return detailMap;
//        if ()
//        String otherInformation = detail.select("font.label").text();
//        System.out.println(otherInformation);

        }
    }

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
            System.out.println(noticeMap);

            return noticeMap;
//            Elements titles = doc.select(".rsunamC");
//            Elements times = doc.select(".yobijigen");
//            Elements teachers =doc.select(".shimei");
//            Elements dates = doc.select(".ui-li-desc");
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


//            System.out.println(titleList);
            System.out.println(informationTitleList);
            System.out.println(informationContentList);
        }
    }




    private String br2nl(String html) {
        if(html==null)
            return html;
        Document document = Jsoup.parse(html);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\n");
        return Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

     String cleanPreserveLineBreaks(String bodyHtml) {

        // get pretty printed html with preserved br and p tags
        String prettyPrintedBodyFragment = Jsoup.clean(bodyHtml, "", Whitelist.none().addTags("br", "p"), new Document.OutputSettings().prettyPrint(true));
        // get plain text with preserved line breaks by disabled prettyPrint
        return Jsoup.clean(prettyPrintedBodyFragment, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }



     Void ctLogin(String username, String password) throws IOException {


         FormBody formBody = new FormBody.Builder()
                .addEncoded("forceDevice","sp")
                .addEncoded("lang","1")
                .addEncoded("userId",username)
                .addEncoded("password",password)
                .addEncoded("login", "login")

                .build();


         Request request = new Request.Builder()
                .url("https://portal2.apu.ac.jp/campusp/splogin.do")
//                .addHeader("Cookie",JsessionID)
//                .header("Host","portal2.apu.ac.jp")
//                .header("Origin","https://portal2.apu.ac.jp")
//                .header("Referer","https://portal2.apu.ac.jp/campusp/sptop.do")
//                .header("Upgrade-Insecure-Requests","1")
//                .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .post(formBody)
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        System.out.println(response.header("Set-Cookie"));
//        JsessionID = response.header("Set-Cookie").split(";")[0];
//        Log.d("Cookie",response.headers().toString());
        return null;
    }
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String username = br.readLine();
        String password = br.readLine();

//        System.out.println(ctSpTop());
//        System.out.println(ctLogin(username,password));
//        System.out.println(ctInformation());
//        ctMessage.getImportantMessageToYou();
        CampusTerminal ct = new CampusTerminal();
        ct.ctSpTop();

        ct.ctLogin(username,password);
//        ct.ctInformation();
        CampusTerminal.ctMessage cm = ct.new ctMessage();
        cm.ctInformation();

        System.out.println(cm.ctGetMessageList(0));


//        ctMessage.ctGetMessageDetail(0);

//        System.out.println(informationMap);
//        ctCourseNotice.ctGetCourseNoticeDetail();
    }
    public void init(){


    }


}
