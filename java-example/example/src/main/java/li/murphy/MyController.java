package li.murphy;

import li.murphy.engine.OcrEngine;
import li.murphy.pojo.OcrResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

@Controller
@RequestMapping("/")
@Slf4j
public class MyController {

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2 + 1, Runtime.getRuntime().availableProcessors(), 3600, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
    OcrEngine ocrEngine = OcrEngine.getInstance();

    @RequestMapping(value = "/OcrLite", produces = "application/json; charset=utf-8")
    @ResponseBody
    public OcrResult detect(@RequestParam MultipartFile file) {
        byte[] bytes = null;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            log.error("读取图片失败");
            throw new RuntimeException();
        }
        final byte [] bytes1 = bytes;
        OcrResult ocrResult = null;
        // 应该改成不可重入锁
//        log.debug("ocr暂存文件写入路径:" + filePath);
//        Future<OcrResult> submit = threadPoolExecutor.submit(() -> ocrEngine.detect(filePath, 0));
        Future<OcrResult> submit = threadPoolExecutor.submit(() -> ocrEngine.detect(bytes1));
        try {
            ocrResult = submit.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("执行ocr出错，错误原因为：", e);
        }
        return ocrResult;
    }
}
