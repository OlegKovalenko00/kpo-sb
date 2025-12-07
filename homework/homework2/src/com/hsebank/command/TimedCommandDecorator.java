package com.hsebank.command;

import com.hsebank.metrics.MetricsCollector;

public final class TimedCommandDecorator implements Command {

    private final Command target;
    private final MetricsCollector metrics;
    private final String name;

    public TimedCommandDecorator(Command target,
                                 MetricsCollector metrics,
                                 String name) {
        this.target = target;
        this.metrics = metrics;
        this.name = name;
    }

    public TimedCommandDecorator(Command target,
                                 MetricsCollector metrics) {
        this(target, metrics, target.getClass().getSimpleName());
    }

    @Override
    public void execute() {
        long start = System.nanoTime();
        try {
            target.execute();
        } catch (Exception e) {
            if (e instanceof RuntimeException re) {
                throw re;
            }
            throw new RuntimeException(e);
        } finally {
            long end = System.nanoTime();
            metrics.record(name, end - start);
        }
    }
}
