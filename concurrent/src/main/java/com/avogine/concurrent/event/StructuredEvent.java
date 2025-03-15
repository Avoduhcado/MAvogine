package com.avogine.concurrent.event;

/**
 *
 */
public sealed interface StructuredEvent permits Suspendable, AsyncAction {

}
