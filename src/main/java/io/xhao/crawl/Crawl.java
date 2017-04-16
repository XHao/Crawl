package io.xhao.crawl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class Crawl {

    public static final String seed = "http://ej.wanfangdata.com.cn/Journals/Search?";
    private static ContentStreamFactory factory = new ContentStreamFactory();

    static Workbook wb = new HSSFWorkbook();

    public static void main(String[] args) throws FileNotFoundException, IOException {
        List<String> urls = create();
        List<ContentResult> results = new ArrayList<>();
        urls.forEach(url -> {
            results.addAll(Arrays.asList(factory.read(url)));
        });

        writeToxsl(results);

    }

    private static void writeToxsl(List<ContentResult> results) throws FileNotFoundException, IOException {
        results.forEach(result -> {
            Sheet sheet = wb.createSheet(result.getName());
            AtomicInteger rowNum = new AtomicInteger(0);
            Row row = sheet.createRow(rowNum.get());
            row.createCell(0).setCellValue("期刊");
            row.createCell(1).setCellValue(result.getName());
            row = sheet.createRow(rowNum.incrementAndGet());
            row.createCell(0).setCellValue("期刊");
            row.createCell(1).setCellValue(result.getEnName());
            result.getBasic().forEach((k, v) -> {
                Row row1 = sheet.createRow(rowNum.incrementAndGet());
                row1.createCell(0).setCellValue(k);
                row1.createCell(1).setCellValue(v);
            });

            result.getDetail().forEach((k, v) -> {
                if (!k.startsWith("Notitle")) {
                    Row title = sheet.createRow(rowNum.incrementAndGet());
                    title.createCell(0).setCellValue(k);
                }

                Map<Integer, String> head = new HashMap<>();
                AtomicInteger headCol = new AtomicInteger(0);
                Row row1 = sheet.createRow(rowNum.incrementAndGet());
                if (v.size() == 0)
                    return;

                v.get(0).keySet().forEach(str -> {
                    row1.createCell(headCol.get()).setCellValue(str);
                    head.put(headCol.getAndIncrement(), str);
                });

                for (int i = 0; i < v.size(); i++) {
                    Map<String, String> map = v.get(i);
                    Row row2 = sheet.createRow(rowNum.incrementAndGet());
                    for (int j = 0; j < head.size(); j++) {
                        row2.createCell(j).setCellValue(map.get(head.get(j)));
                    }
                }
            });
        });
        try (FileOutputStream fileOut = new FileOutputStream("search.xls")) {
            wb.write(fileOut);
        }
        System.exit(0);
    }

    private static List<String> create() {
        int start_page = 1;
        int end_page = 25;
        List<String> urls = new ArrayList<>();
        for (int i = start_page; i <= end_page; i++) {
            urls.add(seed + "year=2012&page=" + i);
        }
        return urls;
    }
}
