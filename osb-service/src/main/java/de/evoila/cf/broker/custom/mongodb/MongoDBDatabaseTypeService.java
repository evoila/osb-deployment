package de.evoila.cf.broker.custom.mongodb;

import de.evoila.cf.broker.service.BackupTypeService;
import de.evoila.cf.model.enums.DatabaseType;
import org.springframework.stereotype.Service;

@Service
public class MongoDBDatabaseTypeService implements BackupTypeService {
    @Override
    public DatabaseType getType () {
        return DatabaseType.MongoDB;
    }
}
