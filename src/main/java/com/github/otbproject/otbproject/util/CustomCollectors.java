package com.github.otbproject.otbproject.util;

import java.util.ArrayList;
import java.util.stream.Collector;

public final class CustomCollectors {
    public static <T> Collector<T, ?, ArrayList<T>> toArrayList() {
        return Collector.of(ArrayList::new, ArrayList::add, (c1, c2) -> { c1.addAll(c2); return c1; });
    }
}
