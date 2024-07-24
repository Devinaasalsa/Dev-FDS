package hbm.fraudDetectionSystem.GeneralComponent.Utility;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class LowercasePhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {
    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        if (name != null) {
            String snakeCaseName = name.getText().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            return Identifier.toIdentifier("\"" + snakeCaseName + "\"");
        }
        return super.toPhysicalColumnName(null, context);
    }
}
