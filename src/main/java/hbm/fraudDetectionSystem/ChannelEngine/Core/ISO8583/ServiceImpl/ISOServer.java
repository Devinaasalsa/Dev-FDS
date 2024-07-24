package hbm.fraudDetectionSystem.ChannelEngine.Core.ISO8583.ServiceImpl;

import hbm.fraudDetectionSystem.ChannelEngine.Constant.ChannelStatus;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.NMMHandler;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.RequestListener;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOMsg;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.transaction.TransactionRequiredException;
import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

import static hbm.fraudDetectionSystem.ChannelEngine.Constant.ChannelStatus.NOT_ACTIVE;

@Slf4j
public class ISOServer implements Runnable {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected BaseChannel channel;
    protected boolean shutdown;
    protected ServerSocket serverSocket;

    public ISOServer(BaseChannel channel) {
        this.shutdown = false;
        this.channel = channel;
    }

    public void run() {
        while (!this.shutdown) {
            this.serverSocket = channel.serverSocket;
            LOGGER.info(
                    String.format(
                            "Server listening on port: %s",
                            this.serverSocket.getLocalPort()
                    )
            );

            try {
                int i = 0;
                while (!this.shutdown) {
                    BaseChannel sc = (BaseChannel) this.channel.clone();
                    try {
                        sc.accept();
                        new Thread(this.createSession(sc), String.format("client-listen-%s-%s", sc.serverSocket.getLocalPort(), i)).start();
                        i++;
                    } catch (SocketException e) {
                        //Run when the stream still access but the socket already closed
                        if (!this.shutdown) {
                            LOGGER.error(
                                    String.format(
                                            "iso-server-%s error, reason: ",
                                            sc.serverSocket.getLocalPort()
                                    ), e
                            );
                            this.relax();
                            break;
                        }
                    } catch (IOException e) {
                        LOGGER.error(
                                String.format(
                                        "iso-server-%s error, reason: ",
                                        sc.serverSocket.getLocalPort()
                                ), e
                        );
                        this.relax();
                    }
                }
            } catch (Exception e) {
                LOGGER.error(
                        String.format(
                                "iso-server-%s error, reason: ",
                                this.serverSocket.getLocalPort()
                        ), e
                );
                this.relax();
            }
        }
    }

    protected void relax() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException ignored) {
        }
    }

    protected Session createSession(BaseChannel channel) {
        return new Session(channel);
    }

    protected class Session implements Runnable {
        BaseChannel channel;

        public Session(BaseChannel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            UUID uniqueID = UUID.randomUUID();
            String uID = uniqueID.toString();
            MDC.put("req_id", uID);

            long channelId = 0;
            BaseChannel bc = channel;
            if (this.channel != null) {
                Socket socket = bc.getSocket();
                NMMHandler nmm = new NMMHandler();

                LOGGER.info(
                        String.format(
                                "session start channel id:%s - %s:%s",
                                bc.getChannelId(), socket.getInetAddress().getHostAddress(), socket.getPort()
                        )
                );

                ChannelStatus stat = NOT_ACTIVE;
                try {
                    this.channel.setSoTimeout(this.channel.timeoutTime);
                    while (stat != ChannelStatus.ACTIVE && bc.isRequiredSendLogon()) {
                        stat = nmm.sendLogon(bc);
                    }
                    this.channel.setSoTimeout(0);
                } catch (SocketException e) {
                    throw new RuntimeException(e);
                }
                channelId = bc.getChannelId();
            }

            if (bc != null) {
                try {
                    while (true) {
                        while (true) {
                            try {
                                ISOMsg m = this.channel.receive(1, uID);
                                RequestListener requestListener = new RequestListener();
                                LOGGER.info("Send data to [Transaction Engine]");
                                ISOMsg resp = requestListener.process(bc.getChannelConfiguration(), m.clone());
                                LOGGER.info("Receive data from [Transaction Engine]");
                                this.channel.send(resp, 2, uID);
                            } catch (ISOException e) {
                                LOGGER.error(
                                        String.format(
                                                "channel id: %s - session error when processing message",
                                                channelId
                                        )
                                );
                            } catch (TransactionRequiredException e) {
                                LOGGER.error(
                                        String.format(
                                                "channel id: %s - session error when processing message: ",
                                                channelId
                                        ), e
                                );
                            } catch (InterruptedIOException e) {
                                LOGGER.error(
                                        String.format(
                                                "channel id: %s - timeout, detail: ",
                                                channelId
                                        ), e
                                );
                            }
                        }
                    }
                } catch (EOFException | SocketException ignored) {
                } catch (Throwable var23) {
                    LOGGER.error(
                            String.format(
                                    "channel id: %s - session error: ",
                                    channelId
                            ), var23
                    );
                }

                try {
                    this.channel.disconnect();
                } catch (IOException e) {
                    LOGGER.error(
                            String.format(
                                    "channel id: %s - session error: ",
                                    channelId
                            ), e
                    );
                }

                LOGGER.info(
                        String.format(
                                "channel id: %s - session end",
                                channelId
                        )
                );
                MDC.remove("req_id");
            }
        }
    }
}
