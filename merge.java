import java.util.stream.IntStream;

public class merge{
public static int[] mergesortM(int[] data){
    int[] temp = new int[data.length];
    int m = 2;
    boolean osc = true;

    while(m < 2 * data.length){
        int operations = (data.length + m - 1) / m;
        final int mf = m;

        if(osc){
          IntStream.range(0, operations)
            .parallel()
            .forEach(i -> {
              int lo = i * mf;
              int split = Math.min(lo + mFinal/2, data.length);
              int hi = Math.min(lo + mFinal, data.length);
              if(split < hi){
                merge(data, temp, lo, split, hi);
              }else{
                for(int b=lo; b < hi; b++){
                  temp[b] = data[b];
                }
              }
              });
        }else{ //flip between the two arrays.
          IntStream.range(0, operations)
            .parallel()
            .forEach(i -> {
              int lo = i * mf;
              int split = Math.min(lo + mFinal/2, data.length);
              int hi = Math.min(lo + mFinal, data.length);
              if(split < hi){
                merge(data, temp, lo, split, hi);
              }else{
                for(int b=lo; b < hi; b++){
                  temp[b] = data[b];
                }
              }
              });
        }
        m = m * 2;
        osc = !osc;
    }
    if(osc){
      return data;
    }else{
      return temp;
    }
}

public static void merge(int[] arr, int[] temp, int lo, int split, int hi) {
    int a = lo;
    int b = split;
    int c = lo;
    while(a < split && b < hi){
        if(arr[a] <= arr[b]){
          temp[c] = arr[a];
          a++;
          c++;
        }else{
          temp[c] = arr[b];
            c++;
            b++;
        }
    }
    while(a < split) {
        temp[c] = arr[a];
        c++;
        a++;
    }
    while (b < hi) {
        temp[c] = arr[b];
        c++;
        b++
    }
}




}
