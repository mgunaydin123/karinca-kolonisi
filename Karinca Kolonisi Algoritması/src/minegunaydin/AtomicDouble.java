package minegunaydin;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicDouble extends Number implements Comparable<AtomicDouble> {

    private AtomicReference<Double> atomicReference;
    public AtomicDouble(Double doubleValue){
        atomicReference = new AtomicReference<Double>(doubleValue);
    }

    @Override
    public int compareTo(AtomicDouble atomicDouble) {
        return Double.compare(this.doubleValue(), atomicDouble.doubleValue());
    }

    @Override
    public int intValue() {
        return atomicReference.get().intValue();
    }

    @Override
    public long longValue() {
        return atomicReference.get().longValue();
    }

    @Override
    public float floatValue() {
        return atomicReference.get().floatValue();
    }

    @Override
    public double doubleValue() {
        return atomicReference.get();
    }

    public boolean compareAndSet(double updatedValue){
        return (atomicReference.compareAndSet(atomicReference.get(), updatedValue));
    }
}
