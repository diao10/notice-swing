package com.notice.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;


/**
 * @author EDY
 */
public class CcbFundUtil {
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

    public static void main(String[] args) {
        StringBuilder contentC = new StringBuilder();


      out:  for (int i = 1; i <= 200; i++) {
            String url = "http://www.ccbfund.cn/xxplxxpl/index_" + i + ".jhtml";

            Document doc = getDocument(url);
            Element cls = doc.getElementsByClass("zixunliebiao").first();
            for (Element child : cls.children()) {
                try {
                    if (ObjectUtil.isNotEmpty(child.getElementsByTag("li"))) {
                        String title = child.getElementsByTag("li").get(0).getElementsByTag("a").get(0).attr("title");
                        String href = child.getElementsByTag("li").get(0).getElementsByTag("a").get(0).attr("href");
                        String dateStr = child.getElementsByTag("li").get(0).getElementsByTag("span").text();
                        DateTime pubDate = DateUtil.parse(dateStr, DatePattern.CHINESE_DATE_PATTERN);
                        int year = pubDate.year();
                        if (pubDate.isAfter(DateUtil.parseDate("2024-06-30"))) {
                            continue;
                        }
                        if (pubDate.isBefore(DateUtil.parseDate("2024-01-01"))) {
                            break out;
                        }
//                        if (!pubD ate.isBefore(DateUtil.parseDate("2023-01-01"))) {
//                        System.out.println("title = " + title);
//                        if ((title.contains("关联方") || title.contains("承销")) && year == 2022) {
                        contentC.append("第").append(i).append("页，标题为：").append(title).append("   ").append(dateStr).append("\r\n");
//                        }
//                        }
//                        if (year <= 2022) {
//                            break out;
//                        }
                        //下载文档
                        Document hrefDoc = getDocument(href);
                        String docHref = hrefDoc.getElementsByClass("wenzhang").first().getElementsByTag("a").first().attr("href");
                        String prefix = "http://www.ccbfund.cn";
                        String suffix = FileUtil.getSuffix(docHref);
                        long size = HttpUtil.downloadFile(prefix + docHref, "C:\\Users\\EDY\\Desktop\\ccbfund_141\\" + title + "_" + DateUtil.format(pubDate, DatePattern.PURE_DATE_FORMAT) + "." + suffix);
                        if (size <= 0) {
                            System.out.println(title);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        FileUtil.writeUtf8String(contentC.toString(), "C:\\Users\\EDY\\Desktop\\ccbfund_141.txt");


        System.out.println("-----------------------------------");
    }
}
