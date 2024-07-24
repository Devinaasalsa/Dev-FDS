package hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ConnectionConfig;

import java.io.Serializable;
import java.util.Objects;

public class ConnectionConfigPK implements Serializable {

    private Long pid;

    public ConnectionConfigPK(Long pid) {
        this.pid = pid;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    // Overrides

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectionConfigPK)) return false;
        ConnectionConfigPK that = (ConnectionConfigPK) o;
        return Objects.equals(getPid(), that.getPid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPid());
    }


}
