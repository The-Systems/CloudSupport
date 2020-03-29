package de.dytanic.cloudnet.api.database;
/*
 * Created by derrop on 09.12.2019
 */

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.wrapper.database.IDatabase;

import java.util.Collection;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class CloudNet2Database implements Database {
    private IDatabase database;

    public CloudNet2Database(IDatabase database) {
        this.database = database;
    }

    @Override
    public Database loadDocuments() {
        return this;
    }

    @Override
    public Collection<Document> getDocs() {
        return this.database.documents().stream().map(document -> Document.load(document.toJson())).collect(Collectors.toList());
    }

    @Override
    public Document getDocument(String name) {
        JsonDocument document = this.database.get(name);
        return document != null ? Document.load(document.toJson()) : null;
    }

    @Override
    public Database insert(Document... documents) {
        for (Document document : documents) {
            this.database.insert(document.getString(Database.UNIQUE_NAME_KEY), JsonDocument.newDocument(document.convertToJsonString()));
        }
        return this;
    }

    @Override
    public Database delete(String name) {
        this.database.delete(name);
        return this;
    }

    @Override
    public Database delete(Document document) {
        return this.delete(document.getString(Database.UNIQUE_NAME_KEY));
    }

    @Override
    public Document load(String name) {
        return this.getDocument(name);
    }

    @Override
    public boolean contains(Document document) {
        return this.contains(document.getString(Database.UNIQUE_NAME_KEY));
    }

    @Override
    public boolean contains(String name) {
        return this.database.contains(name);
    }

    @Override
    public int size() {
        return (int) this.database.getDocumentsCount();
    }

    @Override
    public boolean containsDoc(String name) {
        return this.contains(name);
    }

    @Override
    public Database insertAsync(Document... documents) {
        for (Document document : documents) {
            this.database.insertAsync(document.getString(Database.UNIQUE_NAME_KEY), JsonDocument.newDocument(document.convertToJsonString()));
        }
        return this;
    }

    @Override
    public Database deleteAsync(String name) {
        this.database.deleteAsync(name);
        return this;
    }

    @Override
    public FutureTask<Document> getDocumentAsync(String name) {
        AtomicReference<Document> result = new AtomicReference<>();
        FutureTask<Document> task = new FutureTask<>(result::get);
        this.database.getAsync(name).onComplete(document -> {
            result.set(Document.load(document.toJson()));
            task.run();
        });
        return task;
    }
}
