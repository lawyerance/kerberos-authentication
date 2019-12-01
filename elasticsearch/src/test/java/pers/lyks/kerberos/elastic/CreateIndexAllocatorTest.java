package pers.lyks.kerberos.elastic;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import net.minidev.json.JSONArray;
import org.junit.Test;

import java.util.Map;

/**
 * @author lawyerance
 * @version 1.0 2019-11-29
 */
public class CreateIndexAllocatorTest {

    @Test
    public void compatible() {
        String json = "{\n" +
                "\t\"settings\": {},\n" +
                "\t\"mappings\": {\n" +
                "\t\t\"doc\": {\n" +
                "\t\t\t\"dynamic\": false,\n" +
                "\t\t\t\"properties\": {\n" +
                "\t\t\t\t\"name\": {\n" +
                "\t\t\t\t\t\"type\": \"keyword\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"title\": {\n" +
                "\t\t\t\t\t\"index\": true,\n" +
                "\t\t\t\t\t\"type\": \"text\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"age\": {\n" +
                "\t\t\t\t\t\"type\": \"integer\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"aliases\": {}\n" +
                "}";
        Configuration.ConfigurationBuilder builder = Configuration.builder();
        builder.options(Option.ALWAYS_RETURN_LIST);

        DocumentContext parse = JsonPath.parse(json, builder.build());
        JSONArray array = parse.read("$.mappings.*");
        parse.delete("$.mappings.*");
        System.out.println(parse.jsonString());
        for (Object item : array) {
            Map<String, Object> map = (Map<String, Object>) item;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                parse = parse.put("$.mappings", entry.getKey(), entry.getValue());
                System.out.println(parse.jsonString());

            }
        }

//        parse.add("$.mappings", array);
        System.out.println(parse.jsonString());
    }
}