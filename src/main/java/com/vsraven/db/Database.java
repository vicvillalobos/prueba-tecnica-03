package com.vsraven.db;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.Closeable;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentMap;

public class Database implements Closeable {

    private final DB db;

    public Database (Path dbPath) {

        this.createDirectory(dbPath);

        this.db = DBMaker
                .fileDB(dbPath.toFile())
                .fileMmapEnableIfSupported()
                .transactionEnable()
                .make();
    }

    private void createDirectory(Path dbPath) {
        var dir = dbPath.getParent().toFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Failed to create directory for database: " + dir.getAbsolutePath());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> ConcurrentMap<String, T> getCollection(String name) {
        return (ConcurrentMap<String, T>) db
                .hashMap(name)
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.JAVA)
                .createOrOpen();
    }

    public void commit() {
        db.commit();
    }

    @Override
    public void close() {
        if(!db.isClosed()) {
            db.close();
        }
    }

}
