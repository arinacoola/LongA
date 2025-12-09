import java.math.BigInteger;
import java.util.Random;

public class TestBigInt {
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


        BigInt N1 = new BigInt(BigInt.n);
        N1.num[0]=0xD;
        BigInt muN = bi.mu(N1);

        BigInt AplusB1 = bi.modAdd(A, B, N1, muN);
        BigInt left1 = bi.modMul(AplusB1, C, N1, muN);
        BigInt mid1 = bi.modMul(C, AplusB1, N1, muN);
        BigInt AC1 = bi.modMul(A, C, N1, muN);
        BigInt BC1 = bi.modMul(B, C, N1, muN);
        BigInt right1= bi.modAdd(AC1, BC1, N1, muN);
        checkEq("(A+B)*C =C*(A+B)", left1,mid1);
        checkEq("(A+B)*C= A*C + B*C mod N",left1,right1);

        int times = 120;
        BigInt M = new BigInt(BigInt.n);
        M.num[0] = 0xD;
        BigInt Ntimes = new BigInt(BigInt.n);
        Ntimes.num[0] = times;
        BigInt left2 = bi.modMul(Ntimes, A, M,muN);
        BigInt right2 = BigInt.constZero();
        for (int i = 0; i < times; i++) {
            right2 = bi.modAdd(right2, A, M,muN);
        }
        checkEq("n*A ≡ A+...+A mod M(n times)", left2, right2);

        int k = 5;
        BigInt nForPhi = new BigInt(BigInt.n);
        nForPhi.num[0] = (int) Math.pow(3, k);
        BigInt phi = new BigInt(BigInt.n);
        phi.num[0] = 2 * (int) Math.pow(3, k - 1);
        BigInt A1 = new BigInt(BigInt.n);
        A1.num[0] = 0x25;
        BigInt g = bi.gcdSteyn(A1, nForPhi);
        if (bi.longCmp(g, BigInt.constOne()) != 0) {
            System.out.println("gcd(a, n) != 1");
        }
        else {
            BigInt result = bi.longModPowerBarrett(A1, phi, nForPhi,muN);
            BigInt expected = new BigInt(BigInt.n);
            expected.num[0] = 0x1;
            checkEq("a^phi(n) mod n = 1", result, expected);

        }

        BigInt gcdAB = bi.gcdSteyn(A, B);
        BigInt trueGCD=new BigInt(BigInt.n);
        trueGCD.num[0]=1;
        checkEq("gcd(A, B)", gcdAB,trueGCD);
        BigInt lcmAB=bi.lcm(A,B);
        BigInt trueLCM=new BigInt(BigInt.n);
        trueLCM.num[0]=0x11950E;
        checkEq("lcm(A,B)",lcmAB,trueLCM);

        BigInt X = new BigInt(BigInt.n);
        X.num[0] = 0xABCDE;
        BigInt nBar = new BigInt(BigInt.n);
        nBar.num[0] = 0xD;
        BigInt mu = bi.mu(nBar);
        BigInt barrettResult = bi.barrettRedc(X, nBar, mu);
        BigInteger javaX = new BigInteger("ABCDE", 16);
        BigInteger javaN = new BigInteger("D", 16);
        BigInt expected = new BigInt(BigInt.n);
        expected.num[0] = javaX.mod(javaN).intValue();
        checkEq("barrettRedc(x,n)", barrettResult, expected);



        BigInt powerRes = bi.longModPowerBarrett(A, B, N1,muN);
        BigInteger javaA = new BigInteger("A5F", 16);
        BigInteger javaB = new BigInteger("1B2", 16);
        BigInteger javaN1 = new BigInteger("D", 16);
        BigInt expected2 = new BigInt(BigInt.n);
        expected2.num[0] = javaA.modPow(javaB, javaN1).intValue();
        checkEq("A^B mod N (Barrett Horner)", powerRes, expected2);

        System.out.println("\nResult:");
        print("A", A);
        print("B", B);
        print("A + B", addAB.sum);

        BigInt.SubResult diffAB = A.longSub(B);
        print("A - B", diffAB.sub);

        print("A * B", bi.longMul(A, B));

        print("A / B", divAB.q);

        print("A^2", sq);

        print("A mod B", divAB.r);
        print("A + B mod N", bi.modAdd(A, B, N1,muN));
        print("A - B mod N", bi.modSub(A, B, N1,muN));
        print("A * B mod N", bi.modMul(A, B, N1,muN));
        print("A^2 mod N", bi.modSquare(A, N1));
        print("Barrett Redc(X, n)", barrettResult);
        print("A^B mod N", powerRes);
        print("gcd(A, B)", gcdAB);
        print("lcm(A, B)", lcmAB);
        print("Barret Horner A^Bmod N",powerRes);

        int launchOp = 10000;
        long tAdd = 0, tSub = 0, tMul = 0, tSq = 0, tDiv = 0,tMod = 0,tModAdd = 0,tModSub = 0,tModMul = 0,tModSquare = 0,tRed = 0,tPow = 0,tGcd = 0,tLcm = 0;
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
        int launchOp1=10000;

        for (int i = 0; i < launchOp1; i++) {
            A = randomBigInt(BigInt.n);
            B = randomBigInt(BigInt.n);

            BigInt Nmod = new BigInt(BigInt.n);
            Nmod.num[0] = rnd.nextInt(0xFFFF) + 2;

            BigInt muMod = bi.mu(Nmod);

            long t0 = System.nanoTime();
            tmp = bi.modAdd(A, B, Nmod, muMod);
            long t1 = System.nanoTime();
            tModAdd += (t1 - t0);

            t0 = System.nanoTime();
            tmp = bi.modSub(A, B, Nmod, muMod);
            t1 = System.nanoTime();
            tModSub += (t1 - t0);

            t0 = System.nanoTime();
            tmp = bi.modMul(A, B, Nmod, muMod);
            t1 = System.nanoTime();
            tModMul += (t1 - t0);

            t0 = System.nanoTime();
            tmp = bi.modSquare(A, Nmod, muMod);
            t1 = System.nanoTime();
            tModSquare += (t1 - t0);

            t0 = System.nanoTime();
            tmp = bi.longModPowerBarrett(A, B, Nmod, muMod);
            t1 = System.nanoTime();
            tPow += (t1 - t0);
        }


        System.out.println("\nTime measurements with random data (avg ns): ");
        System.out.printf("Add:  %.2f ns%n", tAdd / (double) launchOp);
        System.out.printf("Sub:  %.2f ns%n", tSub / (double) launchOp);
        System.out.printf("Mul:  %.2f ns%n", tMul / (double) launchOp);
        System.out.printf("Square:  %.2f ns%n", tSq / (double) launchOp);
        System.out.printf("DivMod:  %.2f ns%n", tDiv / (double) launchOp);
        System.out.printf("A mod B:  %.2f ns%n", tMod / (double) launchOp1);
        System.out.printf("A + B mod N:  %.2f ns%n", tModAdd / (double) launchOp1);
        System.out.printf("A - B mod N:   %.2f ns%n", tModSub / (double) launchOp1);
        System.out.printf("A * B mod N:   %.2f ns%n", tModMul / (double) launchOp1);
        System.out.printf("A^2 mod N:  %.2f ns%n", tModSquare / (double) launchOp1);
        System.out.printf("Barrett Redc:   %.2f ns%n", tRed / (double) launchOp1);
        System.out.printf("A^B mod N :   %.2f ns%n", tPow / (double) launchOp1);
        System.out.printf("gcd(A, B):    %.2f ns%n", tGcd / (double) launchOp1);
        System.out.printf("lcm(A, B):    %.2f ns%n", tLcm / (double) launchOp1);
    }
}
