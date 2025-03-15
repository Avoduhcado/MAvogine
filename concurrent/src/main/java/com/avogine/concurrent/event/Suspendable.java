package com.avogine.concurrent.event;

import java.util.function.*;

/**
 * @param <T> 
 * @param condition 
 * @param testCase 
 * @param targetFrameTime 
 * @param action 
 */
public record Suspendable<T>(Predicate<T> condition, T testCase, long targetFrameTime, DoubleConsumer action) implements StructuredEvent {

}
