package cn.wz.netty.test;

import cn.wz.netty.codec.RpcRequest;
import cn.wz.netty.codec.RpcResponse;
import cn.wz.netty.serialize.HessianSerializer;
import cn.wz.utils.HttpRequestUtil;
import org.junit.Test;

/**
 * @author wangzhen
 * @date 2017/3/16 9:26
 * @desc
 */
public class HttpserverTest {


    /**
     * post 提交Rpc请求到服务器端
     */
    @Test
    public void postRpcRequestToServer(){
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setClassName("ttt.ccc");
        rpcRequest.setMethodName("helloworld");
        rpcRequest.setServerAddress("127.0.0.1");
        byte[] serialize = HessianSerializer.serialize(rpcRequest);
        byte[] bytes = HttpRequestUtil.postRequest("http://localhost:8080", serialize);
        RpcResponse deserialize = (RpcResponse) HessianSerializer.deserialize(bytes, RpcResponse.class);
        deserialize.getResult();
    }
}
