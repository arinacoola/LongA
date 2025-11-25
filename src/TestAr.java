import java.util.Random;

public class TestAr {
    private static String norm(BigInt x) {
        String s = x.toString().replace("0x", "").replace("0X", "").toLowerCase();
        int i = 0;
        while (i < s.length() - 1 && s.charAt(i) == '0') i++;
        return s.substring(i);
    }

    private static void print(String label, BigInt x) {
        System.out.println(label + " = " + norm(x));
    }

    private static void checkEq(String msg, BigInt a, BigInt b) {
        if (!norm(a).equals(norm(b))) {
            System.out.println("N0! " + msg + ": our result " + norm(a) + " what is expected " + norm(b));
        } else {
            System.out.println("YES! " + msg);
        }
    }

    static BigInt randomBigInt ( int nVal){
        java.util.Random rnd = new java.util.Random();
        BigInt x = new BigInt(nVal);
        for (int i = 0; i < nVal; i++) {
            x.num[i] = rnd.nextInt(0xFFFF);
        }
        return x;
    }

    private static String toBinary(String hex) {
        return new java.math.BigInteger(hex, 16).toString(2);
    }

    public static void main(String[] args) {
        BigInt A = new BigInt(BigInt.n);
        BigInt B = new BigInt(BigInt.n);
        A.num[0] = 0xA5F;
        B.num[0] = 0x1B2;
        BigInt bi = new BigInt(BigInt.n);


        BigInt.AddResult addAB = A.longAdd(B);
        BigInt.SubResult checkSub = addAB.sum.longSub(B);
        checkEq("(A+B)-B = A", checkSub.sub, A);
        System.out.println("hex (A+B)-B = " + norm(checkSub.sub));
        System.out.println("bin (A+B)-B = " + toBinary(norm(checkSub.sub)));


        BigInt C = new BigInt(BigInt.n);
        C.num[0] = 3;
        BigInt AC = bi.longMul(A, C);
        BigInt BC = bi.longMul(B, C);
        BigInt.AddResult ACplusBC = AC.longAdd(BC);
        BigInt.AddResult AplusB = A.longAdd(B);
        BigInt left = bi.longMul(AplusB.sum, C);
        checkEq("C*(A+B) =(A+B)*C= A*C + B*C", left, ACplusBC.sum);
        System.out.println("hex C*(A+B) = " + norm(left));
        System.out.println("hex A*C + B*C = " + norm(ACplusBC.sum));
        System.out.println("bin C*(A+B) = " + toBinary(norm(left)));
        System.out.println("bin A*C + B*C = " + toBinary(norm(ACplusBC.sum)));



        int nTimes = 120;
        BigInt N = new BigInt(BigInt.n);
        N.num[0] = nTimes;
        BigInt left_mul = bi.longMul(A, N);
        BigInt sumMany = BigInt.constZero();
        for (int i = 0; i < nTimes; i++) {
            sumMany = sumMany.longAdd(A).sum;
        }
        checkEq("n*A = A+...+A (n times)", left_mul, sumMany);
        System.out.println("hex n*A = " + norm(left_mul));
        System.out.println("hex A + ... + A = " + norm(sumMany));
        System.out.println("bin n*A = " + toBinary(norm(left_mul)));
        System.out.println("bin A + ... + A = " + toBinary(norm(sumMany)));


        BigInt sq = bi.longSquare(A);
        BigInt mulAA = bi.longMul(A, A);
        checkEq("A^2 = A*A", sq, mulAA);
        System.out.println("hex A^2 = " + norm(sq));
        System.out.println("hex A*A = " + norm(mulAA));
        System.out.println("bin A^2 = " + toBinary(norm(sq)));
        System.out.println("bin A*A = " + toBinary(norm(mulAA)));


        BigInt.DivModResult divAB = bi.longDivMod(A, B);
        BigInt mulBQ = bi.longMul(B, divAB.q);
        BigInt.AddResult mulPlusR = mulBQ.longAdd(divAB.r);
        checkEq("A = B*Q + R", mulPlusR.sum, A);
        System.out.println("hex B*Q + R = " + norm(mulPlusR.sum));
        System.out.println("bin B*Q + R = " + toBinary(norm(mulPlusR.sum)));

        System.out.println("\nResult:");
        print("A", A);
        print("B", B);
        print("A + B", addAB.sum);

        BigInt.SubResult diffAB = A.longSub(B);
        print("A - B", diffAB.sub);

        print("A * B", bi.longMul(A, B));

        print("A / B", divAB.q);
        print("A mod B", divAB.r);

        print("A^2", sq);



        int launchOp = 10000;
        long tAdd = 0, tSub = 0, tMul = 0, tSq = 0, tDiv = 0;
        BigInt tmp;
        Random rnd = new java.util.Random();
        for (int i = 0; i < launchOp; i++) {
            A = randomBigInt(BigInt.n);
            B = randomBigInt(BigInt.n);
            long t0 = System.nanoTime();
            tmp = A.longAdd(B).sum;
            long t1 = System.nanoTime();
            tAdd += (t1 - t0);
        }

        for (int i = 0; i < launchOp; i++) {
            A = randomBigInt(BigInt.n);
            B = randomBigInt(BigInt.n);
            long t0 = System.nanoTime();
            tmp = A.longSub(B).sub;
            long t1 = System.nanoTime();
            tSub += (t1 - t0);
        }

        for (int i = 0; i < launchOp; i++) {
            A = randomBigInt(BigInt.n);
            B = randomBigInt(BigInt.n);
            long t0 = System.nanoTime();
            tmp = bi.longMul(A, B);
            long t1 = System.nanoTime();
            tMul += (t1 - t0);
        }

        for (int i = 0; i < launchOp; i++) {
            A = randomBigInt(BigInt.n);
            long t0 = System.nanoTime();
            tmp = bi.longSquare(A);
            long t1 = System.nanoTime();
            tSq += (t1 - t0);
        }

        for (int i = 0; i < launchOp; i++) {
            A = randomBigInt(BigInt.n);
            B = randomBigInt(BigInt.n);
            long t0 = System.nanoTime();
            BigInt.DivModResult r = bi.longDivMod(A, B);
            long t1 = System.nanoTime();
            tDiv += (t1 - t0);
        }

        System.out.println("\nTime measurements with random data (avg ns): ");
        System.out.printf("Add:     %.2f ns%n", tAdd / (double) launchOp);
        System.out.printf("Sub:     %.2f ns%n", tSub / (double) launchOp);
        System.out.printf("Mul:     %.2f ns%n", tMul / (double) launchOp);
        System.out.printf("Square:  %.2f ns%n", tSq / (double) launchOp);
        System.out.printf("DivMod:  %.2f ns%n", tDiv / (double) launchOp);
    }
}
