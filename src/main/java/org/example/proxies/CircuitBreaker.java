package org.example.proxies;

import lombok.Getter;
import lombok.Setter;
import org.example.interfaces.CheckedFunction;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Getter @Setter
public class CircuitBreaker {
    private CircuitBreakerState state = CircuitBreakerState.Closed;
    protected LocalTime failureInstant = null;
    protected int retryCount = 0;
    protected final int RETRY_THRESHOLD = 3;
    protected final int MILISECONDS_THRESHOLD = 10;

    public Object invokeFunction(CheckedFunction<Object> supplier) throws InterruptedException {
        return state.invokeFunction(this, supplier);
    }

    public void nextState() {
        state = state.nextState(this);
    }
    public void resetRetryCount() {
        retryCount = 0;
    }
    public Object executeWithRetry(CheckedFunction<Object> supplier) {
            try {
                return supplier.apply();
            } catch (Exception e) {
                retryCount++;
        }
        return null;
    }
    public boolean isTimeoutElapsed() {
        if (failureInstant == null) {
            return false;
        }
        LocalTime now = LocalTime.now();
        return ChronoUnit.MILLIS.between(failureInstant, now) > MILISECONDS_THRESHOLD;
    }
}
enum CircuitBreakerState {
    Closed {
        @Override
        public CircuitBreakerState nextState(CircuitBreaker cb) {
            if (cb.retryCount >= cb.RETRY_THRESHOLD) {
                cb.resetRetryCount();
                cb.failureInstant = LocalTime.now();
                return Open;
            }
            return this;
        }

        @Override
        public Object invokeFunction(CircuitBreaker cb, CheckedFunction<Object> supplier) {
            return cb.executeWithRetry(supplier);
        }
    },
    Open {
        @Override
        public CircuitBreakerState nextState(CircuitBreaker cb) {
            if (cb.isTimeoutElapsed()) {
                cb.resetRetryCount();
                return HalfOpen;
            }
            return this;
        }

        @Override
        public Object invokeFunction(CircuitBreaker cb, CheckedFunction<Object> supplier) {

            return null;
        }
    },
    HalfOpen {
        @Override
        public CircuitBreakerState nextState(CircuitBreaker cb) {
            return this;
        }

        @Override
        public Object invokeFunction(CircuitBreaker cb, CheckedFunction<Object> supplier) {
            return cb.executeWithRetry(supplier);
        }
    };

    public abstract CircuitBreakerState nextState(CircuitBreaker cb);
    public abstract Object invokeFunction(CircuitBreaker cb, CheckedFunction<Object> supplier);
}

