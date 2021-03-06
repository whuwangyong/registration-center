package cn.whu.rest.controller;

import cn.whu.rest.ClientTest;
import cn.whu.rest.DeferredResultHalder;
import cn.whu.rest.MockQueue;
import cn.whu.rest.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * @Author WangYong
 * @Date 2019/11/08
 * @Time 18:33
 */
@RestController
public class TestControl {
    @Autowired
    private MockQueue moQueue;
    @Autowired
    private DeferredResultHalder deferredResultHalder;
    @Autowired
    ClientTest clientTest;
    @Value("${lang:zh}")
    String lang;

    private static final Logger logger = LoggerFactory.getLogger(TestControl.class);

    // 同步处理
    @GetMapping("/sync")
    public String syncSuccess() {
        logger.info("主线程开始");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("主线程结束");
        if (lang.equals("en"))
            return "success";
        if (lang.equals("zh"))
            return "成功";
        else return "0";
    }

    @GetMapping("/runnable")
    public Callable<String> asyncSuccessRunnable() {
        logger.info("主线程开始");
        Callable<String> result = new Callable<String>() {
            public String call() throws Exception {
                logger.info("副线程开始");
                Thread.sleep(10000);
                logger.info("副线程结束");
                return "runnable";
            }
        };
        logger.info("主线程结束");
        return result;
    }

    @GetMapping("/deferredResult")
    public DeferredResult<Person> asyncSuccessDeferredResult() {
        logger.info("主线程开始");
        String orderNumber = String.valueOf(new Random().nextInt());
        moQueue.setPlaceOrder(orderNumber);
        DeferredResult<Person> result = new DeferredResult<>();
        deferredResultHalder.getMap().put(orderNumber, result);
        result.onCompletion(() -> {
            logger.info("complete!");
        });
        result.setResultHandler(new DeferredResult.DeferredResultHandler() {
            @Override
            public void handleResult(Object result) {
                logger.info("result:" + result.toString());
            }
        });
        logger.info("主线程结束");
        return result;
    }

    @PostMapping("/deferredResult")
    public DeferredResult<Person> asyncSuccessDeferredResult2(@RequestBody Person person) {
        logger.info("主线程开始");
//        String orderNumber = String.valueOf(new Random().nextInt());
        String orderNumber = person.getName();
        moQueue.setPlaceOrder(orderNumber);
        DeferredResult<Person> result = new DeferredResult<>();
        deferredResultHalder.getMap().put(orderNumber, result);
        result.onCompletion(() -> {
            logger.info("complete!");
        });
        result.setResultHandler(result1 -> logger.info("result:" + result1.toString()));
        logger.info("主线程结束");
        return result;
    }

    @GetMapping("/rest")
    public String resttemplate() {
        return clientTest.run();
    }

    @GetMapping("/haha")
    public void haha() {
        clientTest.sendPost();
    }

    /**
     * 请求 http://127.0.0.1:8080/test/wang，返回hello, wang
     * @param name
     * @return
     */
    @GetMapping("/test/{name}")
    public String test(@PathVariable("name") String name) {
        return "hello, " + name;
    }

    /**
     * 请求http://127.0.0.1:8080/test?name=wang,返回hello, wang
     * @param name
     * @return
     */
    @GetMapping("/test")
    public String test2(@RequestParam String name) {
        return "hello, " + name;
    }
}