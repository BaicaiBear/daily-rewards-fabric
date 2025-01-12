package me.alpestrine.c.reward.server;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public interface TickExecutor {
    AtomicInteger currentTick = new AtomicInteger();
    ConcurrentHashMap<Integer, ArrayList<Runnable>> tickTasks = new ConcurrentHashMap<>();
    static void executeOnNextTick(Runnable runnable) {
        executeOnNextTick(runnable, 0);
    }
    static void executeOnNextTick(Runnable runnable, int plus) {
        tickTasks.computeIfAbsent(currentTick.get() + 1 + plus, t -> new ArrayList<>()).add(runnable);
    }
}
