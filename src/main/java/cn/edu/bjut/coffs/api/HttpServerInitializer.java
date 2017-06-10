package cn.edu.bjut.coffs.api;

import cn.edu.bjut.coffs.processor.RequestProcessor;
import cn.edu.bjut.coffs.utils.SpringContextUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by chenshouqin on 2016-07-07 15:29.
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private RequestProcessor requestProcessor = SpringContextUtil.getBeanByClass(RequestProcessor.class);

    EventExecutorGroup eventExecutorGroup;


    public static int defaultEventExecuterThreadSize = 10;

    public HttpServerInitializer() {
        eventExecutorGroup = new DefaultEventExecutorGroup(defaultEventExecuterThreadSize);

        if(null == requestProcessor) {
            throw new RuntimeException("please init the requestProcessor in spring!");
        }
        requestProcessor.init();
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(10*1024*1024));
        pipeline.addLast(eventExecutorGroup, new HttpServerHandler());
    }
}
