package netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 */
public class ClientServerTest {
    @Test
    public void testInteraction() throws InterruptedException {
        EventLoopGroup serverLoop = new NioEventLoopGroup();
        try {
           new ServerNetty().start(8011, serverLoop);
           new ClientNetty().start("127.0.0.1", 8011);
        } finally {
            serverLoop.shutdown();
            serverLoop.awaitTermination(3, TimeUnit.SECONDS);
        }

    }
}
