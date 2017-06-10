package cn.edu.bjut.coffs.launch;

import cn.edu.bjut.coffs.api.HttpServer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by chenshouqin on 2016-07-08 11:31.
 */
public class Start {

    public static void main(String[] args) {

        new ClassPathXmlApplicationContext("applicationContext.xml");

        new HttpServer().run();
    }
}
