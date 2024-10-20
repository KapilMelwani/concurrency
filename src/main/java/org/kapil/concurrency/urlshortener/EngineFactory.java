package org.kapil.concurrency.urlshortener;

import java.security.NoSuchAlgorithmException;
import org.kapil.concurrency.urlshortener.core.Engine;
import org.kapil.concurrency.urlshortener.core.EngineType;
import org.kapil.concurrency.urlshortener.core.HashingEngine;
import org.kapil.concurrency.urlshortener.core.KeyGenerator;

public class EngineFactory {

    public Engine getEngine(EngineType type, int machineId, int datacenterId) throws NoSuchAlgorithmException {
        switch (type) {
            case HASHING -> {
                return new HashingEngine();
            }
            case KEY_GENERATOR -> {
                return new KeyGenerator(machineId,datacenterId);
            }
            default -> throw new NoSuchAlgorithmException();
        }
    }
}