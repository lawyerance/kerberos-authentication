package pers.lyks.kerberos.elastic;

import org.elasticsearch.Version;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author lawyerance
 * @version 1.0 2019-12-15
 */
public final class MainInfoVersion {
    public static boolean compatible(MainResponse main) throws IOException {
        Version serverVersion = main.getVersion();
        return serverVersion.major > Version.CURRENT.major;
    }


    public static boolean compatible(InputStream is) throws IOException {
        JsonXContent jsonXContent = JsonXContent.jsonXContent;
        XContentParser parser = jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, is);
        MainResponse response = MainResponse.fromXContent(parser);
        return compatible(response);
    }
}
