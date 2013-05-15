package netty;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import message.CalculationRequest;
import message.CalculationResult;
import message.DetailedWareHouseMessage;
import message.WareHouseMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 *
 */
public class ClientNetty {

	public static final int MAXITEMS = 20;
	public static final int MAXMESSAGE = 2;
	private static final Logger LOG = LoggerFactory.getLogger(ClientNetty.class);


	private ChannelFuture makeBootstrap(String host, int port, EventLoopGroup group, final ChannelHandler handler) throws InterruptedException {
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ObjectEncoder(), new ObjectDecoder(ClassResolvers.cacheDisabled(null)), handler);
			}
		});
		return b.connect(host, port).sync().channel().closeFuture();
	}


	public void start(String host, int port) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			ChannelFuture closeWarehouse = makeBootstrap(host, port, group, new WareHouseHandler());
			ChannelFuture closeCalculator = makeBootstrap(host, port, group, new CalculatorHandler());
            closeWarehouse.sync();
            closeCalculator.sync();
		}
		finally {
			group.shutdown();
		}
	}


	public static class WareHouseHandler extends ChannelInboundMessageHandlerAdapter<DetailedWareHouseMessage> {


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
			}
			else {
				ctx.close().sync();
			}

		}
	}


	public static class CalculatorHandler extends ChannelInboundMessageHandlerAdapter<CalculationResult> {


		private Deque<String> calculations = new LinkedList<String>(Arrays.asList("3+4", "4/2", "6-4"));


		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			LOG.info("kapcsolodva - kezdeti szamitas kuldese: " + calculations.peek());
			ctx.write(new CalculationRequest(calculations.poll()));
		}


		@Override
		public void messageReceived(ChannelHandlerContext ctx, CalculationResult msg) throws Exception {
			LOG.info("Uzenet megjott: " + msg);
			if (calculations.size() > 0) {

				LOG.info("szamitas kuldese: " + calculations.peek());
				ctx.write(new CalculationRequest(calculations.poll()));
			}
			else {
				ctx.close().sync();
			}

		}
	}
}
