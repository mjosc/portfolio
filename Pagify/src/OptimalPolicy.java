import java.util.ArrayList;

public class OptimalPolicy implements SwapPolicy {

  private ArrayList<Integer> workload;
  private ArrayList<Integer> pages;
  private int index; // Tracks workload progress

  public OptimalPolicy(ArrayList<Integer> workload) {
    this.workload = workload;
    this.pages = new ArrayList<>();
    this.index = 0;
  }

  @Override
  public void pageAccessed(int page) {
    if (pages.contains(page)) {
      index++;
      return;
    } else {
      pages.add(page);
      index++;
    }
  }

  @Override
  public int whichPageShouldBeEvicted() {
    // TODO: Change data structure of pages to something more efficient (if possible)
    int firstInstanceIndex = -1;
    int page;
    boolean updated = false;
    for (int pIndex = 0; pIndex < pages.size(); pIndex++) {
      page = pages.get(pIndex);
      for (int wIndex = index; wIndex < workload.size(); wIndex++) {
        if (workload.get(wIndex).equals(page) && pIndex > firstInstanceIndex) {
          firstInstanceIndex = pIndex;
          updated = true;
        }
      }
      if (!updated) {
        // page not used for the remainder of the workload
        firstInstanceIndex = pIndex;
        break;
      }
    }
    assert !(firstInstanceIndex < 0);
    page = workload.get(firstInstanceIndex);
    pages.remove((Integer) page);
    return page;
  }

}
