package pers.lyks.kerberos.elastic;

import org.elasticsearch.Version;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author lawyerance
 * @version 1.0 2019-12-15
 */
public final class MainInfoVersion {
    public static final Logger logger = LoggerFactory.getLogger(MainInfoVersion.class);

    public static boolean compatible(MainResponse main) throws IOException {
        Version serverVersion = main.getVersion();
        logger.info("Elasticsearch use client version {} connect sever version {}. ", Version.CURRENT, serverVersion);
        return serverVersion.major > Version.CURRENT.major;
    }


    public static boolean compatible(InputStream is) throws IOException {
        JsonXContent jsonXContent = JsonXContent.jsonXContent;
        XContentParser parser = jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, is);
        MainResponse response = MainResponse.fromXContent(parser);
        return compatible(response);
    }
}
