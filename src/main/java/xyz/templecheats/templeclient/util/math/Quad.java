package xyz.templecheats.templeclient.util.math;

public class Quad<T> {
    private final T first;
    private final T second;
    private final T third;
    private final T fourth;

    public Quad(T first, T second, T third, T fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    public T getThird() {
        return third;
    }

    public T getFourth() {
        return fourth;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Quad<?> quad = (Quad<?>) obj;
        return first.equals(quad.first) &&
                second.equals(quad.second) &&
                third.equals(quad.third) &&
                fourth.equals(quad.fourth);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        result = 31 * result + third.hashCode();
        result = 31 * result + fourth.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Quad{" + "first=" + first + ", second=" + second + ", third=" + third + ", fourth=" + fourth + '}';
    }
}
