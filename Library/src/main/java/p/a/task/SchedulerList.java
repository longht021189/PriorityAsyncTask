package p.a.task;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Huynh Thanh Long.
 * Date: 7/9/17.
 */
final class SchedulerList extends AbstractList<Contract.Runnable> {

    /** Dummy value to associate with an Object in the backing Map */
    private static final Object PRESENT = new Object();
    /** Contract.Runnable Map */
    private final Map<Contract.Runnable, Object> mSchedulerMap = new HashMap<>();
    /** Contract.Runnable List */
    private final List<Contract.Runnable> mSchedulerList = new ArrayList<>();

    /**
     * Find Insert Position
     * @param scheduler Contract.Runnable
     * @param start     Start
     * @param end       End
     * @return Position
     */
    private int findInsertPosition(Contract.Runnable scheduler, int start, int end) {
        if (start > end) {
            return start;
        }

        int mid = (start + end) / 2;
        Contract.Runnable pivot = mSchedulerList.get(mid);

        if (start == end) {
            if (pivot.getPriority() < scheduler.getPriority()) {
                return start;
            } else {
                return start + 1;
            }
        }
        else {
            if (pivot.getPriority() < scheduler.getPriority()) {
                return findInsertPosition(scheduler, start, mid);
            }
            else {
                return findInsertPosition(scheduler, mid + 1, end);
            }
        }
    }

    /**
     * Find Insert Position
     * @param scheduler Contract.Runnable
     * @return Position
     */
    private int findInsertPosition(Contract.Runnable scheduler, int size) {
        return findInsertPosition(scheduler, 0, size - 1);
    }

    /**
     * Appends the specified element to the end of this list (optional
     * operation).
     * <p>
     * <p>Lists that support this operation may place limitations on what
     * elements may be added to this list.  In particular, some
     * lists will refuse to add null elements, and others will impose
     * restrictions on the type of elements that may be added.  List
     * classes should clearly specify in their documentation any restrictions
     * on what elements may be added.
     * <p>
     * <p>This implementation calls {@code add(size(), e)}.
     * <p>
     * <p>Note that this implementation throws an
     * {@code UnsupportedOperationException} unless
     * {@link #add(int, Object) add(int, E)} is overridden.
     *
     * @param scheduler element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws UnsupportedOperationException if the {@code add} operation
     *                                       is not supported by this list
     * @throws ClassCastException            if the class of the specified element
     *                                       prevents it from being added to this list
     * @throws NullPointerException          if the specified element is null and this
     *                                       list does not permit null elements
     * @throws IllegalArgumentException      if some property of this element
     *                                       prevents it from being added to this list
     */
    @Override
    public boolean add(Contract.Runnable scheduler) {
        if (mSchedulerMap.put(scheduler, PRESENT) != null) {
            return false;
        }

        int size = mSchedulerList.size();
        int index = findInsertPosition(scheduler, size);

        if (index >= size) {
            mSchedulerList.add(scheduler);
        } else {
            mSchedulerList.add(index, scheduler);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param index
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public Contract.Runnable get(int index) {
        return mSchedulerList.get(index);
    }

    /**
     * Returns the number of elements in this list.  If this list contains
     * more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return mSchedulerList.size();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This implementation first gets a list iterator (with
     * {@code listIterator()}).  Then, it iterates over the list until the
     * specified element is found or the end of the list is reached.
     *
     * @param o
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public int indexOf(Object o) {
        return mSchedulerList.indexOf(o);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This implementation always throws an
     * {@code UnsupportedOperationException}.
     *
     * @param index
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws IndexOutOfBoundsException     {@inheritDoc}
     */
    @Override
    public Contract.Runnable remove(int index) {
        Contract.Runnable scheduler = mSchedulerList.remove(index);
        mSchedulerMap.remove(scheduler);
        return scheduler;
    }
}
