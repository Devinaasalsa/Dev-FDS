package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import hbm.fraudDetectionSystem.ChannelEngine.Constant.ChannelStatus;

import hbm.fraudDetectionSystem.ChannelEngine.Core.ISO8583.ServiceImpl.BaseChannel;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfigurationService;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.ApplicationContext;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOMsg;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.EOFException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.util.UUID;
import java.util.concurrent.*;

import static hbm.fraudDetectionSystem.ChannelEngine.Constant.ChannelStatus.ACTIVE;
import static hbm.fraudDetectionSystem.ChannelEngine.Constant.ChannelStatus.NOT_ACTIVE;

@Slf4j
public class NMMHandler {

    public NMMHandler() {
    }

    public ChannelStatus sendLogon(BaseChannel ss) {
        ChannelStatus stat;
        ChannelConfigurationService service = ApplicationContext.getBean("channelConfigurationService", ChannelConfigurationService.class);
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("logon-%d").build();
        ExecutorService executorService = Executors.newSingleThreadExecutor(namedThreadFactory);
        Future<?> test = executorService.submit(new NMMSession(ss));
        try {
            test.get(ss.getTimeoutTime(), TimeUnit.MILLISECONDS);
            stat = ACTIVE;
            service.updateStatusByChannelId(ss.getChannelId(), stat.ordinal());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            stat = NOT_ACTIVE;
            service.updateStatusByChannelId(ss.getChannelId(), 0);
        }
        executorService.shutdown();
        executorService.shutdownNow();

        return stat;
    }

    protected class NMMSession implements Runnable {
        BaseChannel channel;
        final Object lock = new Object();

        public NMMSession(BaseChannel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            UUID uniqueID = UUID.randomUUID();
            String uID = uniqueID.toString();
            MDC.put("req_id", uID);
            try {
                ISOMsg m = new ISOMsg();
                m.set(0, "0800");
                m.set(70, "001");
                this.channel.send(m, 2, uID);
                this.channel.receive(1, uID);
            } catch (ISOException e) {
                log.error(
                        String.format(
                                "channel id: %s - session error when processing message, detail: ",
                                channel.getChannelId()
                        ), e
                );
            } catch (EOFException | InterruptedIOException | SocketException ignored) {
            } catch (Throwable var23) {
                log.error(
                        String.format(
                                "channel id: %s - session error: ",
                                channel.getChannelId()
                        ), var23
                );
            }
        }
    }
}
