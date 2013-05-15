package netty;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import message.DetailedWareHouseMessage;
import message.WareHouseMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 *
 */
public class WareHouseClientNetty {

	public static final int MAXITEMS = 20;
	public static final int MAXMESSAGE = 2;
	private static final Logger LOG = LoggerFactory.getLogger(WareHouseClientNetty.class);


	public void start(String host, int port) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ObjectEncoder(), new ObjectDecoder(ClassResolvers.cacheDisabled(null)), new Handler());
				}
			});


			b.connect(host, port).sync().channel().closeFuture().sync();
		}
		finally {
			group.shutdown();
		}
	}


	public static class Handler extends ChannelInboundMessageHandlerAdapter<DetailedWareHouseMessage> {


		private final Random random = new Random();
		private AtomicInteger remainingMessages = new AtomicInteger(MAXMESSAGE);


		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			final int itemId = random.nextInt(MAXITEMS);
			LOG.info("kapcsolodva - kezdeti cikkszam kuldese: " + itemId);
			ctx.write(new WareHouseMessage(itemId));
		}


		@Override
		public void messageReceived(ChannelHandlerContext ctx, DetailedWareHouseMessage msg) throws Exception {
			LOG.info("Uzenet megjott: " + msg);
			if (remainingMessages.decrementAndGet() > 0) {
				final int itemId = random.nextInt(MAXITEMS);
				LOG.info("cikkszam kuldese: " + itemId);
				ctx.write(new WareHouseMessage(itemId));
			} else {
                ctx.close().sync();
            }

		}
	}
}
