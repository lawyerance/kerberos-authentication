package pers.lyks.kerberos.elastic.compatible;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author lawyerance
 * @version 1.0 2019-12-15
 */
public class CreateIndexAllocatorTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testNonIncludeType() throws IOException {
        String json = "{\n" +
            "    \"mappings\": {\n" +
            "        \"properties\": {\n" +
            "            \"title\": {\n" +
            "                \"type\": \"keyword\"\n" +
            "            },\n" +
            "            \"age\": {\n" +
            "                \"type\": \"integer\"\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";
        JsonNode root = mapper.readTree(json);
        JsonNode node = root.at("/mappings/properties");
        Assert.assertTrue(node instanceof ObjectNode);
    }

    @Test
    public void testIncludeType() throws IOException {
        String json = "{\n" +
            "    \"mappings\": {\n" +
            "        \"index_include_type\": {\n" +
            "            \"properties\": {\n" +
            "                \"title\": {\n" +
            "                    \"type\": \"keyword\"\n" +
            "                },\n" +
            "                \"age\": {\n" +
            "                    \"type\": \"integer\"\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";
        JsonNode root = mapper.readTree(json);
        JsonNode node = root.at("/mappings/properties");
        Assert.assertTrue(node instanceof MissingNode);
    }
}
