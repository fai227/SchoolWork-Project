import java.math.BigInteger;
import java.util.Scanner;

public class two {

  public static final int number = 10000000;
  public static int[] c = new int[number];
  public static int cnt = 1;

  public static void main(String[] args){
    Scanner stdIn = new Scanner(System.in);

    for (int i = 0; i < c.length; i++) {
      for (int j = 0; j < 2; j++) {
        c[i] = 0;
      }
    }

    c[0] = 2;

    ///実行時間表示用///
    long start = System.currentTimeMillis();
    long dcnt = 0;
    long curtime = 0;

    ///素数計算///
    int num = 2;
    int maxp = 2;

    for (num = 3; num < 216100; num += 2) {
      ///実行時間表示用///
      curtime = System.currentTimeMillis();
      if (((curtime - start) / 1000) % 10 == 0 && dcnt != (curtime - start) / 1000) {
        dcnt = (curtime - start) / 1000;
        System.out.println(dcnt + "秒経過");
      }

      isPrime(num);
    }

    for (int i = 0; i < 10; i++) {
      System.out.println(c[i]);
    }

    int check_max = 0;
    while (true) {
       if(c[check_max] == 0){
        maxp = c[check_max - 1];
        break;
      }
      check_max++;
    }


    ///結果表示用///
    System.out.println("素数の個数：" + cnt);
    System.out.println("最大の素数：" + maxp);
    System.out.println("経過時間：" + (curtime - start) / 1000.0 + "秒");

    start = System.currentTimeMillis();

    int maxp_n = 0;

    for (int i = c.length - 1; i >= 0; i--) {

      curtime = System.currentTimeMillis();
      if (((curtime - start) / 1000) % 10 == 0 && dcnt != (curtime - start) / 1000) {
        dcnt = (curtime - start) / 1000;
        System.out.println(dcnt + "秒経過");
      }

      if (c[i] != 0 && isPrimeLucas(c[i])) {
        maxp_n = c[i];
        break;
      }
    }

    System.out.println("経過時間：" + (curtime - start) / 1000.0 + "秒");
    System.out.println("見つかった素数は、n^(" + maxp_n + ") - 1です。");

    int ketasu = (int)(maxp_n * 0.30102999566);
    ketasu++;
    System.out.println("桁数は、" + ketasu + "桁です。");

    System.out.println("素数を表示しますか？ Yes:1, No:0");

    int x = stdIn.nextInt();
    if (x == 1) {
      BigInteger m = BigInteger.ONE;
      for (int i = 0; i < maxp_n; i++) {
        m = m.multiply(BigInteger.valueOf(2));
      }
      m = m.add(BigInteger.valueOf(-1));
      System.out.println(m.toString());
    }

  }

  public static boolean isPrimeLucas(int num){
    BigInteger n = BigInteger.valueOf(4);
    int devide = num;
    BigInteger m = BigInteger.ONE;
    for (int i = 0; i < devide; i++) {
      m = m.multiply(BigInteger.valueOf(2));
    }
    m = m.add(BigInteger.valueOf(-1));

    for (int i = 2; i < devide; i++) {
      n = n.pow(2);
      n = n.add(BigInteger.valueOf(-2));
      n = n.mod(m);
    }

    if (n.compareTo(BigInteger.ZERO) == 0) {
      return true;
    }


    return false;
  }

  public static void isPrime(int num){
    boolean checker = true;
    for (int i = 0; c[i] != 0; i++) {

      if (num % c[i] == 0) {
        checker = false;
        break;
      }

      if (num < c[i] * c[i]) {
        break;
      }

    }


    if (checker) {
      c[cnt] = num;
      cnt++;
    }

  }
}
