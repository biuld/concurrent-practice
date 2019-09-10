package com.github.biuld.graph;

import com.codahale.metrics.*;

import java.util.concurrent.*;

public class MonitoredThreadPoolExecutor extends ThreadPoolExecutor {

    private final MetricRegistry metricRegistry = new MetricRegistry();
    private final ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry).build();
    private final String metricsPrefix = MetricRegistry.name(getClass(), "monitored");
    private ThreadLocal<Timer.Context> taskExecutionTimer = new ThreadLocal<>();

    public static ThreadPoolExecutor fixedThreadPool(int threads) {
        return new MonitoredThreadPoolExecutor(
                threads,
                threads,
                300L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(),
                new ThreadPoolExecutor.AbortPolicy(),
                1);
    }

    public MonitoredThreadPoolExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            Integer monitoredSeconds) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        reporter.start(monitoredSeconds, TimeUnit.SECONDS);
        registerGauges();
    }

    public MonitoredThreadPoolExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory,
            Integer monitoredSeconds) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        reporter.start(monitoredSeconds, TimeUnit.SECONDS);
        registerGauges();
    }

    public MonitoredThreadPoolExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            RejectedExecutionHandler handler,
            Integer monitoredSeconds) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        reporter.start(monitoredSeconds, TimeUnit.SECONDS);
        registerGauges();
    }

    public MonitoredThreadPoolExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory,
            RejectedExecutionHandler handler,
            Integer monitoredSeconds) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        reporter.start(monitoredSeconds, TimeUnit.SECONDS);
        registerGauges();
    }

    private void registerGauges() {
        metricRegistry.register(MetricRegistry.name(metricsPrefix, "corePoolSize"), (Gauge<Integer>) this::getCorePoolSize);
        metricRegistry.register(MetricRegistry.name(metricsPrefix, "activeThreads"), (Gauge<Integer>) this::getActiveCount);
        metricRegistry.register(MetricRegistry.name(metricsPrefix, "maxPoolSize"), (Gauge<Integer>) this::getMaximumPoolSize);
        metricRegistry.register(MetricRegistry.name(metricsPrefix, "queueSize"), (Gauge<Integer>) () -> getQueue().size());
    }

    @Override
    protected void beforeExecute(Thread thread, Runnable task) {
        super.beforeExecute(thread, task);
        Timer timer = metricRegistry.timer(MetricRegistry.name(metricsPrefix, "task-execution"));
        taskExecutionTimer.set(timer.time());
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        Timer.Context context = taskExecutionTimer.get();
        context.stop();
        super.afterExecute(runnable, throwable);
        if (throwable == null && runnable instanceof Future && ((Future) runnable).isDone()) {
            try {
                ((Future) runnable).get();
            } catch (CancellationException ce) {
                throwable = ce;
            } catch (ExecutionException ee) {
                throwable = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        if (throwable != null) {
            Counter failedTasksCounter = metricRegistry.counter(MetricRegistry.name(metricsPrefix, "failed-tasks"));
            failedTasksCounter.inc();
        } else {
            Counter successfulTasksCounter = metricRegistry.counter(MetricRegistry.name(metricsPrefix, "successful-tasks"));
            successfulTasksCounter.inc();
        }
    }
}
