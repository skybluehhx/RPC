package Remote.endoce;

import Remote.RPCInfo.Request;
import Remote.RPCInfo.Response;
import Remote.model.RemotingTransporter;
import Remote.seriable.SerializerFactory;

/**
 * @author zoujianglin
 * @date 2018/8/2 10:57
 */

/**
 * 按照不同的方式解析RemotingTransport中真正携带的数据对象（也就是enity属性）
 */
public class AnalysisRemotingTransport {

    public static RemotingTransporter doAnalysisRemoting(RemotingTransporter transporter) {
        switch (transporter.getTransporterType()){
            case LINProtocol.REQUEST_REMOTING:
                Request request = SerializerFactory.getiSerializer().deserialize(transporter.getBytes(), Request.class);
                transporter.setEnity(request);
                break;
            case LINProtocol.RESPONSE_REMOTING:
                Response response = SerializerFactory.getiSerializer().deserialize(transporter.getBytes(), Response.class);
                transporter.setEnity(response);
                break;
        }
        return  transporter;

        /*if (LINProtocol.REQUEST_REMOTING == transporter.getTransporterType()) {
            Request request = SerializerFactory.getiSerializer().deserialize(transporter.getBytes(), Request.class);
            transporter.setEnity(request);
        } else if (LINProtocol.RESPONSE_REMOTING == transporter.getTransporterType()) {

            Response response = SerializerFactory.getiSerializer().deserialize(transporter.getBytes(), Response.class);
            transporter.setEnity(response);

        }
        return transporter;
        */
    }

}
