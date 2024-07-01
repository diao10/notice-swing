package org.example.notice.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.Data;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TestHttp2Util {
    public static Document getDocument(String url) {
        Connection conn = Jsoup.connect(url);
        Document document = null;
        try {
            document = conn.get();
        } catch (IOException e) {
            e.printStackTrace();
            // handle error
        }
        return document;
    }

    @Data
    public static class Fund implements Serializable {
        private String code;
        private String name;
        private String size;
    }

    public static void exportSize() throws FileNotFoundException {
        StringBuilder contentC = new StringBuilder();
        List<Fund> list = new ArrayList<>();
        List<Map<String, Object>> all = ExcelUtil.getReader("C:\\Users\\39240\\Desktop\\公募基金份额-231231.xlsx").read(1, 2, 100);

        System.out.println("-----------------------------------");

        for (int i = 0; i < all.size(); i++) {

            String url = "https://fundf10.eastmoney.com/jbgk_";
            Fund fund = new Fund();
            fund.setCode((String) all.get(i).get("产品代码"));
            fund.setName((String) all.get(i).get("产品名称"));

            url += fund.getCode();
            url += ".html";
            try {
                Document doc = getDocument(url);
                Element cls = doc.getElementsByClass("detail").first().getElementsByClass("txt_cont").first().getElementsByClass("info").first();
                boolean flag = false;
                for (Element child : cls.getElementsByTag("tr")) {
                    for (int j = 0; j < child.children().size(); j++) {
                        if ("资产规模".equals(child.child(j).getElementsByTag("th").text())) {
                            fund.setSize(child.child(j + 1).text());
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        break;
                    }
                }
                list.add(fund);
            } catch (Exception e) {
                contentC.append(fund.getName()).append("\r\n");

            }


        }
        System.out.println("-----------------------------------");


        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);
        writer.flush(new FileOutputStream(new File("C:\\Users\\39240\\Desktop\\test.xlsx")));
        writer.close();

        FileUtil.writeUtf8String(contentC.toString(), "C:\\Users\\39240\\Desktop\\err.txt");

    }


    public static void exportPDF() throws InterruptedException {

        StringBuilder contentC = new StringBuilder();
        List<Map<String, Object>> all = ExcelUtil.getReader("C:\\Users\\39240\\Desktop\\公募基金份额-231231.xlsx").read(1, 2, 100);

        System.out.println("-----------------------------------");

        for (int i = 0; i < all.size(); i++) {
            WebClient webClient = new WebClient();
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.waitForBackgroundJavaScript(3000);

            String url = "https://fundf10.eastmoney.com/jjgg_";
            String code = (String) all.get(i).get("产品代码");
            String name = (String) all.get(i).get("产品名称");
            url += code;
            url += "_3.html";
            try {
                HtmlPage page = webClient.getPage(url);
                Document doc = Jsoup.parse(page.asXml(), url);
                Element cls = doc.getElementById("ggtable").getElementsByTag("tbody").get(0);
                for (int j = 0; j < cls.children().size(); j++) {
                    Element child = cls.children().get(j);
                    Elements childCls = child.getElementsByTag("a");
                    String title = childCls.get(0).attr("title");
                    if (title.contains("2023年年度报告")) {
                        String href = childCls.get(1).attr("href");
                        String suffix = FileUtil.getSuffix(href);
                        HttpUtil.downloadFile(href, "C:\\Users\\39240\\Desktop\\test\\" + title + "." + suffix);
                        break;
                    }

                    if (j == cls.children().size() - 1) {
                        contentC.append(name).append("\r\n");

                    }
                }
            } catch (Exception e) {
                contentC.append(name).append("\r\n");

            }


        }
        System.out.println("-----------------------------------");

        FileUtil.writeUtf8String(contentC.toString(), "C:\\Users\\39240\\Desktop\\err.txt");

    }


    public static void main(String[] args) throws IOException {

        try {
            exportPDF();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

}
