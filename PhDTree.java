package a4;

import java.util.*;

/** A PhDTree is a tree representing people who have received a PhD degree; each node
 *  represents a person, and the edges represent advisor-advisee relationships, since
 *  PhD students almost always have an advisor who mentors them.
 */
public class PhDTree {
    // You will not need to modify these fields. They are used to help test and
    // print out your output

    /** The String that marks the start of children in toString() */
    public static final String START_ADVISEE_DELIMITER = "[";

    /** The String that divides children in toString() */
    public static final String DELIMITER = ", ";

    /** The String that marks the end of children in toString() */
    public static final String END_ADVISEE_DELIMITER = "]";

    /**
     * The Professor at the root of this PhDTree.
     * i.e. the Professor at this node of this PhDTree.
     * All nodes of a PhDTree have a different Professor in them. The names of
     * professors are all distinct: there are no duplicates.
     */
    private Professor prof;

    /**
     * Year in which this node's professor was awarded their PhD.
     */
    private int phdYear;

    /**
     * The advisees of this PhDTree node.
     * Each element of this set is an advisee of the Professor at this
     * node.  It is the empty set if this node is a leaf. The set of
     * PhDTree nodes reachable via advisees forms a tree.
     */
    private SortedSet<PhDTree> advisees;

    /** Returns false or throws an assertion error if
     *  the class invariant is not satisfied. Requires:
     *  assertion checking is enabled.
     */
    private boolean classInv() {
        Set<Professor> seenProfs = new HashSet<>();
        Set<PhDTree> seenNodes = new HashSet<>();
        return classInvTraverse(seenProfs, seenNodes);
    }

    /**
     * Helper method for classInv. Traverses the tree from this node,
     * adding all Professors and nodes seen to the respective seen sets. Things added must
     * not already be in the set; it would imply the data structure is not a tree. Returns false
     * or throws an assertion error if these conditions are not met.
     */
    private boolean classInvTraverse(Set<Professor> seenProfs, Set<PhDTree> seenNodes) {
        assert !seenProfs.contains(prof) : "prof " + prof + " is not unique";
        assert !seenNodes.contains(this) : "node " + this + " is not unique";
        seenProfs.add(prof);
        seenNodes.add(this);
        for (PhDTree child: advisees) {
            if (!child.classInvTraverse(seenProfs, seenNodes)) return false;
        }
        return true;
    }

    /**
     * Creates: a new PhDTree with root Professor p and no children.
     */
    public PhDTree(Professor p, int year) throws IllegalArgumentException {
        assert p != null;
        prof = p;
        phdYear = year;
        advisees = new TreeSet<>((x, y) -> x.prof.compareTo(y.prof));
        assert classInv();
    }

    /** The Professor at the root of this PhDTree. */
    public Professor prof() {
        return prof;
    }

    /** The number of direct advisees of the professor at the root of the PhDTree. */
    public int numAdvisees() {
        // TODO 1
        classInv();
        return advisees.size();
    }

    /**
     * Returns the number of nodes in this PhDTree.
     * Note: If this is a leaf, the size is 1 (just the root)
     */
    public int size() {
        // TODO 2
        // This method must be recursive.
        // State whether this is a searching or a counting method: counting method
        classInv();
        int sum = 0;
        Iterable<PhDTree> iter = (Iterable<PhDTree>) advisees.iterator();
        for (PhDTree i : iter) {
            sum += i.size();
        }
        sum += 1;
        return sum;
    }

    /**
     * The maximum depth of this PhDTree,
     * i.e. the longest path from the root to a leaf.
     * Example: If this PhDTree has only one node, returns 0.
     */
    public int maxDepth() {
        // TODO 3
        classInv();
        if (numAdvisees() == 0) {
            return 0;
        }
        Iterable<PhDTree> iter = (Iterable<PhDTree>) advisees.iterator();
        SortedSet<Integer> depthlist = new TreeSet<>();
        for (PhDTree i : iter) {
            depthlist.add(1 + i.maxDepth());
        }
        return depthlist.last();

    }

    /**
     * Returns the subtree with p at the root. Throws NotFound
     * if p is not in the tree.
     */
    public PhDTree findTree(Professor p) throws NotFound {
        // TODO 4
        // You will need to use recursion and to catch the NotFound exception.
        classInv();
        try {
            if (this.prof().equals(p)) {
                return this;
            } else if (numAdvisees() == 0) {
                throw new NotFound();
            }

            Iterable<PhDTree> iter = (Iterable<PhDTree>) advisees.iterator();
            for (PhDTree i : iter) {
                i.classInv();
                return i.findTree(p);
            }
        } catch (NotFound e) {
            System.out.println("Not found!");
            throw new NotFound();
        }
        return null;
    }

    /** Returns true if this PhDTree contains a node with Professor p. */
    public boolean contains(Professor p) {
        try {
            findTree(p);
            return true;
        } catch (NotFound exc) {
            return false;
        }
    }
    /**
     * Effect: Extend the tree rooted at Professor p with a new node for
     * the new advisee, Professor a, who received their PhD in the year
     * year.
     * Checks: p is in this PhDTree, and a is not already in this PhDTree.
     */
    public void insert(Professor p, Professor a, int year) {
        // TODO 5
        // This method should not be recursive.
        // Use method findTree(), above, and use no methods that are below.
        // DO NOT traverse the tree twice looking for the same professor
        // --don't duplicate work.
        assert classInv();
        assert contains(p);
        assert !contains(p);

        try {
            PhDTree aTree = new PhDTree(a, year);
            PhDTree pProf = findTree(p);
            pProf.advisees.add(aTree);

        } catch (NotFound exc) {

        }
        assert classInv();
    }

    /**
     * Returns the immediate advisor of p, or throws NotFound if
     * p is not a descendant of the root node of this tree.
     */
    public Professor findAdvisor(Professor p) throws NotFound {
        // TODO 6
        // try catch will never be caught
        classInv();
        try {
            if (!contains(p) || p.equals(prof)) {
                throw new NotFound();
            }
            Iterable<PhDTree> iter = (Iterable<PhDTree>) advisees.iterator();
            for (PhDTree i : iter) {
                i.classInv();
                if (i.prof.equals(p)) {
                    return prof;
                } else {
                    return i.findAdvisor(p);
                }
            }
        } catch (NotFound e){
            System.out.println("Not found");
            throw new NotFound();
        }
        return null;
    }


    /**
     * Returns: The path between "here" (the root of this PhDTree) to
     * professor descendant p. Throws NotFound if there is no such path.
     */
    public List<Professor> findAcademicLineage(Professor p) throws NotFound {
        // TODO 7
        List<Professor> l = new ArrayList<>();
        if (!contains(p)) {
            throw new NotFound();
        } else if (this.prof().equals(p)) {
            ArrayList<Professor> e = new ArrayList();
            e.add(this.prof());
            return e;
        }

        Iterable<PhDTree> iter = (Iterable<PhDTree>) advisees.iterator();
        for (PhDTree i : iter) {
            i.classInv();
            if (i.contains(p)) {
                l.add(i.prof);
                l.addAll(findAcademicLineage(p));
                return l;
            }
        }
        return null;
    }

    /**
     * Returns: The professor at the root of the smallest subtree of
     * this PhDTree that contains prof1 and prof2, if such a subtree
     * exists. Otherwise, throws NotFound.
     */
    public Professor commonAncestor(Professor prof1, Professor prof2) throws NotFound {
        // TODO 8
        classInv();
        try{
            if (!contains(prof1) || !contains(prof2)){
                throw new NotFound();

            } else {
                List<Professor> prof1Path = findAcademicLineage(prof1);
                List<Professor> prof2Path = findAcademicLineage(prof2);
                List<Professor> commonProfs = new ArrayList<>();

                for (Professor i: prof1Path){
                    for (Professor j: prof2Path){
                        if (i.equals(j)){commonProfs.add(j);}
                    }
                }
                classInv();
                return commonProfs.get(commonProfs.size()-1);
            }
        } catch (NotFound e){
            System.out.println("Not found");
            throw new NotFound();
        }
    }

    /**
     * Return a (single line) String representation of this PhDTree.
     * If this PhDTree has no advisees (it is a leaf), return the root's
     * substring.
     * Otherwise, return
     * ... root's substring + START_ADVISEE_DELIMITER + each
     * advisees's toString, separated by DELIMITER, followed by
     * END_ADVISEE_DELIMITER.
     *
     * Thus, for the following tree:
     *
     * <pre>
     * Depth:
     *   0      Maya_Leong
     *            /     \
     *   1 Matthew_Hui  Curran_Muhlberger 
     *           /          /         \
     *   2 Amy_Huang    Tomer_Shamir   Andrew_Myers  
     *           \
     *   3    David_Gries
     *
     * Maya_Leong.toString() should print:
     * Maya Leong[Matthew Hui[Amy Huang[David Gries]]],Curran Muhlberger[Tomer Shamir,Andrew Myers]]
     *
     * Matthew_Hui.toString() should print:
     * Matthew Hui[Amy Huang[David Gries]]
     *
     * Andrew_Myers.toString() should print:
     * Andrew Myers
     * </pre>
     */
    @Override
    public String toString() {
        if (advisees.isEmpty())
            return prof.toString();
        StringBuilder s = new StringBuilder();
        s.append(prof.toString())
         .append(START_ADVISEE_DELIMITER);
        boolean first = true;
        for (PhDTree dt : advisees) {
            if (!first) s.append(DELIMITER);
            first = false;
            s.append(dt.toString());
        }
        s.append(END_ADVISEE_DELIMITER);
        return s.toString();
    }

    /**
     * Return a verbose (multi-line) string representing this PhDTree with
     * the professors [first name last name - year].
     * Each professor in the tree is on its own line (there are no spaces at the
     * beginning or end of each new line)
     * Each line is terminated by a newline character ('\n').
     */
    public String toStringVerbose() {
        StringBuilder s = new StringBuilder();
        // TODO 9
        // Use a StringBuilder to implement this method (Hint: look at toString)
        if (advisees.isEmpty()) {
            return s.append(prof() + " - " + phdYear + "\n").toString();
        }
        Iterable<PhDTree> iter = (Iterable<PhDTree>) advisees.iterator();
        for (PhDTree i : iter) {
            return s.append(prof() + " - " + phdYear + "\n" + i.toStringVerbose()).toString();
        }
        return s.toString();
    }

}