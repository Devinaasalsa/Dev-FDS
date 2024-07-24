package hbm.fraudDetectionSystem.ChannelEngine.Core.ISO8583.ServiceImpl;

import hbm.fraudDetectionSystem.ChannelEngine.Core.ISO8583.Service.ClientChannel;

import hbm.fraudDetectionSystem.ChannelEngine.Core.ISO8583.Service.ServerChannel;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOMsg;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOContainer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;

@Slf4j
public abstract class BaseChannel implements ClientChannel, ServerChannel, Cloneable {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected ChannelConfiguration channelConfiguration;
    protected boolean requiredSendLogon;
    protected ISOContainer container;
    protected String ip;
    protected int port;
    protected ServerSocket serverSocket;
    protected int timeoutTime;
    protected int connectTimeoutTime;
    protected boolean keepAlive;
    protected boolean usable;
    protected int maxPacketLength;
    protected Socket socket;
    protected DataInputStream dis;
    protected DataOutputStream dos;
    protected Object disLock;
    protected Object dosLock;
    protected Object dumpLock;

    public BaseChannel() {
        this.maxPacketLength = 100000;
        this.disLock = new Object();
        this.dosLock = new Object();
        this.dumpLock = new Object();
    }

    public BaseChannel(ChannelConfiguration channelConfiguration) {
        this.maxPacketLength = 100000;
        this.channelConfiguration = channelConfiguration;
        this.disLock = new Object();
        this.dosLock = new Object();
        this.dumpLock = new Object();
    }

    public long getChannelId() {
        return channelConfiguration.getPid();
    }

    public long getConfigId() {
        return channelConfiguration.getPid();
    }

    public int getTimeoutTime() {
        return timeoutTime;
    }

    public ChannelConfiguration getChannelConfiguration() {
        return channelConfiguration;
    }

    @Override
    public void setHost(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String getIp() {
        return this.ip;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    public boolean isRequiredSendLogon() {
        return requiredSendLogon;
    }

    public void setRequiredSendLogon(boolean requiredSendLogon) {
        this.requiredSendLogon = requiredSendLogon;
    }

    @Override
    public void setContainer(ISOContainer container) {
        this.container = container;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void setTimeoutTime(int timeoutTime) {
        this.timeoutTime = timeoutTime;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public Socket getSocket() {
        return this.socket;
    }

    protected void sendMessageLength(int len) throws IOException {

    }

    protected int getMessageLength() throws IOException, ISOException {
        return -1;
    }

    protected void sendMessageTrailer() throws IOException {

    }

    protected void getMessageTrailer() throws IOException {

    }

    @Override
    public void accept() throws IOException {
        Socket s = this.serverSocket.accept();
        this.connect(s);
        this.usable = true;
    }

    @Override
    public boolean isUsable() {
        return this.usable;
    }

    protected void applyTimeout() throws SocketException {
        if (this.socket != null) {
            this.socket.setKeepAlive(this.keepAlive);
            if (this.timeoutTime >= 0) {
                //TODO: Need More analysis
                this.setSoTimeout(this.timeoutTime);
            }
        }
    }

    protected void setSoTimeout(int time) throws SocketException {
        this.socket.setSoTimeout(time);
    }

    @Override
    public void connect() {
        try {
            this.connect(this.newSocket(this.ip, this.port));
            this.applyTimeout();
            this.usable = true;
        } catch (ConnectException e) {
            System.out.println("Connection Refused");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void connect(Socket socket) throws IOException {
        this.socket = socket;
        this.applyTimeout();
        synchronized (this.disLock) {
            this.dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        }

        synchronized (this.dosLock) {
            this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        }
    }

    protected Socket newSocket(String ip, int port) throws IOException {
        Socket s = new Socket();
        s.connect(new InetSocketAddress(ip, port), this.connectTimeoutTime);
        return s;
    }

    protected void closeSocket() throws IOException {
        Socket s = null;
        if (this.socket != null) {
            s = this.socket;
            this.socket = null;
        }

        if (s != null) {
            s.shutdownOutput();
        }
    }

    @Override
    public void disconnect() throws IOException {
        try {
            this.usable = false;
            this.closeSocket();

            if (this.dis != null) {
                try {
                    this.dis.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

            if (this.dos != null) {
                try {
                    this.dos.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            throw e;
        }

        this.socket = null;
    }

    @Override
    public void reconnect() {

    }

    @Override
    public void send(ISOMsg msg, int direction, String uID) throws IOException, ISOException {
        try {
            if (!isConnected()) {
                throw new IOException("Channel isn't connected");
            }

            msg.setContainer(this.container);

            byte[] b = this.pack(msg);

            synchronized (this.dosLock) {
                this.sendMessageLength(b.length);
                this.sendMessage(b);
                this.sendMessageTrailer();
                this.dos.flush();
            }
        } catch (ISOException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("unexpected exception", e);
        } finally {
            LOGGER.info(msg.getLogger().toString());

            switch (direction) {
                case 1:
                    LOGGER.info(
                            String.format(
                                    "Sending message to host %s:%s",
                                    socket.getInetAddress().toString().replace("/", ""), socket.getLocalPort()
                            )
                    );
                    break;

                case 2:
                    LOGGER.info(
                            String.format(
                                    "Sending message to client %s:%s",
                                    socket.getInetAddress().getHostAddress(), socket.getPort()
                            )
                    );
            }
        }
    }

    protected void sendMessage(byte[] msg) throws IOException {
        this.dos.write(msg);
    }

    @Override
    public ISOMsg receive(int direction, String uID) throws IOException, ISOException {
        ISOMsg m = new ISOMsg();

        try {
            if (!isConnected()) {
                throw new ISOException("Channel isn't connected");
            }

            byte[] b;

            synchronized (this.disLock) {
                int len = this.getMessageLength();

                if (len == -1) {
                    b = this.streamReceive();
                } else {
                    if (len <= 0 || len > this.maxPacketLength) {
                        throw new ISOException("receive length " + len + " seems strange - maxPacketLength = " + this.maxPacketLength);
                    }

                    b = new byte[len];
                    this.getMessage(b, 0, len);
                    this.getMessageTrailer();
                }

                m.setContainer(this.container);

                if (b.length > 0) {
                    switch (direction) {
                        case 1:
                            LOGGER.info(
                                    String.format(
                                            "Incoming message from client %s:%s",
                                            socket.getInetAddress().getHostAddress(), socket.getPort()
                                    )
                            );
                            break;

                        case 2:
                            LOGGER.info(
                                    String.format(
                                            "Incoming message from host %s:%s",
                                            socket.getInetAddress().toString().replace("/", ""), socket.getLocalPort()
                                    )
                            );
                    }

                    this.unpack(m, b);
                    LOGGER.info(
                            String.format(
                                    "Unpacking message: \n%s",
                                    m.getLogger().toString()
                            )
                    );
                }

            }
        } catch (ISOException e) {
            LOGGER.info(
                    String.format(
                            "Unpacking message: \n%s",
                            m.getLogger().toString()
                    )
            );
            LOGGER.error("", e);
            throw e;
        } catch (EOFException e) {
            this.closeSocket();
            LOGGER.error(
                    "peer-disconnect reason: ",
                    e
            );
            throw e;
        } catch (SocketException e) {
            this.closeSocket();
            if (this.usable) {
                LOGGER.error(
                        "peer-disconnect reason: ",
                        e
                );
            }
            throw e;
        } catch (InterruptedIOException e) {
//            this.closeSocket();
            LOGGER.error(
                    "io-timeout reason: ",
                    e
            );
            throw e;
        } catch (IOException e) {
            this.closeSocket();
            if (this.usable) {
                LOGGER.error("", e);
            }
            throw e;
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new IOException("unexpected exception", e);
        } finally {

        }
        return m;
    }

    protected byte[] streamReceive() throws IOException {
        return new byte[0];
    }

    protected void getMessage(byte[] b, int offset, int len) throws IOException {
        this.dis.readFully(b, offset, len);
    }

    protected byte[] pack(ISOMsg m) throws ISOException {
        return m.pack();
    }

    protected void unpack(ISOMsg m, byte[] b) throws ISOException {
        m.unpack(b);
    }

    protected boolean isConnected() {
        return this.socket != null && this.usable;
    }

    public Object clone() {
        try {
            BaseChannel channel = (BaseChannel) super.clone();
            channel.disLock = new Object();
            channel.dosLock = new Object();
            channel.dumpLock = new Object();
            channel.dis = null;
            channel.dos = null;
            channel.usable = false;
            channel.socket = null;
            return channel;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
