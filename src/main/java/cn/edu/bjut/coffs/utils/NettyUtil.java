package cn.edu.bjut.coffs.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;


/**
 * Created by chenshouqin on 2016-07-07 22:26.
 */
public class NettyUtil {


    public static void writeResponse(HttpRequest httpRequest, StringBuilder content, Channel channel) {
        ByteBuf buf = Unpooled.copiedBuffer(content.toString(), CharsetUtil.UTF_8);
        content.setLength(0);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);

        HttpHeaders headers = response.headers();
        headers.set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");

        channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        channel.close();

    }
}
