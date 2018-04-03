package crawl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class QichachaJsonPipeLine extends FilePersistentBase implements Pipeline  {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public QichachaJsonPipeLine() {
        setPath("/data/webmagic");
    }

    public QichachaJsonPipeLine(String path) {
        setPath(path);
    }

    public void process(ResultItems resultItems, Task task) {
        String path = this.path + PATH_SEPERATOR + task.getUUID() + PATH_SEPERATOR;
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(getFile(path + DigestUtils.md5Hex(resultItems.getRequest().getUrl()) + ".json")));
            printWriter.write(JSON.toJSONString(resultItems.getAll(), SerializerFeature.WriteNullStringAsEmpty));
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }
}
