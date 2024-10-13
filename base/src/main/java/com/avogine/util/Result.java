package com.avogine.util;

import java.util.Objects;
import java.util.function.*;

/**
 *
 * @param <T>
 * @param success
 * @param failure
 */
public record Result<T>(T success, Exception failure) {

	/**
	 * Validate that at least one, but only one field has been set.
	 */
	public Result {
		if (Objects.isNull(success)) {
			Objects.requireNonNull(failure);
		} else if (Objects.nonNull(failure)) {
			throw new IllegalStateException("Cannot construct a successful Result with a failure.");
		}
	}

	/**
	 * Construct a {@link Result} containing the successful value of the wrapped operation.
	 * @param success The successful result value of the wrapped operation.
	 */
	public Result(T success) {
		this(success, null);
	}

	/**
	 * Construct a {@link Result} containing the caught {@link Exception} of the wrapped operation.
	 * @param failure The failure result {@link Exception} of the wrapped operation.
	 */
	public Result(Exception failure) {
		this(null, failure);
	}

	/**
	 * @return true if this {@link Result} represents a successful operation.
	 */
	public boolean isSuccess() {
		return Objects.nonNull(success);
	}

	/**
	 * @return true if this {@link Result} represents a failed operation.
	 */
	public boolean isFailure() {
		return !isSuccess();
	}
	
	/**
	 * @return The successful result value if {@link Result#isSuccess()} is true, otherwise returns {@code null}.
	 */
	public T getOrNull() {
		if (failure != null) {
			return null;
		}
		return success;
	}
	
	/**
	 * @return The successful result value if {@link Result#isSuccess()} is true, otherwise throws {@link Result#failure()}.
	 * @throws Exception The caught {@link Exception} if this Result failed.
	 */
	public T getOrThrow() throws Exception {
		if (failure != null) {
			throw failure;
		}
		return success;
	}
	
	public T getOrElse(Function<Exception, T> failedAction) {
		if (failure != null) {
			return failedAction.apply(failure);
		}
		return success;
	}

	public void ifPresent(Consumer<? super T> action) {
		if (success != null) {
			action.accept(success);
		}
	}

	@FunctionalInterface
	public interface ThrowingSupplier<T, E extends Exception> {
		T get() throws E;
	}
	
	/**
	 * @param <T>
	 * @param block
	 * @return
	 */
	public static <T> Result<T> runCatching(ThrowingSupplier<T, ? extends Exception> block) {
		try {
			return new Result<>(block.get());
		} catch (Exception e) {
			return new Result<>(e);
		}
	}
}
