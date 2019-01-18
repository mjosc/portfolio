import java.util.HashSet;
import java.util.LinkedList;

public class LRUPolicy implements SwapPolicy {

  // constant time for contains
  private HashSet<Integer> index;
  // constant time for remove
  private LinkedList<Integer> pages;

  int indexSize = 0;

  public LRUPolicy() {
    index = new HashSet<>();
    pages = new LinkedList<>();
  }

  @Override
  public void pageAccessed(int page) {
    if (index.contains(page)) {
      if (pages.peekFirst().equals(page)) {
        return;
      }
      pages.remove((Integer)page);
      pages.addFirst(page);
    } else {
      index.add(page);
      pages.addFirst(page);
      indexSize++;
    }
  }

  @Override
  public int whichPageShouldBeEvicted() {
    int page = pages.removeLast();
    index.remove(page);
    return page;
  }

}
