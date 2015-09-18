package com.github.otbproject.otbproject.config;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.util.JsonHandler;
import com.github.otbproject.otbproject.util.ThreadUtil;

import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class WrappedConfig<T> {
    protected static final BlockingDeque<Runnable> UPDATE_DEQUE = new LinkedBlockingDeque<>();
    protected static final ExecutorService UPDATE_SERVICE;

    static {
        UPDATE_SERVICE = ThreadUtil.getSingleThreadExecutor("config-updater");
        UPDATE_SERVICE.execute(() -> {
            try {
                while (true) {
                    UPDATE_DEQUE.takeFirst().run();
                }
            } catch (InterruptedException e) {
                App.logger.error("Interrupted config updater service. Configs can no longer be modified. This is probably not what you wanted to do.");
                Thread.currentThread().interrupt();
            }
        });
    }

    protected T config;
    protected final Class<T> tClass;
    protected final String path;
    private boolean needsUpdate = false;

    public WrappedConfig (Class<T> tClass, String path, Supplier<T> defaultConfigSupplier) {
        this.tClass = tClass;
        this.path = path;
        config = JsonHandler.readValue(path, tClass).orElseGet(defaultConfigSupplier);
        writeToFile();
    }

    public <R> R get(Function<T, R> function) {
        return function.apply(config);
    }

    public void edit(Consumer<T> consumer) {
        UPDATE_DEQUE.addLast(() -> {
            updateIfNeeded();
            consumer.accept(config);
            writeToFile();
        });
    }

    protected void writeToFile() {
        JsonHandler.writeValue(path, config);
    }

    public void update() {
        queueUpdate(false);
    }

    protected void queueUpdate(boolean logUpdate) {
        UPDATE_DEQUE.addFirst(() -> {
            if (!needsUpdate) {
                needsUpdate = true;
                UPDATE_DEQUE.addLast(() -> this.updateIfNeeded(logUpdate));
            }
        });
    }

    protected void updateIfNeeded() {
        updateIfNeeded(true);
    }

    private void updateIfNeeded(boolean logUpdate) {
        if (needsUpdate) {
            needsUpdate = false;
            if (updateFromFile() && logUpdate) {
                App.logger.debug("Updated changed file: " + path);
            }
        }
    }

    private boolean updateFromFile() {
        Optional<T> optional = JsonHandler.readValue(path, tClass);
        config = optional.orElse(config);
        return optional.isPresent();
    }
}
