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

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;


/**
 * @author EDY
 */
public class HsFundsListener {

    public static void exportNotice(String startDate, String endDate, String maxPageStr, String exportPath,
                                    JProgressBar progressBar) {
        Integer maxPage = null;
        if (NumberUtil.isNumber(maxPageStr)) {
            maxPage = Integer.parseInt(maxPageStr);
        }
        exportNotice(startDate, endDate, maxPage, exportPath, progressBar);
    }


    public static void exportNotice(String startDate, String endDate, Integer maxPage, String exportPath,
                                    JProgressBar progressBar) {
        exportPath = FileUtil.normalize(exportPath);
        exportPath = exportPath + "/" + FundEnum.HS_FUNDS.getName() + "/";
        StringBuilder result = new StringBuilder();
        StringBuilder resultError = new StringBuilder();
        boolean maxPageNullFlag = false;
        if (ObjectUtil.isNull(maxPage) || maxPage <= 0) {
            maxPage = 1;
            maxPageNullFlag = true;
        }
        out:
        for (int i = 1; i <= maxPage; i++) {
            String url = "http://www.hsfund.com/servlet/json";
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("page", i);
            paramMap.put("pageRow", 12);
            paramMap.put("funcNo", 905022);
            String response = HttpUtil.post(url, paramMap);
            JSONObject responseJson = JSONUtil.parseObj(response);
            if (ObjectUtil.isNull(responseJson.containsKey("results"))) {
                break;
            }
            JSONArray results = responseJson.getJSONArray("results");
            JSONObject results0 = results.getJSONObject(0);

            JSONArray data = results0.getJSONArray("data");
            for (int j = 0; j < data.size(); j++) {
                JSONObject json = data.getJSONObject(j);
                String publishDate = json.getStr("publish_date");
                String announcementName = json.getStr("announcement_name");
                String linkUrl = json.getStr("link_url");
                DateTime pubDate = DateUtil.parseDate(publishDate);
                if (StrUtil.isNotEmpty(endDate) && pubDate.isAfter(DateUtil.parseDate(endDate))) {
                    continue;
                }
                if (StrUtil.isNotEmpty(startDate) && pubDate.isBefore(DateUtil.parseDate(startDate))) {
                    break out;
                }
                result.append("第").append(i).append("页，标题为：").append(announcementName).append("   ").append(publishDate).append("\r" +
                        "\n");
                try {
                    String prefix = "http://www.hsfund.com/";
                    String docHref = prefix + linkUrl;
                    String suffix = FileUtil.getSuffix(docHref);
                    long size = HttpUtil.downloadFile(docHref,
                            exportPath + "doc/" + publishDate + "_" + announcementName + "." + suffix);
                    if (size <= 0) {
                        System.out.println("-----------------------------------------title:" + announcementName);
                        resultError.append("第").append(i).append("页，标题为：").append(announcementName).append(",下载文件失败").append("\r" +
                                "\n");
                    }
                } catch (Exception e) {
                    resultError.append("第").append(i).append("页，标题为：").append(announcementName).append(",").append(e.getMessage()).append("\r\n");
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
