package com.avogine.concurrent.event;

/**
 * Perform a single {@link Runnable} action without blocking.
 * 
 * This action should ideally be performed without much latency as it will block the enclosing scope for its entire duration.
 * @param action 
 */
public record AsyncAction(Runnable action) implements StructuredEvent {

}
