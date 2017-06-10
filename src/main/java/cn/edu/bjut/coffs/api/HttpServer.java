package cn.edu.bjut.coffs.api;

import cn.edu.bjut.coffs.processor.RequestProcessor;
import cn.edu.bjut.coffs.utils.LOGGER;
import cn.edu.bjut.coffs.utils.SpringContextUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by chenshouqin on 2016-07-07 14:57.
 */

public class HttpServer {

    private RequestProcessor requestProcessor = SpringContextUtil.getBeanByClass(RequestProcessor.class);

    public void run() {

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)
                    .option(ChannelOption.SO_KEEPALIVE, false);

            serverBootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer());

            Channel ch = serverBootstrap.bind(requestProcessor.getPort()).sync().channel();

            System.out.println("server has started at port : " + requestProcessor.getPort());
            ch.closeFuture().sync();

        } catch (InterruptedException e) {
            LOGGER.errorLog(this.getClass(), "run", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
