import java.util.Random;

public class TimeAndCycle {
    private static final int WARMUP = 5000;
    private static final int ITERATIONS = 10000;
    private static final double CPU_GHZ = 3.2;
    private static final Random rnd = new Random();

    public static void main(String[] args) {
        BigInt M = randomBigInt();

        benchmark("add", () -> {
            BigInt a = randomBigInt();
            BigInt b = randomBigInt();
            a.longAdd(b);
        });

        benchmark("sub", () -> {
            BigInt a = randomBigInt();
            BigInt b = randomBigInt();
            a.subAbs(b);
        });

        benchmark("mull", () -> {
            BigInt a = randomBigInt();
            BigInt b = randomBigInt();
            a.longMul(b);
        });

        benchmark("div", () -> {
            BigInt a = randomBigInt();
            BigInt b = randomBigInt();
            a.longDiv(b);
        });

        benchmark("sq", () -> {
            BigInt a = randomBigInt();
            a.longSq();
        });

        benchmark("mod", () -> {
            BigInt a = randomBigInt();
            a.mod(M);
        });

        benchmark("mod add", () -> {
            BigInt a = randomBigInt();
            BigInt b = randomBigInt();
            a.modAdd(b, M);
        });

        benchmark("mod sub", () -> {
            BigInt a = randomBigInt();
            BigInt b = randomBigInt();
            a.modSub(b, M);
        });

        benchmark("mod sq", () -> {
            BigInt a = randomBigInt();
            a.modSq(M);
        });

        benchmark("Barrett", () -> {
            BigInt a = randomBigInt();
            BigInt b = randomBigInt();
            a.modMul(b, M);
        });

        benchmark("Horner+Barrett", () -> {
            BigInt a = randomBigInt();
            BigInt e = BigInt.constWord(65537);
            a.longModPowerBarrett(e, M);
        });
    }

    private static void benchmark(String name, Operation op) {
        for (int i = 0; i < WARMUP; i++) {
            op.run();
        }
        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            op.run();
        }
        long end = System.nanoTime();
        double avgNs = (end - start) / (double) ITERATIONS;
        double cycles = avgNs * CPU_GHZ;
        System.out.printf("%-20s : %10.2f ns   ( %.0f cycles)%n", name, avgNs, cycles);
    }

    private static BigInt randomBigInt() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            sb.append(Integer.toHexString(rnd.nextInt()));
        }
        return BigInt.fromHex(sb.toString());
    }

    private interface Operation {
        void run();
    }
}
