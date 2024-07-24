package hbm.fraudDetectionSystem.ChannelEngine.Configuration.ISO8583;

import hbm.fraudDetectionSystem.ChannelEngine.Core.ISO8583.ServiceImpl.Base24Channel;
import hbm.fraudDetectionSystem.ChannelEngine.Core.ISO8583.ServiceImpl.ISOServer;
import hbm.fraudDetectionSystem.ChannelEngine.Core.ISO8583.ServiceImpl.TCPChannel;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelConfiguration.ChannelConfiguration;
import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ConnectionConfig.ConnectionConfig;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOBaseContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldConfiguration.FieldConfigurationService;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.HeaderConfiguration.HeaderConfigurationService;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.List;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;

@Configuration
public class ISO8583InitialConfiguration {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    protected final List<ChannelConfiguration> isoChannelConfigurations;
    protected final HeaderConfigurationService headerConfigurationService;
    protected final FieldConfigurationService fieldConfigurationService;

    @Autowired
    public ISO8583InitialConfiguration(List<ChannelConfiguration> isoChannelConfigurations, HeaderConfigurationService headerConfigurationService, FieldConfigurationService fieldConfigurationService) {
        this.isoChannelConfigurations = isoChannelConfigurations;
        this.headerConfigurationService = headerConfigurationService;
        this.fieldConfigurationService = fieldConfigurationService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ISO8583InitialConfig() throws IOException {
        for (ChannelConfiguration channelConfiguration : isoChannelConfigurations) {
            MessageConfiguration msgConfig = channelConfiguration.getMsgConfig();
            ConnectionConfig connectionConfig = channelConfiguration.getConnectionConfig();
            ISOServer isoServer = null;

            switch (connectionConfig.getState()) {
                case SERVER:
                    LOGGER.info("Prepare container by config id");
                    ISOBaseContainer container = this.getContainerByConfigId(msgConfig.getConfigId().toString());
                    LOGGER.info("Successfully prepare container");

                    //TCP TYPE BASE24CHANNEL, DLL
                    switch (connectionConfig.getType().getTcpTypeId().intValue()) {
                        case 1:
                            Base24Channel channel = new Base24Channel(channelConfiguration);
                            ServerSocket ss = new ServerSocket(connectionConfig.getLocalPort(), 50, InetAddress.getByName(connectionConfig.getRemoteAddr().equals("ANY") ? "0.0.0.0" : connectionConfig.getRemoteAddr()));
                            ss.setReuseAddress(true);
                            channel.setServerSocket(ss);
                            channel.setContainer(container);
                            channel.setTimeoutTime(connectionConfig.getToTime());
                            channel.setRequiredSendLogon(connectionConfig.getLogonReq());

                            isoServer = new ISOServer(channel);
                            new Thread(isoServer, String.format("iso-server-%s", channel.getChannelId())).start();
                            break;

                        case 2:
                            TCPChannel tcpChannel = new TCPChannel(channelConfiguration);
                            ServerSocket tcpSs = new ServerSocket(connectionConfig.getLocalPort(), 50, InetAddress.getByName(connectionConfig.getRemoteAddr().equals("ANY") ? "0.0.0.0" : connectionConfig.getRemoteAddr()));
                            tcpSs.setReuseAddress(true);
                            tcpChannel.setServerSocket(tcpSs);
                            tcpChannel.setContainer(container);
                            tcpChannel.setTimeoutTime(connectionConfig.getToTime());
                            tcpChannel.setRequiredSendLogon(connectionConfig.getLogonReq());

                            isoServer = new ISOServer(tcpChannel);
                            new Thread(isoServer, String.format("iso-server-%s", tcpChannel.getChannelId())).start();
                            break;
                        default:
                            break;
                    }
                    break;

                case CLIENT:
                default:
                    break;
            }
        }
    }

    protected ISOBaseContainer getContainerByConfigId(String configId) {
        return new ISOBaseContainer(headerConfigurationService.getHeaderConfigurationFromMemory(configId), fieldConfigurationService.getFieldConfigurationFromMemory(configId));
    }
}
