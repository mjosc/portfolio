import java.util.ArrayList;

/*
 * A directed acyclic graph structure representing a family tree.
 */
public class FamilyTree {

  /*
   * Represents the maximum number of biological parents. This is no longer a perfectly accurate representation given
   * the 2016 birth of the first baby with three genetic parents (https://goo.gl/rsjvqU).
   */
  private static final int MAX_NUM_PARENTS = 2;

  private Individual mRoot;

  public FamilyTree() {
    mRoot = new Individual();
  }

  private class Individual {

    private String mFirstName;
    private String mLastName;

    private Partnership mPartnerships[];

    /*
     * TODO: Should the child point directly to the parents or to the partnership from which they were conceived?
     *
     * Currently, the latter option seems more consistent with a relational model. This should make it easier to find
     * siblings and other related information.
     */
    private Partnership mParents = new Partnership();

    private boolean addFather(Individual individual) {
      return false;
    }

    private boolean addMother(Individual individual) {
      return false;
    }

  }

  private class Partnership {

    private ArrayList<Individual> mPartners = new ArrayList<>(MAX_NUM_PARENTS);
    /*
     * The average number of children per household appears to be approximately 3 to 5 over the past 200 years
     * (according to various resources found via a simple Google search). The ArrayList's default initial capacity of
     * 10 should be a sufficient balance of performance and memory consumption for the number of generations stored in
     * the average family tree. Of course, this is a rough estimatation and if optimization turns out to be
     * necessary, either the initial capacity can be adjusted or an alternative to the ArrayList may be sought.
     */
    private ArrayList<Individual> mChildren = new ArrayList<>(); // Default = 10

    public boolean addPartner(Individual individual) {

      /*
       * TODO: Would it make more sense to throw an exception or simply return false?
       *
       * This is entirely dependent on how I would like to interface with the gedcom4j library.
       */
      if (mPartners.size() >= MAX_NUM_PARENTS) {
        return false;
      }

      mPartners.add(individual);

      return true;
    }
  }
}
