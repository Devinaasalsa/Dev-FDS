package hbm.fraudDetectionSystem.ChannelEngine.Core.ISO8583.Service;

public interface ClientChannel {
    void setHost(String ip, int port);

    String getIp();

    int getPort();
}
