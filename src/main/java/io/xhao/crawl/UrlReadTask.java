package io.xhao.crawl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.jsoup.select.Elements;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UrlReadTask implements Runnable {
    private final Elements tds;
    private final CountDownLatch latch;
    private final LinkedBlockingQueue<UrlResult> resutls;

    @Override
    public void run() {
        resutls.add(new UrlResult(tds.get(1).select("td").select("a").attr("href")));
        latch.countDown();
    }
}
