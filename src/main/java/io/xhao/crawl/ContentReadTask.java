package io.xhao.crawl;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ContentReadTask implements Runnable {

    private final String url;

    private final BlockingQueue<ContentResult> results;

    @Override
    public void run() {
        try {
            ContentResult ret = new ContentResult();
            Document doc = null;
            while (true) {
                try {
                    doc = Jsoup.connect("http://ej.wanfangdata.com.cn/" + url).get();
                    break;
                } catch (SocketTimeoutException ex) {
                    ex.printStackTrace();
                }
            }

            Elements elements = doc.getElementsByClass("container").select("table").get(0).select("tbody").select("tr").select("td");
            Elements nameElements = elements.get(1).select("div");
            ret.setName(nameElements.get(1).text());
            ret.setEnName(nameElements.get(2).text());
            for (int i = 2; i < 12; i++) {
                String txt = elements.get(i).text();
                ret.getBasic().put(txt.substring(0, txt.indexOf(":")), txt.substring(txt.indexOf(":") + 1));
            }

            elements = doc.getElementsByClass("table");

            for (int i = 0; i < 5; i++) {
                if (i == 2)
                    continue;
                Element ele = elements.get(i);
                String title = "Notitle" + i;
                Elements detailHead;
                if (ele.getElementsByClass("tbtitle").size() == 1) {
                    title = ele.getElementsByClass("tbtitle").get(0).text();
                    detailHead = ele.select("thead").select("tr").get(1).select("td");
                } else {
                    detailHead = ele.select("thead").select("tr").get(0).select("td");
                }

                List<String> head = new ArrayList<>();
                for (int k = 0; k < detailHead.size(); k++) {
                    head.add(detailHead.get(k).text());
                }

                List<Map<String, String>> detail = new ArrayList<>();
                ret.getDetail().put(title, detail);
                Elements rowDetail = ele.select("tbody").select("tr").select("tr");
                int rows = rowDetail.size();
                for (int j = 0; j < rows; j++) {
                    Map<String, String> map = new HashMap<>();
                    for (int k = 0; k < head.size(); k++) {
                        if (rowDetail.get(j).select("td").size() > k)
                            map.put(head.get(k), rowDetail.get(j).select("td").get(k).text());
                    }
                    detail.add(map);
                }
            }
            results.add(ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
