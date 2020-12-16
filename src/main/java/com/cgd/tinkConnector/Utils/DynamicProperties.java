package com.cgd.tinkConnector.Utils;

import com.cgd.tinkConnector.Repositories.DatabasePropertiesRepository;
import com.cgd.tinkConnector.entities.DatabaseProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DynamicProperties {

    public static String CGD_USER_TRANSLATION_TYPE = "cgd.userTranslationType";
    public static String CGD_ACTIVATE_UPLOAD_TO_TINK = "cgd.activateUploadToTink";
    public static String CGD_ACTIVATE_BATCH_FILE_PROCESSING = "cgd.activateBatcheFileProcessing";


    private DatabasePropertiesRepository repository;

    private Map<String, DatabaseProperties> cachedProperties = new HashMap<>();

    public DynamicProperties(DatabasePropertiesRepository repository) {

        this.repository = repository;

    }

    public boolean getPropertyAsBoolean(String key) {

        String prop = getProperty(key);

        if (prop == null) return false;
        else return Boolean.parseBoolean(prop);

    }

    public boolean existsProperty(String key) {

        String propV = getProperty(key);
        return propV != null;

    }

    public int getPropertyAsInteger(String key) {

        String propV = getProperty(key);
        if (propV == null) return 0;

        return Integer.parseInt(propV);

    }

    public String getProperty(String key) {

        if (cachedProperties.containsKey(key)) {

            return cachedProperties.get(key).getValue();
        }
        Optional<DatabaseProperties> element = this.repository.findById(key);

        if (element.isPresent()) {

            cachedProperties.put(key, element.get());
            return element.get().getValue();
        }

        return null;
    }

    public void reset() {

        this.cachedProperties.clear();
    }

    public boolean saveProperty(String propKey, String propValue) {

        if (propKey == null || propValue == null) return false;

        DatabaseProperties prop = new DatabaseProperties();
        prop.setKey(propKey);
        prop.setValue(propValue);
        this.cachedProperties.put(propKey, prop);
        this.repository.save(prop);
        return true;

    }
}
