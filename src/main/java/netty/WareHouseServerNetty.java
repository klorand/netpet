package netty;

import java.util.Random;

import message.DetailedWareHouseMessage;
import message.WareHouseMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 *
 */
public class WareHouseServerNetty {

	public static final int MAXSTOCK = 10;
	private static final Logger LOG = LoggerFactory.getLogger(WareHouseServerNetty.class);


	public ChannelFuture start(int port, EventLoopGroup eventLoopGroup) throws InterruptedException {
		ServerBootstrap b = new ServerBootstrap();
		b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ObjectEncoder(), new ObjectDecoder(ClassResolvers.cacheDisabled(null)), new Handler());
            }
        });

		return b.bind("127.0.0.1", port).sync();
	}


	public static class Handler extends ChannelInboundMessageHandlerAdapter<WareHouseMessage> {


		private final Random random = new Random();


		@Override
		public void messageReceived(ChannelHandlerContext ctx, WareHouseMessage msg) throws Exception {
			LOG.info("Uzenet megjott a klienstol: " + msg);

			final int stock = random.nextInt(MAXSTOCK);
			LOG.info("raktar keszlet kuldese: " + stock);
			ctx.write(new DetailedWareHouseMessage(msg.getItemId(), "alma", "leiras"));
		}


	}
}
