package Remote.server.service;

import Remote.server.annotion.RPCService;

/**
 * @author zoujianglin
 * @date 2018/7/31 9:26
 */
@RPCService(serverName = "hello",module = (short)1)
public interface IService {

    public String say();
}
