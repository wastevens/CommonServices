package com.dstevens.suppliers;

import static java.util.UUID.randomUUID;

import java.util.function.Supplier;

import org.springframework.stereotype.Service;

@Service
public class IdSupplier implements Supplier<String> {

    @Override
    public String get() {
        return randomUUID().toString();
    }

}
