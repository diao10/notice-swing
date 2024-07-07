package com.notice.swing;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author diaoyn
 * @create 2024-07-07 23:35:43
 */
public class CsFundsListener {

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
        exportPath = exportPath + "/" + FundEnum.CS_FUNDS.getName() + "/";
        StringBuilder contentC = new StringBuilder();

        if (ObjectUtil.isNull(maxPage)) {
            maxPage = 1;
        }
        for (int i = 0; i <= maxPage; i++) {
            JSONObject reqJson = new JSONObject();
            reqJson.set("CatalogID", 995);
            reqJson.set("InfoOrderBy", " PublishDate desc,orderflag desc ");
            reqJson.set("OtherCatalog", "qxjj_xswj");
            reqJson.set("StartTime", startDate);
            reqJson.set("EndTime", endDate);
            reqJson.set("Query", searchWord);
            reqJson.set("InfoPageIndex", i);

            Map<String, Object> commonMap = new HashMap<>();
            commonMap.put("_ZVING_METHOD", "Information/loadDt");
            commonMap.put("_ZVING_URL", "%2Fqxjj%2Fjjgg%2F");

            commonMap.put("_ZVING_DATA", reqJson.toString());
            commonMap.put("_ZVING_DATA_FORMAT", "json");
            String bodyStr = HttpUtil.post("https://www.csfunds.com.cn/front/ajax/invoke", commonMap);
            JSONObject bodyJson = JSONUtil.parseObj(bodyStr);

            if (bodyJson.getInt("count") <= 0) {
                break;
            }

            JSONArray array = bodyJson.getJSONArray("InfoDt");
            for (JSONObject jsonObject : array.jsonIter()) {
                String dateStr = jsonObject.getStr("PUBLISHDATE");
                Date pubDate = DateUtil.parse(dateStr, "yyyy.MM.dd");
                String dateStr2 = DateUtil.formatDate(pubDate);
                contentC.append("第").append(i + 1).append("页，标题为：").append(jsonObject.get("TITLE")).append("   ").append(dateStr2).append("\r\n");

                //下载文档
                String prefix = "https://www.csfunds.com.cn";
                String link = jsonObject.getStr("Link");
                Document linkDoc = getDocument(prefix + link);
                try {
                    String docHref = linkDoc.getElementsByClass("contents").first().getElementsByTag("a").first().attr("href");
                    if (FileUtil.isAbsolutePath(docHref)) {
                        docHref = prefix + docHref;
                    }

                    String suffix = FileUtil.getSuffix(docHref);
                    HttpUtil.downloadFile(docHref, "C:\\Users\\39240\\Desktop\\csfunds_61\\" + jsonObject.get("TITLE") + "_" + DateUtil.format(pubDate, DatePattern.PURE_DATE_FORMAT) + "." + suffix);

                } catch (Exception e) {
                    e.printStackTrace();
                    contentNoDoc.append("第").append(i + 1).append("页，标题为：").append(jsonObject.get("TITLE")).append("   ").append(dateStr2).append("\r\n");
                }
            }
        }


    }
}
