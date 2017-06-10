package cn.edu.bjut.coffs.api;

import cn.edu.bjut.coffs.enums.RequestType;
import cn.edu.bjut.coffs.processor.RequestProcessor;
import cn.edu.bjut.coffs.utils.LOGGER;
import cn.edu.bjut.coffs.utils.NettyUtil;
import cn.edu.bjut.coffs.utils.SpringContextUtil;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chenshouqin on 2016-07-07 21:39.
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject>{


    private RequestProcessor requestProcessor = SpringContextUtil.getBeanByClass(RequestProcessor.class);
    private HttpRequest httpRequest;
    private final StringBuilder responseContent = new StringBuilder();
    private final StringBuilder path = new StringBuilder();
    private static final HttpDataFactory FACTORY = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
    private HttpPostRequestDecoder postRequestDecoder;


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(null != postRequestDecoder) {
            postRequestDecoder.cleanFiles();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.errorLog(HttpServerHandler.class, "exceptionCaught", cause);
        ctx.channel().close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        messageReceived(channelHandlerContext, httpObject);
    }

    private void messageReceived(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) {
        try {
            InetSocketAddress socketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
            InetAddress inetAddress = socketAddress.getAddress();
            String ipAddress = inetAddress.getHostAddress().toString();
            Map<String, RequestValue> mapParams = Maps.newHashMap();
            mapParams.put("request_ip", new RequestValue(RequestValue.RequestParamsType.STRING, ipAddress));

            if(httpObject instanceof HttpRequest) {
                HttpRequest request = this.httpRequest = (HttpRequest) httpObject;
                URI uri = new URI(request.uri());
                path.setLength(0);
                path.append(uri.getPath());
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
                Map<String, List<String>> uriAttributes = queryStringDecoder.parameters();

                Set<String> keys = uriAttributes.keySet();
                for(String key : keys) {
                    Object[] attrValue = uriAttributes.get(key).toArray();

                    RequestValue requestValue = null;
                    if(1 == attrValue.length) {
                        requestValue = new RequestValue(RequestValue.RequestParamsType.STRING, attrValue[0].toString());
                    } else if(1 < attrValue.length) {
                        String[] strArrVal = new String[attrValue.length];
                        System.arraycopy(attrValue, 0, strArrVal, 0, attrValue.length);
                        requestValue = new RequestValue(RequestValue.RequestParamsType.STRING_ARRAY, strArrVal);
                    }
                    mapParams.put(key, requestValue);
                }

                if(request.method().equals(HttpMethod.GET)) {
                    String result = requestProcessor.processHttpRequest(path.toString(), mapParams, RequestType.GET);
                    outputResponse(channelHandlerContext, result);
                }

                if(request.method().equals(HttpMethod.POST)) {
                    String result = requestProcessor.processHttpRequest(path.toString(), mapParams, RequestType.POST);
                    outputResponse(channelHandlerContext, result);
                }
            }
        } catch (Exception e) {
            LOGGER.errorLog(HttpServerHandler.class, "messageReceived", e);
        }
    }

    private void outputResponse(ChannelHandlerContext ctx, String content) {
        responseContent.setLength(0);
        responseContent.append(content);
        NettyUtil.writeResponse(httpRequest, responseContent, ctx.channel());
    }
}
