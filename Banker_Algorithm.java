/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package os2;
// 20140291                 20140211
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
 
/**
 *
 * @author darklife
 */
public class Os2 {

    public static final int N = 10;
    public static int[] available = new int[N];
    public static int[][] maximum = new int[N][N];
    public static int[][] allocation = new int[N][N];
    public static int[][] need = new int[N][N];
    public static int n = 0, m = 0;
    // n is the proccess number
    // m is the resources

    /**
     * @param args the command line arguments
     */
    public static void states() {
        System.out.println("System current state is Safe.");
        System.out.println("Available:");
        for (int i = 0; i < m; i++) {
            System.out.print(available[i] + " ");
        }
        System.out.println("\nAllocation:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.print(allocation[i][j] + " ");
            }
            System.out.println();
        }
        //Need
        System.out.println("Need:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.print(maximum[i][j] - allocation[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("Maximum Need:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.print(maximum[i][j] + " ");
            }
            System.out.println();
        }

    }

    public static Boolean banker() {
        int[] dumavailable = new int[N];
        for (int i = 0; i < n; i++) {
            dumavailable[i] = available[i];
            for (int j = 0; j < m; j++) {
                need[i][j] = maximum[i][j] - allocation[i][j];
                
            }
        }
        
        
        int lastmask = 0;
        int mask = 0;
        Boolean state = true;
        while (true) {
            lastmask = mask;
            //System.out.println(mask +" "+lastmask);
            for (int i = 0; i < n; i++) {
                if (0 == ((mask >> i) & 1)) {
                    int flag = 1;
                    for (int j = 0; j < m; j++) {
                        if (dumavailable[j] < need[i][j]) {
                            flag = 0;
                            break;
                        }
                    }
                    if (flag == 1) {
                        mask |= (1 << i);// 2^i
                        for (int j = 0; j < m; j++) {
                            dumavailable[j] += allocation[i][j];

                        }
                    }
                }
            }
            if (mask == ((1 << n) - 1)) {
                state = true;
                break;
            }
            if (lastmask == mask) {
                state = false;
                break;
            }
        }
        return state;

    }

    public static Void Request(int prcno, Vector<Integer> res) {
        for (int i = 0; i < res.size(); i++) {
            if (res.get(i) > available[i]) {
                System.out.println("Request more resources than available");
                return null;
            } else if (res.get(i) + allocation[prcno][i] > maximum[prcno][i]) {
                System.out.println("Request more resources than the process claim");
                return null;
            }
        }
        for (int i = 0; i < res.size(); i++) {
            available[i] -= res.get(i);
            allocation[prcno][i] += res.get(i);
        }
        if (!banker()) {
            for (int i = 0; i < res.size(); i++) {
                available[i] += res.get(i);
                allocation[prcno][i] -= res.get(i);
            }
            System.out.println("Request resources that lead to unsafe state");
            return null;
        }
        // request granted
        System.out.println("request granted");
        states();
        return null;

    }

    public static Void Release(int prcno, Vector<Integer> res) {

        for (int i = 0; i < res.size(); i++) {
            if (res.get(i) > allocation[prcno][i]) {
                System.out.println("Release more resources than acquired by the process");
                return null;
            }
        }
        for (int i = 0; i < res.size(); i++) {
            allocation[prcno][i] -= res.get(i);
            available[i] += res.get(i);
        }
        //banker();
        states();
        return null;

    }

    public static void readFile() {
        try (Scanner buffer = new Scanner(new FileReader("C:\\Users\\darklife\\Desktop\\input.txt"))) {
            n = buffer.nextInt();
            m = buffer.nextInt();
            for (int j = 0; j < m; j++) {
                available[j] = buffer.nextInt();
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    maximum[i][j] = buffer.nextInt();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        readFile();
        String str = null;
        Scanner sc = new Scanner(System.in);
        String parts[] = null;
        int x;
        while (true) {
            str = sc.nextLine();
            parts = str.split(" ");
            if (parts[0].equals("rl")) {
                Vector<Integer> tmp = new Vector<Integer>();
                int prcno = Integer.parseInt(parts[1]);
                if (prcno >= n) {
                    System.out.println("Requested proccess not found in system");
                } else {
                    for (int i = 2; i < 2 + m; i++) {
                        x = Integer.parseInt(parts[i]);
                        tmp.add(x);
                    }
                    Release(prcno, tmp);
                }

            } else if (parts[0].equals("rq")) {
                Vector<Integer> tmp = new Vector<Integer>();
                int prcno = Integer.parseInt(parts[1]);
                if (prcno > n) {
                    System.out.println("Requested proccess not found in system");
                } else {
                    for (int i = 2; i < 2 + m; i++) {
                        x = Integer.parseInt(parts[i]);
                        tmp.add(x);
                    }
                    Request(prcno, tmp);
                }
            } else if (parts[0].equals("quit")) {
                return;
            } else if (parts[0].equals("st")) {
                states();
            }
        }
    }

}
