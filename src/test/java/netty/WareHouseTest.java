package netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 */
public class WareHouseTest {
    @Test
    public void testInteraction() throws InterruptedException {
        EventLoopGroup serverLoop = new NioEventLoopGroup();
        try {
           new WareHouseServerNetty().start(8011,serverLoop);
           new WareHouseClientNetty().start("127.0.0.1", 8011);
        } finally {
            serverLoop.shutdown();
            serverLoop.awaitTermination(3, TimeUnit.SECONDS);
        }

    }
}
