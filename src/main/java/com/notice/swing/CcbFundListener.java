package com.notice.swing;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.io.IOException;


/**
 * @author EDY
 */
public class CcbFundListener {
    public static Document getDocument(String url) {
        Connection conn = Jsoup.connect(url);
        Document document = null;
        try {
            document = conn.get();
        } catch (IOException e) {
            System.out.println("-----------------------------------------conn:" + e.getMessage());
        }
        return document;
    }

    public static void exportNotice(String startDate, String endDate, String searchWord, String maxPageStr, String exportPath, JProgressBar progressBar) {
        Integer maxPage = null;
        if (NumberUtil.isNumber(maxPageStr)) {
            maxPage = Integer.parseInt(maxPageStr);
        }
        exportNotice(startDate, endDate, searchWord, maxPage, exportPath, progressBar);
    }


    public static void exportNotice(String startDate, String endDate, String searchWord, Integer maxPage, String exportPath, JProgressBar progressBar) {
        exportPath = FileUtil.normalize(exportPath);
        StringBuilder contentC = new StringBuilder();
        if (ObjectUtil.isNull(maxPage)) {
            maxPage = 1;
        }
        out:
        for (int i = 1; i <= maxPage; i++) {
            progressBar.setValue(i * 100 / maxPage);
            progressBar.repaint();
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
                        if (StrUtil.isNotEmpty(endDate) && pubDate.isAfter(DateUtil.parseDate(endDate))) {
                            continue;
                        }
                        if (StrUtil.isNotEmpty(startDate) && pubDate.isBefore(DateUtil.parseDate(startDate))) {
                            break out;
                        }
                        if (StrUtil.isNotEmpty(searchWord) && !title.contains(searchWord)) {
                            continue;
                        }
                        contentC.append("第").append(i).append("页，标题为：").append(title).append("   ").append(dateStr).append("\r\n");

                        //下载文档
                        Document hrefDoc = getDocument(href);
                        String docHref = hrefDoc.getElementsByClass("wenzhang").first().getElementsByTag("a").first().attr("href");
                        String prefix = "http://www.ccbfund.cn";
                        String suffix = FileUtil.getSuffix(docHref);
                        long size = HttpUtil.downloadFile(prefix + docHref, exportPath + "/ccbfund/" + title + "_" + DateUtil.format(pubDate, DatePattern.PURE_DATE_FORMAT) + "." + suffix);
                        if (size <= 0) {
                            System.out.println("-----------------------------------------title:" + title);
                        }
                    }
                    //页数
                    if (maxPage == 1 && ObjectUtil.isNotEmpty(child.getElementsByTag("div"))) {
                        String content = child.getElementsByTag("div").text();
                        content = StrUtil.sub(content, content.indexOf("共") + 1, content.indexOf("条"));
                        maxPage = Integer.parseInt(content);
                    }
                } catch (Exception e) {
                    System.out.println("-----------------------------------------" + e.getMessage());
                }
            }
        }
        FileUtil.writeUtf8String(contentC.toString(), exportPath + "/" + "ccbfund" + ".txt");
    }

}
