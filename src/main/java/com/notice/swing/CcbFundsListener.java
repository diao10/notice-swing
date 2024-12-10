package com.notice.swing;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.notice.enums.FundEnum;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;
import java.io.IOException;


/**
 * @author EDY
 */
public class CcbFundsListener {
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
        exportPath = exportPath + "/" + FundEnum.CCB_FUNDS.getName() + "/";
        StringBuilder result = new StringBuilder();
        StringBuilder resultError = new StringBuilder();
        boolean maxPageNullFlag = false;
        if (ObjectUtil.isNull(maxPage) || maxPage <= 0) {
            maxPage = 1;
            maxPageNullFlag = true;
        }
        out:
        for (int i = 1; i <= maxPage; i++) {
            String url = "http://www.ccbfund.cn/website/v1/api/article/list?categoryId=830&page=" + i + "&keyword=";
            if (StrUtil.isNotEmpty(searchWord)) {
                url = url + searchWord;
            }
            String response = HttpUtil.get(url);
            JSONObject responseJson = JSONUtil.parseObj(response);
            if (ObjectUtil.isNull(responseJson.containsKey("data"))) {
                break;
            }
            JSONObject data = responseJson.getJSONObject("data");
            JSONArray content = data.getJSONArray("content");
            Integer totalPages = data.getInt("totalPages");
            if (maxPageNullFlag) {
                maxPage = totalPages;
            }

            for (JSONObject json : content.jsonIter()) {

                String title = json.getStr("title");
                String dateStr = json.getStr("publishDate");
                String categoryId = json.getStr("categoryId");
                String contentId = json.getStr("contentId");
                DateTime pubDate = DateUtil.parseDate(dateStr);
                if (StrUtil.isNotEmpty(endDate) && pubDate.isAfter(DateUtil.parseDate(endDate))) {
                    continue;
                }
                if (StrUtil.isNotEmpty(startDate) && pubDate.isBefore(DateUtil.parseDate(startDate))) {
                    break out;
                }
                result.append("第").append(i).append("页，标题为：").append(title).append("   ").append(dateStr).append("\r\n");
                String href = "http://www.ccbfund.cn/resource/static/content/" + contentId + ".html";
                try {
                    String urlRep = HttpUtil.get(href);
                    String docHref = Jsoup.parse(urlRep).getElementsByTag("a").first().attr("href");
                    String prefix = "http://www.ccbfund.cn";
                    if (FileUtil.isAbsolutePath(docHref)) {
                        docHref = prefix + docHref;
                    }
                    String suffix = FileUtil.getSuffix(docHref);
                    long size = HttpUtil.downloadFile(docHref, exportPath + "doc/" + title + "_" + dateStr + "." + suffix);
                    if (size <= 0) {
                        System.out.println("-----------------------------------------title:" + title);
                        resultError.append("第").append(i).append("页，标题为：").append(title).append(",下载文件失败").append("\r\n");
                    }
                } catch (Exception e) {
                    resultError.append("第").append(i).append("页，标题为：").append(title).append(",").append(e.getMessage()).append("\r\n");
                    System.out.println("-----------------------------------------" + e.getMessage());
                }
            }
            progressBar.setValue(i * 100 / maxPage);
            progressBar.repaint();
        }
        FileUtil.writeUtf8String(result.toString(), exportPath + "fundList.txt");
        if (StrUtil.isNotEmpty(resultError)) {
            FileUtil.writeUtf8String(resultError.toString(), exportPath + "fundListError.txt");
        }
    }

}
