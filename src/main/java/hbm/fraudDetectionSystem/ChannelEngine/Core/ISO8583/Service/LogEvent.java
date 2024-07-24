package hbm.fraudDetectionSystem.ChannelEngine.Core.ISO8583.Service;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.*;

public class LogEvent {
    protected StringBuilder logger;

    public LogEvent() {
        this.logger = new StringBuilder();
    }

    /*
        1 -> Common Logging
        2 -> Log Separator
        3 -> Log Class
        4 -> Log without time
        5 -> Log without new line
     */
    public void addMessage(String message, int type) {
        switch (type) {
            case 1:
                LOG(this.logger, message);
                break;

            case 2:
                this.logger.append("\n");
                LOGSEPARATOR(this.logger);
                break;

            case 4:
                LOGWithoutTime(this.logger, message);
                break;

            case 5:
                LOGWithoutNewLine(this.logger, message);
                break;

            case 3:
            default:
                break;
        }
    }

    public void dump(boolean trim) {
        if (trim)
            System.out.println(this.logger.toString().trim());
        else
            System.out.println(this.logger);
    }
}
