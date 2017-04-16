package io.xhao.crawl;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ContentStreamFactory {

    private ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    public ContentResult[] read(String url) {
        try {
            Document doc = null;
            while (true) {
                try {
                    doc = Jsoup.connect(url).get();
                    break;
                } catch (SocketTimeoutException ex) {
                    ex.printStackTrace();
                }
            }
            Elements elements = doc.select("table").select("tr");
            CountDownLatch latch = new CountDownLatch(elements.size() - 1);
            LinkedBlockingQueue<UrlResult> resutls = new LinkedBlockingQueue<>();
            for (int i = 1; i < elements.size(); i++) {
                Elements tds = elements.get(i).select("td");
                pool.submit(new UrlReadTask(tds, latch, resutls));
            }

            latch.await();
            LinkedBlockingQueue<ContentResult> ret = new LinkedBlockingQueue<>();
            resutls.stream().parallel().forEach(u -> {
                new ContentReadTask(u.getUrl(), ret).run();
            });
            return ret.toArray(new ContentResult[0]);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return new ContentResult[0];
    }
}
