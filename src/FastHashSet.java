import java.util.*;

// an HashSet extension that constantly keeps track of the value of its hashCode() method, making it constant (amortized) time.
class FastHashSet<E> extends HashSet<E> {
  int h = 0;

  public FastHashSet(){
    super();
    h = 0;
  }

  public FastHashSet(Collection<? extends E> c) {
    super(c);
    h = super.hashCode();
  }

  public FastHashSet(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
    h = 0;
  }
  public FastHashSet(int initialCapacity) {
    super(initialCapacity);
    h = 0;
  }

  @Override
  public boolean add(E e) {
    boolean out = super.add(e);
    h += e.hashCode();
    return out;
  }

  @Override
  public boolean remove(Object o) {
      boolean out = super.remove(o);
      h -= o.hashCode();
      return out;
    }

  @Override
  public void clear() {
    super.clear();
    h = 0;
  }

  @Override
  public int hashCode() {
    return h;
  }

}
