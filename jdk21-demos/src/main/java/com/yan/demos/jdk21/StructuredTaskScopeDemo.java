package com.yan.demos.jdk21;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class StructuredTaskScopeDemo {

    public static void main(String[] args) {
        try {
            StructuredTaskScopeOnFailOrHalfSuccess<Integer> scope = new StructuredTaskScopeOnFailOrHalfSuccess<>();
            for (int i = 0;i<10;i++) {
                scope.fork(() -> {
                    int random = new Random(100).nextInt();
                    if (random > 66) {
                        throw new RuntimeException("gt 66");
                    }
                    return random;
                });
            }
            scope.join();
            System.out.println(scope.getResults());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
