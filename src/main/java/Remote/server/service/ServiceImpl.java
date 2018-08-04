package Remote.server.service;

import org.springframework.stereotype.Component;

/**
 * @author zoujianglin
 * @date 2018/7/31 9:27
 */
@Component("serverImpl")
public class ServiceImpl implements IService {

    public ServiceImpl() {
        super();
        System.out.println("===");

    }

    public String say() {
        System.out.println("hello");
        return "hello";
    }
}
