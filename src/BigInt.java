public class BigInt {
    public final static int w = 32;
    int[] num;
    public final static int n = 64;
    public final static String Hex = "0123456789ABCDEF";

    public BigInt(int size) {
        num = new int[size];
    }

    public String stringToHex(String string) {
        if (string.length() < 2) return string;
        if (string.charAt(0) == '0') {
            if (string.charAt(1) == 'x' || string.charAt(1) == 'X') {
                string = string.substring(2);
            }
        }

        string = string.toUpperCase();
        return string;
    }

    public int[] hexToBlock(String string) {
        int blocks = string.length() / 8;
        if (string.length() % 8 != 0) {
            blocks++;
        }

        int[] num = new int[blocks];
        int endBlock = string.length();
        int blockIndex = 0;
        while (endBlock > 0) {
            int startBlock = endBlock - 8;
            if (startBlock < 0) {
                startBlock = 0;
            }
            String block = string.substring(startBlock, endBlock);
            num[blockIndex] = (int) Long.parseLong(block, 16);
            endBlock = startBlock;
            blockIndex++;
        }
        for (int i = 0; i < blocks / 2; i++) {
            int tmp = num[i];
            num[i] = num[blocks - 1 - i];
            num[blocks - 1 - i] = tmp;
        }

        return num;
    }

    public String blockToHex(int[] num) {
        String hex = "";
        for (int i = num.length - 1; i >= 0; i--) {
            int currentNum = num[i];
            String stringForBlock = "";
            if (currentNum == 0) {
                stringForBlock = "00000000";
            }
            while (currentNum > 0) {
                int res = currentNum % 16;
                char symbol = Hex.charAt(res);
                stringForBlock = symbol + stringForBlock;
                currentNum = currentNum / 16;
            }
            if (stringForBlock.length() < 8) {
                int notEnough = 8 - stringForBlock.length();
                for (int j = 0; j < notEnough; j++) {
                    stringForBlock = "0" + stringForBlock;
                }
            }
            hex = hex + stringForBlock;
        }
        return "0x" + hex;
    }

    public static BigInt constZero() {
        BigInt cZero = new BigInt(n);
        return cZero;
    }

    public static BigInt constOne() {
        BigInt cOne = new BigInt(n);
        cOne.num[0] = 1;
        return cOne;
    }

    public class AddResult {
        public BigInt sum;
        public long carry;
    }

    public AddResult longAdd(BigInt other) {
        BigInt c = new BigInt(n);
        long carry = 0;
        for (int i = 0; i < n; i++) {
            long temp = this.num[i] + other.num[i] + carry;
            c.num[i] = (int) (temp & 0xFFFFFFFFL);
            carry = temp >> w;
        }
        AddResult result = new AddResult();
        result.sum = c;
        result.carry = carry;
        return result;
    }

    public class SubResult {
        public BigInt sub;
        public int borrow;
    }

    public SubResult longSub(BigInt other) {
        BigInt c = new BigInt(n);
        int borrow = 0;
        for (int i = 0; i < n; i++) {
            long temp = (this.num[i] & 0xFFFFFFFFL) - (other.num[i] & 0xFFFFFFFFL) - borrow;
            if (temp >= 0) {
                c.num[i] = (int) temp;
                borrow = 0;
            } else {
                c.num[i] = (int) (0x100000000L + temp);
                borrow = 1;
            }
        }
        SubResult result = new SubResult();
        result.sub = c;
        result.borrow = borrow;
        return result;
    }

    public BigInt longMulOneDigit(BigInt a, int b) {
        BigInt c = new BigInt(n);
        long carry = 0;
        long bb = b & 0xFFFFFFFFL;
        for (int i = 0; i < n; i++) {
            long aa = a.num[i] & 0xFFFFFFFFL;
            long tmp = aa * bb + carry;
            c.num[i] = (int) (tmp & 0xFFFFFFFFL);
            carry = tmp >>> 32;
        }
        return c;
    }


    public BigInt longShiftDigitsToHigh(BigInt a, int shift) {
        BigInt c = new BigInt(n);
        if (shift >= n) return c;
        for (int i = 0; i + shift < n; i++) {
            c.num[i + shift] = a.num[i];
        }
        return c;
    }

    public BigInt longMul(BigInt a, BigInt b) {
        BigInt c = new BigInt(2 * n);
        for (int i = 0; i < n; i++) {
            BigInt temp = longMulOneDigit(a, b.num[i]);
            temp = longShiftDigitsToHigh(temp, i);
            AddResult result = c.longAdd(temp);
            c = result.sum;
        }
        return c;
    }


    public BigInt longSquare(BigInt a) {
        BigInt c = new BigInt(2 * n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                long productPair = (a.num[i] & 0xFFFFFFFFL) * (a.num[j] & 0xFFFFFFFFL);
                if (i != j) {
                    productPair = productPair * 2;
                }
                int pos = i + j;
                long carry = productPair;
                while (carry != 0 && pos < 2 * n) {
                    long sum = c.num[pos] + carry;
                    if (sum < 0x100000000L) {
                        c.num[pos] = (int) sum;
                        carry = 0;
                    } else {
                        c.num[pos] = (int) (sum - 0x100000000L);
                        carry = sum / 0x100000000L;
                    }
                    pos++;
                }
            }
        }
        return c;
    }

    public int longCmp(BigInt a, BigInt b) {
        int i = n - 1;
        while (i >= 0 && a.num[i] == b.num[i]) {
            i = i - 1;
        }
        if (i == -1) {
            return 0;
        } else {
            if (a.num[i] > b.num[i]) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public int BitLength(BigInt a) {
        int i = n - 1;
        while (i >= 0 && a.num[i] == 0) {
            i--;
        }
        if (i < 0) {
            return 0;
        }
        long block = a.num[i] & 0xFFFFFFFFL;
        int bits = 32;
        while (bits > 0 && ((block >> (bits - 1)) & 1) == 0) {
            bits--;
        }

        return i * 32 + bits;
    }


    public class DivModResult {
        BigInt r;
        BigInt q;
    }

    public DivModResult longDivMod(BigInt A, BigInt B) {
        BigInt R = new BigInt(n);
        BigInt Q = new BigInt(n);

        for (int i = 0; i < n; i++) {
            R.num[i] = A.num[i];
        }
        int k = BitLength(B);
        while (longCmp(R, B) >= 0) {
            int t = BitLength(R);
            int shiftBits = t - k;
            BigInt C = longShiftBitsToLeft(B, shiftBits);
            while (longCmp(C, R) > 0) {
                shiftBits--;
                C = longShiftBitsToLeft(B, shiftBits);
            }
            R = R.longSub(C).sub;
            int block = shiftBits / 32;
            int bit = shiftBits % 32;
            Q.num[block] |= (1 << bit);
        }
        DivModResult res = new DivModResult();
        res.q = Q;
        res.r = R;
        return res;
    }


    public static BigInt longShiftBitsToLeft(BigInt a, int shiftBits) {
        BigInt c = new BigInt(n);
        int shiftBlocks = shiftBits / 32;
        int bitShift = shiftBits % 32;
        long carry = 0;

        for (int i = 0; i < n; i++) {
            long cur = (i - shiftBlocks >= 0 ? (a.num[i - shiftBlocks] & 0xFFFFFFFFL) : 0);
            long shifted = (cur << bitShift) & 0xFFFFFFFFL;
            shifted |= carry;
            c.num[i] = (int) shifted;
            if (bitShift == 0) {
                carry = 0;
            } else {
                carry = (cur >>> (32 - bitShift)) & 0xFFFFFFFFL;
            }
        }
        return c;
    }

    public static BigInt longShiftBitsToRight(BigInt a, int shiftBits) {
        int shiftBlock = shiftBits / 32;
        int posBit = shiftBits % 32;
        BigInt c = new BigInt(n);
        for (int i = n - 1; i >= 0; i--) {
            int indx = i - shiftBlock;
            if (indx < 0) continue;
            long shifted = (a.num[i] & 0xFFFFFFFFL) >>> posBit;
            c.num[indx] = (int) (shifted & 0xFFFFFFFFL);
            if (posBit != 0 && indx - 1 >= 0) {
                long carry = (a.num[i] & 0xFFFFFFFFL) << (32 - posBit);
                c.num[indx - 1] |= (int) (carry & 0xFFFFFFFFL);
            }
        }
        return c;
    }

    public String toString() {
        return blockToHex(this.num);
    }

    public boolean even(BigInt a) {
        return (a.num[0] & 1) == 0;
    }

    public BigInt absDiff(BigInt a, BigInt b) {
        if (longCmp(a, b) >= 0) {
            return a.longSub(b).sub;
        } else {
            return b.longSub(a).sub;
        }
    }

    public BigInt gcdSteyn(BigInt a, BigInt b) {
        if (longCmp(a, BigInt.constZero()) == 0) {
            return b;
        }
        if (longCmp(b, BigInt.constZero()) == 0) {
            return a;
        }
        BigInt d = BigInt.constOne();
        while (even(a) && even(b)) {
            a = longShiftBitsToRight(a, 1);
            b = longShiftBitsToRight(b, 1);
            d = longShiftBitsToLeft(d, 1);
        }
        while (even(a)) {
            a = longShiftBitsToRight(a, 1);
        }
        while (longCmp(b, BigInt.constZero()) != 0) {
            while (even(b)) {
                b = longShiftBitsToRight(b, 1);
            }
            if (longCmp(a, b) > 0) {
                BigInt tmp = a;
                a = b;
                b = absDiff(tmp, b);
            } else {
                b = absDiff(b, a);
            }
        }
        return longMul(d, a);
    }

    public BigInt lcm(BigInt a,BigInt b){
       if(longCmp(a,BigInt.constZero())==0 || longCmp(b,BigInt.constZero())==0){
           return BigInt.constZero();
       }
        BigInt d = gcdSteyn(a, b);
        BigInt mul = longMul(a,b);
        BigInt.DivModResult div = longDivMod(mul,d);
        return div.q;
    }

    public BigInt modAdd(BigInt a, BigInt b, BigInt n) {
        BigInt.AddResult result = a.longAdd(b);
        BigInt c = result.sum;
        if (longCmp(c, n) >= 0) {
            c = c.longSub(n).sub;
        }
        return c;
    }

}





