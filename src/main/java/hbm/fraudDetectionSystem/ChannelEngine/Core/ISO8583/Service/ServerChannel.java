package hbm.fraudDetectionSystem.ChannelEngine.Core.ISO8583.Service;

import java.io.IOException;

public interface ServerChannel extends ISOChannel {
    void accept() throws IOException;
}
