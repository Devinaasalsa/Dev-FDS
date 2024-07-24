package hbm.fraudDetectionSystem.ChannelEngine.Core.ISO8583.ServiceImpl;

import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfiguration;

import java.io.IOException;
import java.net.SocketException;

import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper.hexString;

public class Base24Channel extends BaseChannel {
    public Base24Channel() {
        super();
    }

    public Base24Channel(ChannelConfiguration channelConfiguration) {
        super(channelConfiguration);
    }

    @Override
    protected void sendMessageTrailer() throws IOException {
        this.dos.write(3);
    }

    @Override
    protected void getMessageTrailer() throws IOException {
        byte[] b = new byte[1];
        this.dis.readFully(b, 0, 1);
        System.out.printf("got-message-trailer %s%n\n", hexString(b));
    }

    protected byte[] streamReceive() throws IOException {
        byte[] buf = new byte[4096];

        int i;
        for(i = 0; i < 4096; ++i) {
            int c = this.dis.read();

            if (c == -1) {
                throw new SocketException("Connection reset");
            }

            if (c == 3) {
                break;
            }

            buf[i] = (byte)c;
        }

        if (i == 4096) {
            throw new IOException("message too long");
        } else {
            byte[] d = new byte[i];
            System.arraycopy(buf, 0, d, 0, i);
            return d;
        }
    }
}
