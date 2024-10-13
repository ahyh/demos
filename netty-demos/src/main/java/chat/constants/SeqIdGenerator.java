package chat.constants;

import java.util.concurrent.atomic.AtomicInteger;

public class SeqIdGenerator {

    private static final AtomicInteger id = new AtomicInteger(1);

    public static int nextId() {
        return id.incrementAndGet();
    }
}
