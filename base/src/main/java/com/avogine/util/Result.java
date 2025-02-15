package com.avogine.util;

import java.util.function.Function;

/**
 * @param <T> The type of value in the event of a successful {@link Result}.
 * @param value
 */
public record Result<T>(Object value) {
	
	/**
	 * Construct a {@link Result} containing a successful value.
	 * @param <T> The type of value.
	 * @param value The successful result value.
	 * @return a {@link Result} containing a successful value.
	 */
	public static <T> Result<T> success(T value) {
		return new Result<>(value);
	}
	
	/**
	 * Construct a {@link Result} containing a caught {@link Throwable}.
	 * @param <T> The type of a would be successful value.
	 * @param exception The failure result {@link Throwable}.
	 * @return a {@link Result} containing an encapsulated {@link Throwable}.
	 */
	public static <T> Result<T> failure(Throwable exception) {
		return new Result<>(new Failure(exception));
	}
	
	/**
	 * @return true if this {@link Result} represents a successful outcome.
	 */
	public boolean isSuccess() {
		return !(value instanceof Failure);
	}
	
	/**
	 * @return true if this {@link Result} represents a failed outcome.
	 */
	public boolean isFailure() {
		return value instanceof Failure;
	}
	
	/**
	 * @return The successful result value if {@link Result#isSuccess()} is true, otherwise returns {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public T getOrNull() {
		return isFailure() ? null : (T) value;
	}
	
	/**
	 * @return The encapsulated {@link Throwable} if {@link Result#isFailure()} is true, otherwise returns {@code null}.
	 */
	public Throwable exceptionOrNull() {
		return value instanceof Failure(Throwable exception) ? exception : null;
	}
	
	/**
	 * @return The successful result value if {@link Result#isSuccess()} is true, otherwise throws the encapsulated {@link Throwable}.
	 * @throws Throwable The encapsulated {@link Throwable} if this Result failed.
	 */
	@SuppressWarnings("unchecked")
	public T getOrThrow() throws Throwable {
		throwOnFailure();
		return (T) value;
	}
	
	/**
	 * @param onFailure
	 * @return The successful result value if {@link Result#isSuccess()} is true, otherwise returns the result of {@code onFailure}.
	 */
	@SuppressWarnings("unchecked")
	public T getOrElse(Function<Throwable, T> onFailure) {
		return switch (exceptionOrNull()) {
			case null -> (T) value;
			case Throwable exception -> onFailure.apply(exception);
		};
	}
	
	/**
	 * @param defaultValue
	 * @return The successful result value if {@link Result#isSuccess()} is true, otherwise returns {@code defaultValue}.
	 */
	@SuppressWarnings("unchecked")
	public T getOrDefault(T defaultValue) {
		if (isFailure()) {
			return defaultValue;
		}
		return (T) value;
	}
	
	@SuppressWarnings("squid:S112") // Result is intended to enhance "known" cases of error handling and _should not_ be a total replacement for try/catch in any instances of the code.
	private void throwOnFailure() throws Throwable {
		if (value instanceof Failure(Throwable exception)) {
			throw exception;
		}
	}
	
	private static record Failure(Throwable exception) {
		
	}
	
	/**
	 *
	 * @param <T>
	 * @param <E>
	 */
	@FunctionalInterface
	public interface ThrowingSupplier<T, E extends Throwable> {
		/**
		 * @return a result
		 * @throws E
		 */
		T get() throws E;
	}
	
	/**
	 * @param <T>
	 * @param block
	 * @return a {@link Result} containing either a successful value or an encapsulated {@link Throwable}.
	 */
	public static <T> Result<T> runCatching(ThrowingSupplier<T, Throwable> block) {
		try {
			return Result.success(block.get());
		} catch (Throwable e) {
			return Result.failure(e);
		}
	}
}
