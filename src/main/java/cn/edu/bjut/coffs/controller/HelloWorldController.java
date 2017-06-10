package cn.edu.bjut.coffs.controller;

import cn.edu.bjut.coffs.annotation.*;
import cn.edu.bjut.coffs.enums.RequestType;
import cn.edu.bjut.coffs.model.Person;
import cn.edu.bjut.coffs.utils.JsonMapper;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenshouqin on 2016-07-08 11:23.
 */
@Controller
@RequestMapping(pathPrefix = "/hello")
public class HelloWorldController {

    private JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();

    @RequestParams(path = "/show", methods = {RequestType.POST, RequestType.GET},
            required = {"name", "password", "age"})
    @Json
    public Map<String, Object> helloWorld(@Model Person p, @Param("name") String name,
                                          @Param("password") String password,
                                          @Param("age") int age) throws Exception {
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("status", 200);
        map.put("name", name);
        map.put("password", password);
        map.put("age", age);
        map.put("p.name", p.getName());
        map.put("p.password", p.getPassword());
        map.put("p.age", p.getAge());
        return map;
    }
}
