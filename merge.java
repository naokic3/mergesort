import java.util.stream.IntStream;

public class merge{
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




}