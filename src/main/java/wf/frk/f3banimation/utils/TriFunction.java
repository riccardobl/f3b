package wf.frk.f3banimation.utils;

import java.util.Objects;
import java.util.function.Function;

public interface TriFunction<A,B,C,R>{

    R apply(A a,B b,C c);

    
    default <V> TriFunction<A, B,C, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (A t, B u,C c) -> after.apply(apply(t, u,c));
    }

}