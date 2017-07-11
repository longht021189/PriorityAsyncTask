package p.a.task;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Huynh Thanh Long.
 * Date: 7/9/17.
 */

public class SchedulerListTest {

    @Test
    public void checkOneItem() throws Exception {
        SchedulerList list = new SchedulerList();
        assertTrue(list.add(new MyScheduler(1)));
        assertEquals(1, list.size());
    }

    @Test
    public void checkAddTwoTimesWithOneItem() throws Exception {
        SchedulerList list = new SchedulerList();
        Scheduler scheduler = new MyScheduler(1);

        assertTrue(list.add(scheduler));
        assertFalse(list.add(scheduler));
        assertEquals(1, list.size());
    }

    @Test
    public void checkSamePriorityList() throws Exception {
        final int size = 1000;
        final List<Scheduler> array = new ArrayList<>();
        final SchedulerList list = new SchedulerList();

        for (int i = 0; i < size; ++i) {
            Scheduler item = new MyScheduler(1);

            array.add(item);
            assertTrue(list.add(item));
        }

        assertEquals(size, list.size());

        for (int i = 0; i < size; ++i) {
            assertEquals(i, list.indexOf(array.get(i)));
        }
    }

    @Test
    public void checkDecPriorityList() throws Exception {
        final int size = 1000;
        final List<Scheduler> array = new ArrayList<>();
        final SchedulerList list = new SchedulerList();

        for (int i = 0; i < size; ++i) {
            Scheduler item = new MyScheduler(size - i - 1);

            array.add(item);
            assertTrue(list.add(item));
        }

        assertEquals(size, list.size());

        for (int i = 0; i < size; ++i) {
            assertEquals(i, list.indexOf(array.get(i)));
        }
    }

    @Test
    public void checkIncPriorityList() throws Exception {
        final int size = 1000;
        final List<Scheduler> array = new ArrayList<>();
        final SchedulerList list = new SchedulerList();

        for (int i = 0; i < size; ++i) {
            Scheduler item = new MyScheduler(i);

            array.add(item);
            assertTrue(list.add(item));
        }

        assertEquals(size, list.size());

        for (int i = 0; i < size; ++i) {
            assertEquals(size - i - 1, list.indexOf(array.get(i)));
        }
    }

    @Test
    public void compareWithSortedList() throws Exception {
        final int size = 10000;
        final Random random = new Random();

        final SchedulerList schedulerList = new SchedulerList();
        final SortedList<Scheduler> sortedList = new MySortedList();

        for (int i = 0; i < size; ++i) {
            int priority = random.nextInt(Integer.MAX_VALUE);
            Scheduler scheduler = new MyScheduler(priority);

            schedulerList.add(scheduler);
            int index = sortedList.add(scheduler);

            assertTrue(index >= 0);
            assertEquals(index, schedulerList.indexOf(scheduler));
        }
    }

    @Test
    public void compareTimeWithSortedList() throws Exception {
        final int size = 100000;
        final Random random = new Random();
        final List<Scheduler> list = new ArrayList<>();

        for (int i = 0; i < size; ++i) {
            int priority = random.nextInt(Integer.MAX_VALUE);
            final Scheduler scheduler = new MyScheduler(priority);
            list.add(scheduler);
        }

        final SchedulerList schedulerList = new SchedulerList();
        final SortedList<Scheduler> sortedList = new MySortedList();

        long startTime = System.currentTimeMillis();
        for (Scheduler scheduler : list) {
            sortedList.add(scheduler);
        }
        long sortedListTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        for (Scheduler scheduler : list) {
            schedulerList.add(scheduler);
        }
        long schedulerListTime = System.currentTimeMillis() - startTime;

        assertTrue(schedulerListTime <= sortedListTime);
    }

    /**
     * Implement Self Scheduler
     */
    private static class MyScheduler extends Scheduler {
        /** Self Priority */
        final int mPrioriry;

        /**
         * Constructor
         * @param priority Self Priority
         */
        MyScheduler(int priority) {
            mPrioriry = priority;
        }

        /**
         * Runs before {@link #run()}.
         */
        @Override
        public void onPreExecute() {

        }

        /**
         * Runs after {@link #run()}
         */
        @Override
        public void onPostExecute() {

        }

        /**
         * Return Priority for Scheduler
         *
         * @return Integer, must constant, because it's calculated 1 time
         * When it's execute by {@link Manager}
         */
        @Override
        public int getPriority() {
            return mPrioriry;
        }
    }

    /**
     * Implement Sorted List
     * To Compare with Scheduler List
     */
    private static class MySortedList extends SortedList<Scheduler> {

        /**
         * Creates a new SortedList of type T.
         */
        MySortedList() {
            super(Scheduler.class, new MySortedListCallback());
        }
    }

    /**
     * Implement Sorted List Callback
     */
    private static class MySortedListCallback extends SortedList.Callback<Scheduler> {

        /**
         * Similar to {@link Comparator#compare(Object, Object)}, should compare two and
         * return how they should be ordered.
         *
         * @param o1 The first object to compare.
         * @param o2 The second object to compare.
         * @return a negative integer, zero, or a positive integer as the
         * first argument is less than, equal to, or greater than the
         * second.
         */
        @Override
        public int compare(Scheduler o1, Scheduler o2) {
            return o2.getPriority() - o1.getPriority();
        }

        /**
         * Called by the SortedList when the item at the given position is updated.
         *
         * @param position The position of the item which has been updated.
         * @param count    The number of items which has changed.
         */
        @Override
        public void onChanged(int position, int count) {

        }

        /**
         * Called by the SortedList when it wants to check whether two items have the same data
         * or not. SortedList uses this information to decide whether it should call
         * {@link #onChanged(int, int)} or not.
         * <p>
         * SortedList uses this method to check equality instead of {@link Object#equals(Object)}
         * so
         * that you can change its behavior depending on your UI.
         * <p>
         * For example, if you are using SortedList with a {@link RecyclerView.Adapter
         * RecyclerView.Adapter}, you should
         * return whether the items' visual representations are the same or not.
         *
         * @param oldItem The previous representation of the object.
         * @param newItem The new object that replaces the previous one.
         * @return True if the contents of the items are the same or false if they are different.
         */
        @Override
        public boolean areContentsTheSame(Scheduler oldItem, Scheduler newItem) {
            return oldItem == newItem;
        }

        /**
         * Called by the SortedList to decide whether two object represent the same Item or not.
         * <p>
         * For example, if your items have unique ids, this method should check their equality.
         *
         * @param item1 The first item to check.
         * @param item2 The second item to check.
         * @return True if the two items represent the same object or false if they are different.
         */
        @Override
        public boolean areItemsTheSame(Scheduler item1, Scheduler item2) {
            return item1 == item2;
        }

        /**
         * Called when {@code count} number of items are inserted at the given position.
         *
         * @param position The position of the new item.
         * @param count    The number of items that have been added.
         */
        @Override
        public void onInserted(int position, int count) {

        }

        /**
         * Called when {@code count} number of items are removed from the given position.
         *
         * @param position The position of the item which has been removed.
         * @param count    The number of items which have been removed.
         */
        @Override
        public void onRemoved(int position, int count) {

        }

        /**
         * Called when an item changes its position in the list.
         *
         * @param fromPosition The previous position of the item before the move.
         * @param toPosition   The new position of the item.
         */
        @Override
        public void onMoved(int fromPosition, int toPosition) {

        }
    }
}
