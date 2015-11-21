package com.github.otbproject.otbproject.config;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface WrappedConfig<T> {
    <R> R get(Function<T, R> function);

    <R> R getExactly(Function<T, R> function) throws ExecutionException, InterruptedException, TimeoutException;

    <R> Optional<R> getExactlyAsOptional(Function<T, R> function);

    void edit(Consumer<T> consumer);

    void updateLater();

    void updateAndAwait();

    static <T> WrappedConfig<T> of(Class<T> tClass, String path, Supplier<T> defaultConfigSupplier) {
        return new WrappedConfigImpl<>(tClass, path, defaultConfigSupplier);
    }
}
