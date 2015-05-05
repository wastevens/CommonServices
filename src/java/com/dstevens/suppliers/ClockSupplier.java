package com.dstevens.suppliers;

import java.time.Clock;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

@Service
public class ClockSupplier implements Supplier<Clock> {

    @Override
    public Clock get() {
        return Clock.systemUTC();
    }

}
