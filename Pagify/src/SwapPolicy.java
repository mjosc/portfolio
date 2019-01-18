public interface SwapPolicy {

  void pageAccessed(int page);
  int whichPageShouldBeEvicted();

}
