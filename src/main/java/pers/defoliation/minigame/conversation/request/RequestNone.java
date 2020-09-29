package pers.defoliation.minigame.conversation.request;

import java.util.Optional;
import java.util.function.Consumer;

public class RequestNone extends RequestBase {

    public static RequestNone newRequestPlayer(Consumer<Request> c) {
        return new RequestNone(c);
    }

    private Consumer<Request> consumer;

    public RequestNone(Consumer<Request> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void start() {
        consumer.accept(this);
        setCompleted(true);
    }

    @Override
    public void reset() {

    }

    @Override
    public Optional getResult() {
        return Optional.empty();
    }
}
