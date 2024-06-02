// Java code for the above approach
public class Shannon {
    // Node class to store each symbol along with its probability and code
    static class Node {
        char sym; // symbol
        double pro; // probability
        int[] arr = new int[20]; // array to store the code
        int top; // top of the stack
    }

    // Function to calculate Shannon code
    static void shannon(int l, int h, Node[] p) {
        double pack1 = 0, pack2 = 0, diff1 = 0, diff2 = 0;
        int i, d, k = 0, j; // Initialize k here
        if ((l + 1) == h || l == h || l > h) {
            if (l == h || l > h)
                return;
            p[h].arr[++(p[h].top)] = 0;
            p[l].arr[++(p[l].top)] = 1;
            return;
        } else {
            for (i = l; i <= h - 1; i++)
                pack1 = pack1 + p[i].pro;
            pack2 = pack2 + p[h].pro;
            diff1 = pack1 - pack2;
            if (diff1 < 0)
                diff1 = diff1 * -1;
            j = 2;
            while (j != h - l + 1) {
                k = h - j;
                pack1 = pack2 = 0;
                for (i = l; i <= k; i++)
                    pack1 = pack1 + p[i].pro;
                for (i = h; i > k; i--)
                    pack2 = pack2 + p[i].pro;
                diff2 = pack1 - pack2;
                if (diff2 < 0)
                    diff2 = diff2 * -1;
                if (diff2 >= diff1)
                    break;
                diff1 = diff2;
                j++;
            }
            k++;
            for (i = l; i <= k; i++)
                p[i].arr[++(p[i].top)] = 1;
            for (i = k + 1; i <= h; i++)
                p[i].arr[++(p[i].top)] = 0;
            shannon(l, k, p);
            shannon(k + 1, h, p);
        }
    }

    // Function to sort the symbols based on their probability
    static void sortByProbability(int n, Node[] p) {
        int i, j;
        Node temp = new Node();
        for (j = 1; j <= n - 1; j++) {
            for (i = 0; i < n - 1; i++) {
                if ((p[i].pro) > (p[i + 1].pro)) {
                    temp = p[i];
                    p[i] = p[i + 1];
                    p[i + 1] = temp;
                }
            }
        }
    }

    // Function to display the symbols along with their probability and code
    static void display(int n, Node[] p) {
        int i, j;
        System.out.println("\n\n\n  Symbol  Probability Code");
        for (i = n - 1; i >= 0; i--) {
            System.out.print(p[i].sym + "   " + p[i].pro + "   ");
            for (j = 0; j <= p[i].top; j++)
                System.out.print(p[i].arr[j]);
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int n = 5;
        Node[] p = new Node[n];
        double total = 0;
        for (int i = 0; i < n; i++) {
            p[i] = new Node();
            p[i].sym = (char) (65 + i);
            p[i].pro = new double[] { 0.22, 0.28, 0.15, 0.30, 0.05 }[i];
            total = total + p[i].pro;
            if (total > 1) {
                System.out.println("Invalid. Enter new values");
                total = total - p[i].pro;
                i--;
            }
        }
        p[n - 1].pro = 1 - total;
        sortByProbability(n, p);
        for (int i = 0; i < n; i++)
            p[i].top = -1;
        shannon(0, n - 1, p);
        display(n, p);
    }
}
