package hbm.fraudDetectionSystem.ChannelEngine.Core.ISO8583.ServiceImpl;


import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper.byte2hex;
import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.helper.ConversionHelper.hexString;

@Slf4j
public class TCPChannel extends BaseChannel {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    public TCPChannel(ChannelConfiguration channelConfiguration) {
        super(channelConfiguration);
    }

//    @Override
//    protected void sendMessageTrailer() throws IOException {
//        this.dos.write(3);
//    }

    protected void sendMessageLength(int len) throws IOException {
//        ++len;
        this.dos.write(len >> 8);
        this.dos.write(len);
    }

    protected int getMessageLength() throws IOException {
        int l = 0;
        byte[] b = new byte[2];

        while(l == 0) {
            this.dis.readFully(b, 0, 2);
//            System.out.println(byte2hex(b));

//            System.out.println(this.dis.readUTF());
            l = (b[0] & 255) << 8 | b[1] & 255;

            if (l == 0) {
                this.dos.write(b);
                this.dos.flush();
            }
        }

        LOGGER.info(
                String.format(
                        "got-message-length: %s", l
                )
        );
        return l;
    }

//    protected void getMessageTrailer() throws IOException {
//        byte[] b = new byte[1];
//        this.dis.readFully(b, 0, 1);
//        log.info(
//                String.format(
//                        "got-message-trailler: %s", hexString(b)
//                )
//        );
//    }

}
