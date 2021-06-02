using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace OOP_Exam
{
    public class HeapSort<T>
    {
        public static Collection<T> Sort(Collection<T> collection, Comparer<T> comparer = null)
        {
            int n = collection.Count;
            // Build heap (rearrange array)
            for (int i = n / 2 - 1; i >= 0; i--)
                Heapify(collection, n, i);
            // One by one extract an element from heap
            for (int i = n - 1; i > 0; i--)
            {
                // Move current root to end
                T temp = collection[0];
                collection[0] = collection[i];
                collection[i] = temp;
                // call max heapify on the reduced heap
                Heapify(collection, i, 0);
            }
            return collection;
        }

        private static void Heapify(Collection<T> collection, int n, int i, Comparer<T> comparer = null)
        {
            if (comparer == null)
            {
                comparer = Comparer<T>.Default;
            }
            int largest = i; // Initialize largest as root
            int l = 2 * i + 1; // left = 2*i + 1
            int r = 2 * i + 2; // right = 2*i + 2

            // If left child is larger than root
            if (l < n && comparer.Compare(collection[l], collection[largest]) > 0)
                largest = l;

            // If right child is larger than largest so far
            if (r < n && comparer.Compare(collection[r], collection[largest]) > 0)
                largest = r;

            // If largest is not root
            if (largest != i)
            {
                T swap = collection[i];
                collection[i] = collection[largest];
                collection[largest] = swap;

                // Recursively heapify the affected sub-tree
                Heapify(collection, n, largest);
            }
        }

    }
}