import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class RandomPolicy implements SwapPolicy {

  private HashSet<Integer> pages;

  public RandomPolicy() {
    pages = new HashSet<>();
  }

  @Override
  public void pageAccessed(int page) {
    pages.add(page);
  }

  @Override
  public int whichPageShouldBeEvicted() {
    Random r = new Random();
    int slot = r.nextInt(pages.size());
    Iterator<Integer> it = pages.iterator();
    for (int i = 0; i < slot; ++i) {
      it.next();
    }
    int ret = it.next();
    it.remove();
    return ret;
  }

}

//  Write classes implementing the LRU policy, and the optimal policy.

// The optimal policy should take an ArrayList of song numbers as a
// constructor parameter (it needs this to read the future).  The LRU policy
// shouldn't need to take any parameters.