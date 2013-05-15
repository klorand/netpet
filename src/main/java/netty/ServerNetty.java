package netty;

import java.util.Random;

import message.CalculationRequest;
import message.CalculationResult;
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
public class ServerNetty {

	public static final int MAXSTOCK = 10;
	private static final Logger LOG = LoggerFactory.getLogger(ServerNetty.class);


	public ChannelFuture start(int port, EventLoopGroup eventLoopGroup) throws InterruptedException {
		ServerBootstrap b = new ServerBootstrap();
		b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new ObjectEncoder(),
                                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                new WarehouseHandler(),
                                new CalculatorHandler()
                        );
                    }
                });

		return b.bind("127.0.0.1", port).sync();
	}


	public static class WarehouseHandler extends ChannelInboundMessageHandlerAdapter<WareHouseMessage> {


		private final Random random = new Random();


		@Override
		public void messageReceived(ChannelHandlerContext ctx, WareHouseMessage msg) throws Exception {
			LOG.info("Uzenet megjott a klienstol: " + msg);

			final int stock = random.nextInt(MAXSTOCK);
			LOG.info("raktar keszlet kuldese: " + stock);
			ctx.write(new DetailedWareHouseMessage(msg.getItemId(), "alma", "leiras"));
		}


	}

    public static class CalculatorHandler extends ChannelInboundMessageHandlerAdapter<CalculationRequest>{
        private final Random random = new Random();

        @Override
        public void messageReceived(ChannelHandlerContext ctx, CalculationRequest msg) throws Exception {
            LOG.info("Uzenet megjott a klienstol: " + msg);

            //TODO evaluate
            final Number result = random.nextInt(MAXSTOCK);
            LOG.info("szamitas kuldese: " + result);
            ctx.write(new CalculationResult(msg,result));
        }
    }
}
