import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.Arrays;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;
public class merges {
    static int[] masterArray = new int[5000000];
    static int[][] arrays = new int[4][masterArray.length];
    public static void main(String[] args) {
        long totalTimeP = 0;
        long totalTimeP1 = 0;
        long totalTimeP2 = 0;
        long totalTimeP3 = 0;
        int iterations = 10; // Number of iterations for averaging
        
        for (int iter = 0; iter < iterations; iter++) {
            long startTimeX = System.currentTimeMillis();
            arrays = generateArrays(arrays[0].length, 0, 10000);
            long endTimeX = System.currentTimeMillis();

            long startTime = System.currentTimeMillis();
            Arrays.sort(arrays[0]);
            long endTime = System.currentTimeMillis();
            long timeP = endTime - startTime;
            totalTimeP += timeP;





            long startTime1 = System.currentTimeMillis();
            Arrays.parallelSort(arrays[1]);
            long endTime1 = System.currentTimeMillis();
            long timeP1 = endTime1 - startTime1;

            totalTimeP1 += timeP1;

            long startTime2 = System.currentTimeMillis();
            arrays[2] = mergesortP1(arrays[2]);
            long endTime2 = System.currentTimeMillis();
            long timeP2 = endTime2 - startTime2;
            totalTimeP2 += timeP2;

        }

        totalTimeP /= iterations;
        totalTimeP1 /= iterations;
        totalTimeP2 /= iterations;
        totalTimeP3 /= iterations;

        System.out.println(Arrays.equals(arrays[0], arrays[1]));
        System.out.println(Arrays.equals(arrays[0], arrays[2]));



        System.out.println("Arrays.sort(): " + (totalTimeP / 1) + " ms");
        System.out.println("Arrays.parallelSort(): " + (totalTimeP1 / 1) + " ms");
        System.out.println("my sort P1: " + (totalTimeP2 / 1) + " ms");
    }


    public static int[][] generateArrays(int size, int min, int max) {

        
        // Create a thread-safe random number generator
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        // Process in larger chunks for better efficiency
        final int CHUNK_SIZE = size/4*28;
        final int numChunks = (size + CHUNK_SIZE - 1) / CHUNK_SIZE;
        
        // Generate random values in parallel by chunk  
        IntStream.range(0, numChunks).parallel().forEach(chunk -> {
            int start = chunk * CHUNK_SIZE;
            int end = Math.min(start + CHUNK_SIZE, size);
            
            // Generate random values for this chunk
    
            for (int i = 0; i < size; i++) {
                masterArray[i] = random.nextInt(10000);
            }
            // Efficient bulk copying
            arrays[0] = masterArray;
            System.arraycopy(masterArray, 0, arrays[1], 0, masterArray.length);
            System.arraycopy(masterArray, 0, arrays[2], 0, masterArray.length);
            return;
            
        });
        
        return arrays;
    }

    public static int[] mergesortP1(int[] data) {
        int[] arr1 = data; 
        int[] arr2 = new int[data.length]; 
        int[] src = arr1;  
        int[] dst = arr2; 
        int m = 2;
        
        while (m < 2 * data.length) {
            int num = (data.length + m - 1) / m;
            final int mFinal = m;
            final int[] srcFinal = src; 
            final int[] dstFinal = dst;
    
            IntStream.range(0, num)
                    .parallel()
                    .forEach(i -> {
                        int lo = i * mFinal;
                        int mid = Math.min(lo + mFinal/2, data.length);
                        int hi = Math.min(lo + mFinal, data.length);
                        
                        if (mid < hi) {
                            merge(srcFinal, dstFinal, lo, mid, hi);
                        } else {
                            for (int j = lo; j < hi; j++) {
                                dstFinal[j] = srcFinal[j];
                            }
                        }
                    });
            
            // Swap source and destination arrays for next iteration
            int[] temp = src;
            src = dst;
            dst = temp;
            
            m *= 2;
        }
        

        return src;
    }
    public static int[] mergesortP2(int[] data) {
        int[] arr1 = data; 
        int[] arr2 = new int[data.length]; 
        int[] src = arr1;  
        int[] dst = arr2; 
        int m = 2;
        
        while (m < 2 * data.length) {
            int num = (data.length + m - 1) / m;
            final int mFinal = m;
            final int[] srcFinal = src; 
            final int[] dstFinal = dst;
    
            IntStream.range(0, num)
                    .parallel()
                    .forEach(i -> {
                        int lo = i * mFinal;
                        int mid = Math.min(lo + mFinal/2, data.length);
                        int hi = Math.min(lo + mFinal, data.length);
                        
                        if (mid < hi) {
                            simdMerge1(srcFinal, dstFinal, lo, mid, hi);
                        } else {
                            for (int j = lo; j < hi; j++) {
                                dstFinal[j] = srcFinal[j];
                            }
                        }
                    });
            
            // Swap source and destination arrays for next iteration
            int[] temp = src;
            src = dst;
            dst = temp;
            
            m *= 2;
        }
        
        // Return the array that contains the final sorted result
        return src;
    }
    private static void simdMerge1(int[] src, int[] dst, int lo, int mid, int hi) {
        int i = lo;
        int j = mid;
        int k = lo;
        
        // Unroll the main comparison loop
        while (i + 3 < mid && j + 3 < hi) {
            // Process 4 elements at once with manual comparisons
            for (int u = 0; u < 4; u++) {
                if (src[i] <= src[j]) {
                    dst[k++] = src[i++];
                } else {
                    dst[k++] = src[j++];
                }
            }
        }
        
        
        // Regular merge for remaining elements
        while (i < mid && j < hi) {
            if (src[i] <= src[j]) {
                dst[k++] = src[i++];
            } else {
                dst[k++] = src[j++];
            }
        }
        
        // Fast System.arraycopy for any remaining chunks
        if (i < mid) {
            System.arraycopy(src, i, dst, k, mid - i);
        } else if (j < hi) {
            System.arraycopy(src, j, dst, k, hi - j);
        }
    }









    public static int[] mergesortM(int[] data) {
        int[] temp = new int[data.length];
        int m = 2;
        boolean osc = true;
        
        while (m < 2 * data.length) {
            int num = (data.length + m - 1) / m;
            final int mFinal = m;  // Need final variable for lambda
            
            if (osc) {
                // Merge from data into temp
                IntStream.range(0, num)
                        .parallel()
                        .forEach(i -> {
                            int lo = i * mFinal;
                            int mid = Math.min(lo + mFinal/2, data.length);
                            int hi = Math.min(lo + mFinal, data.length);
                            
                            if (mid < hi) {
                                merge(data, temp, lo, mid, hi);
                            } else {
    
                                for (int j = lo; j < hi; j++) {
                                    temp[j] = data[j];
                                }
                            }
                        });
            } else {
                IntStream.range(0, num)
                        .parallel()
                        .forEach(i -> {
                            int lo = i * mFinal;
                            int mid = Math.min(lo + mFinal/2, data.length);
                            int hi = Math.min(lo + mFinal, data.length);
                            
                            if (mid < hi) {
                                merge(temp, data, lo, mid, hi);
                            } else {
                                for (int j = lo; j < hi; j++) {
                                    data[j] = temp[j];
                                }
                            }
                        });
            }
            
            m *= 2;
            osc = !osc;
        }
        
        if (!osc) {
            return temp;
        } else {
            return data;
        }
    }



    public static void merge(int[] arr, int[] temp, int left, int mid, int right) {
        int i = left;  
        int j = mid;  
        int k = left;   
    
        while (i < mid && j < right) {
            if (arr[i] <= arr[j]) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }
        while (i < mid) {
            temp[k++] = arr[i++];
        }
        while (j < right) {
            temp[k++] = arr[j++];
        }
    }

    public static void parallelSort(int[] array) {
        // Create a fork-join pool
        ForkJoinPool pool = ForkJoinPool.commonPool();
        
        // Create working copy to avoid allocation during merges
        int[] workingCopy = Arrays.copyOf(array, array.length);
        
        // Create and invoke the sorting task with anonymous RecursiveAction
        pool.invoke(new RecursiveAction() {
            @Override
            protected void compute() {
                sortAndMerge(array, workingCopy, 0, array.length);
            }
            
            // Recursive helper method for sorting and merging
            private void sortAndMerge(int[] arr, int[] work, int start, int end) {
                int length = end - start;
                
                // Base case: for very small arrays, use insertion sort
                if (length <= 16) {
                    insertionSort(arr, start, end);
                    return;
                }
                
                int mid = start + (length / 2);
                
                // Copy to working array
                System.arraycopy(arr, start, work, start, length);
                
                // Create and invoke tasks for both halves
                invokeAll(
                    new RecursiveAction() {
                        @Override protected void compute() { 
                            sortAndMerge(work, arr, start, mid); 
                        }
                    },
                    new RecursiveAction() {
                        @Override protected void compute() { 
                            sortAndMerge(work, arr, mid, end); 
                        }
                    }
                );
                
                // Merge results
                merge(work, arr, start, mid, end);
            }
            
            // Insertion sort for small arrays
            private void insertionSort(int[] arr, int start, int end) {
                for (int i = start + 1; i < end; i++) {
                    int key = arr[i];
                    int j = i - 1;
                    
                    while (j >= start && arr[j] > key) {
                        arr[j + 1] = arr[j];
                        j--;
                    }
                    arr[j + 1] = key;
                }
            }
            
            // Merge method
            private void merge(int[] src, int[] dest, int start, int mid, int end) {
                int i = start, j = mid, k = start;
                
                while (i < mid && j < end) {
                    if (src[i] <= src[j]) {
                        dest[k++] = src[i++];
                    } else {
                        dest[k++] = src[j++];
                    }
                }
                
                while (i < mid) {
                    dest[k++] = src[i++];
                }
                while (j < end) {
                    dest[k++] = src[j++];
                }
            }
        });
    }


    public static void parallelSort2(int[] array) {
        // Create a fork-join pool
        ForkJoinPool pool = ForkJoinPool.commonPool();
        
        // Create working copy to avoid allocation during merges
        int[] workingCopy = Arrays.copyOf(array, array.length);
        
        // Create and invoke the sorting task with anonymous RecursiveAction
        pool.invoke(new RecursiveAction() {
            @Override
            protected void compute() {
                sortAndMerge(array, workingCopy, 0, array.length);
            }
            
            // Recursive helper method for sorting and merging
            private void sortAndMerge(int[] arr, int[] work, int start, int end) {
                int length = end - start;
                
                // Base case: for very small arrays, use insertion sort
                if (length <= 16) {
                    insertionSort(arr, start, end);
                    return;
                }
                
                int mid = start + (length / 2);
                
                // Copy to working array
                System.arraycopy(arr, start, work, start, length);
                
                // Create and invoke tasks for both halves
                invokeAll(
                    new RecursiveAction() {
                        @Override protected void compute() { 
                            sortAndMerge(work, arr, start, mid); 
                        }
                    },
                    new RecursiveAction() {
                        @Override protected void compute() { 
                            sortAndMerge(work, arr, mid, end); 
                        }
                    }
                );
                
                // Merge results
                blockMerge(work, arr, start, mid, end);
            }
            
            // Insertion sort for small arrays
            private void insertionSort(int[] arr, int start, int end) {
                for (int i = start + 1; i < end; i++) {
                    int key = arr[i];
                    int j = i - 1;
                    
                    while (j >= start && arr[j] > key) {
                        arr[j + 1] = arr[j];
                        j--;
                    }
                    arr[j + 1] = key;
                }
            }
            
            // Merge method
            private void blockMerge(int[] src, int[] dest, int start, int mid, int end) {
                final int BLOCK_SIZE = 8192; // Optimal block size depends on cache size
                
                int i = start, j = mid;
                
                // Process by blocks for better cache efficiency
                while (i < mid && j < end) {
                    // Choose which block to merge next
                    if (src[i] <= src[j]) {
                        int blockEnd = Math.min(i + BLOCK_SIZE, mid);
                        while (i < blockEnd && i < mid && (j >= end || src[i] <= src[j])) {
                            dest[i + j - mid] = src[i++];
                        }
                    } else {
                        int blockEnd = Math.min(j + BLOCK_SIZE, end);
                        while (j < blockEnd && j < end && (i >= mid || src[j] < src[i])) {
                            dest[i + j - mid] = src[j++];
                        }
                    }
                }
                
                // Copy remaining elements
                while (i < mid) dest[i + j - mid] = src[i++];
                while (j < end) dest[i + j - mid] = src[j++];
            }
        });
    }



















}