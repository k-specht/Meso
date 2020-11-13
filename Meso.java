import java.lang.reflect.Array;
import java.util.Random;

/** Meso
 *  A small class for multi-threaded MergeSort of any objects that implement the Comparable interface.
 *  Tip:  Use Meso.sort(things) to sort Comparable things.
 *  Note: Default behavior of the sort() function uses 4 threads, but you can use the overload to specify more.
 *  @author Käthe Specht
 *  @version 1.2 - 11/11/2020
 */
public class Meso
{
    /**
     *  Entry point into the MergeSort testing program. Use MergeSort.sort(things) to sort things.
     *  @param args - The command line arguments to the program (ignored by MergeSort for now).
     */
    public static void main(String[] args)
    {
        // Generate input
        Random gen = new Random();
        Double[] input = new Double[10000000];

        for (int i = 10000000; i > 0; i--)
            input[10000000 - i] = gen.nextDouble() * 10000000 - 5000000; //i; // Reversed 1-n

        // Run without additional threads and calculate time
        long startTime = System.currentTimeMillis();

        Meso.sort(input, 0);

        long endTime   = System.currentTimeMillis();
        double runTime = (double)(endTime - startTime) / 1000;

        // Run with 4 threads (2^2 = 4)
        startTime = System.currentTimeMillis();
        Meso.sort(input);
        endTime   = System.currentTimeMillis();

        System.out.println("Single-thread Runtime: " + runTime + " seconds.");
        System.out.println("Multi-thread  Runtime: " + Double.toString((double)(endTime - startTime) / 1000) + " seconds.");
    }

    /**
     *  Sorts the sub-array specified recursively with Merge Sort, using 2^threads many threads.
     *  @param <T>      - An object that implements the Comparable interface.
     *  @param inp      - The array to be sorted (a pointer). Must implement the Comparable interface.
     *  @param left     - The start index to sort from.
     *  @param right    - The end index to sort until (must be within array).
     *  @param threads  - The number of desired threads to run on (2^threads).
     *  @throws RuntimeException If the input data type does not implement Comparable.
     */
    public static <T extends Comparable<T>> void sort(T[] inp, int left, int right, long threads)
    {
        // Checks the input object for Comparable implementation at the beginning of each thread
        if ( threads > 0 && !Comparable.class.isAssignableFrom(inp[0].getClass()) )
            throw new RuntimeException("The class " + inp[0].getClass() + " does not implement the Comparable interface and thus cannot be sorted.");
        
        // Calculate midpoint and create new threads
        if ( left < right )
        {
            int mid = (left + right) / 2;

            // Create two new threads for each pass (each handles 1/4th the data)
            if ( threads > 0L )
            {
                Thread mThread  = new Thread(() -> sort(inp, left,    mid  , threads - 1L));
                Thread mThread2 = new Thread(() -> sort(inp, mid + 1, right, threads - 1L));

                mThread.start();
                mThread2.start();

                // Await completion
                try
                {
                    mThread.join();
                    mThread2.join(); 
                }
                catch(InterruptedException e)
                {
                    System.out.println(e.getMessage());
                    mThread.interrupt();
                    mThread2.interrupt();
                }
            }
            else 
            {
                sort(inp, left,    mid  , threads - 1L);
                sort(inp, mid + 1, right, threads - 1L);
            }

            // Merge the results
            merge(inp, left, mid, right);
        }
    }

    /**
     *  Sorts the given array recursively with Merge Sort, using 2^threads many threads.
     *  Overload for sorting entire array with desired number of threads.
     *  @param <T>      - An object that implements the Comparable interface.
     *  @param inp      - The array to be sorted (a pointer). Must implement the Comparable interface.
     *  @param threads  - The number of desired threads to run on (2^threads).
     *  @throws RuntimeException If the input data type does not implement Comparable.
     */
    public static <T extends Comparable<T>> void sort(T[] inp, long threads)
    {
        sort(inp, 0, inp.length - 1, threads);
    }

    /**
     *  Sorts the given array recursively with Merge Sort on four threads (default behavior).
     *  @param <T> - An object that implements the Comparable interface.
     *  @param inp - The array to be sorted (a pointer). Must implement the Comparable interface.
     *  @throws RuntimeException If the input data type does not implement Comparable.
     */
    public static <T extends Comparable<T>> void sort(T[] inp)
    {
        sort(inp, 0, inp.length - 1, 2);
    }

    /**
     *  Merges the two input halves specified by left, mid, and right.
     *  @param <T>   - An object that implements the Comparable interface.
     *  @param inp   - A pointer to the integer array to be modified.
     *  @param left  - The left half's beginning index.
     *  @param mid   - The midpoint between the left and right array halves.
     *  @param right - The right half's beginning index.
     */
    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> void merge(T[] inp, int left, int mid, int right)
    {
        // Create temporary arrays to hold the halves
        T[] tmp  = (T[]) Array.newInstance(inp[0].getClass(), mid   - left + 1);
        T[] tmp2 = (T[]) Array.newInstance(inp[0].getClass(), right - mid     );

        int count  = 0;
        int count2 = 0;
        int count3 = left;

        // Fill the arrays
        System.arraycopy(inp, left,      tmp, 0, tmp.length );
        System.arraycopy(inp, mid + 1,  tmp2, 0, right - mid);

        // Merge them into the original array
        for (; count < tmp.length && count2 < tmp2.length; count3++)
        {
            if ( tmp2[count2].compareTo(tmp[count]) > 0 )
            {
                inp[count3] = tmp[count];
                count++;
            }
            else 
            {
                inp[count3] = tmp2[count2];
                count2++;
            }
        }

        // Fill in remainders
        for (; count < tmp.length; count3++, count++ )
            inp[count3] = tmp[count];

        for (; count2 < tmp2.length; count3++, count2++ )
            inp[count3] = tmp2[count2];
    }

    /**
     *  A helper method for printing arrays.
     *  @param <T> - The class of the array object.
     *  @param arr - The array to be printed.
     */
    public static <T> void printArray(T[] arr)
    {
        StringBuilder build = new StringBuilder();
        build.append("[");

        for (T num : arr) 
            build.append(num + ", ");

        if (build.length() > 1) 
            build.delete(build.length() - 2, build.length());

        build.append("]");

        System.out.println(build.toString());
    }
}