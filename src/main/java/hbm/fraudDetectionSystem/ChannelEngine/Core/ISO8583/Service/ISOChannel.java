package hbm.fraudDetectionSystem.ChannelEngine.Core.ISO8583.Service;

import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Exception.ISOException;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.component.ISOMsg;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOContainer;

import java.io.IOException;

public interface ISOChannel {
    void setContainer(ISOContainer container);
    void connect();
    void disconnect() throws IOException;
    void reconnect();
    boolean isUsable();
    void send(ISOMsg msg, int direction, String uID) throws IOException, ISOException;
    ISOMsg receive(int direction, String uID) throws IOException, ISOException;
}
