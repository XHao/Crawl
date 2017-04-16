package io.xhao.crawl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ContentResult {
    private String name;
    private String enName;
    private Map<String, String> basic = new HashMap<>();
    private Map<String, List<Map<String, String>>> detail = new HashMap<>();

}
