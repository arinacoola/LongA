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
            long temp = (this.num[i] & 0xFFFFFFFFL) + (other.num[i] & 0xFFFFFFFFL) + carry;
            c.num[i] = (int) (temp & 0xFFFFFFFFL);
            carry = temp >>> 32;
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
            long bb = b.num[i] & 0xFFFFFFFFL;
            long carry = 0;
            for (int j = 0; j < n; j++) {
                int pos = i + j;
                long cur = c.num[pos] & 0xFFFFFFFFL;
                long prod = (a.num[j] & 0xFFFFFFFFL) * bb;
                long tmp = cur + prod + carry;
                c.num[pos] = (int) (tmp & 0xFFFFFFFFL);
                carry = tmp >>> 32;
            }
            int pos = i + n;
            while (carry != 0 && pos < 2 * n) {
                long tmp = (c.num[pos] & 0xFFFFFFFFL) + carry;
                c.num[pos] = (int) (tmp & 0xFFFFFFFFL);
                carry = tmp >>> 32;
                pos++;
            }
        }
        return c;
    }

    public BigInt longSquare(BigInt a) {
        return longMul(a, a);
    }

    public int longCmp(BigInt a, BigInt b) {
        int lena = a.num.length > BigInt.n ? BigInt.n : a.num.length;
        int lenb = b.num.length > BigInt.n ? BigInt.n : b.num.length;
        int i = n - 1;
        while (i >= 0 && a.num[i] == b.num[i]) {
            i = i - 1;
        }
        if (i == -1) {
            return 0;
        } else {
            if ((a.num[i] & 0xFFFFFFFFL) > (b.num[i] & 0xFFFFFFFFL)) {
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
        if (longCmp(B, BigInt.constZero()) == 0) {
            throw new ArithmeticException("division by zero in longDivMod");
        }

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

    public BigInt longPower(BigInt a, BigInt b) {
        BigInt aSqB = BigInt.constOne();
        int bitLen = BitLength(b);
        for (int i = bitLen - 1; i >= 0; i--) {
            if (bitReview(b, i)) {
                aSqB = longMul(aSqB, a);
            }
            if (i != 0) {
                aSqB = longMul(aSqB, aSqB);
            }
        }
        return aSqB;
    }
    public boolean bitReview(BigInt b, int i) {
        int word = i / 32;
        int bit  = i % 32;
        if (word >= b.num.length) {
            return false;
        }
        return ((b.num[word] >>> bit) & 1) == 1;
    }



    public static BigInt longShiftBitsToLeft(BigInt a, int shiftBits) {
        BigInt c = new BigInt(n);
        int shiftBlocks = shiftBits / 32;
        int bitShift = shiftBits % 32;
        long carry = 0;

        for (int i = 0; i < n; i++) {
            long cur = (i - shiftBlocks >= 0 ? (a.num[i - shiftBlocks] & 0xFFFFFFFFL) : 0);
            long shifted = ((cur << bitShift) & 0xFFFFFFFFL);
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

    public static void shiftRightInPlace(BigInt a, int shiftBits) {
        if (shiftBits == 0) return;
        int shiftBlocks = shiftBits / 32;
        int bitShift = shiftBits % 32;
        int nlen = a.num.length;
        if (shiftBlocks >= nlen) {
            for (int i = 0; i < nlen; i++) a.num[i] = 0;
            return;
        }
        if (bitShift == 0) {
            for (int i = 0; i < nlen; i++) {
                int src = i + shiftBlocks;
                a.num[i] = (src < nlen) ? a.num[src] : 0;
            }
            return;
        }
        for (int i = 0; i < nlen; i++) {
            int src = i + shiftBlocks;
            long low = 0;
            if (src < nlen) low = (a.num[src] & 0xFFFFFFFFL) >>> bitShift;
            long high = 0;
            if (src + 1 < nlen) high = (a.num[src + 1] & 0xFFFFFFFFL) << (32 - bitShift);
            a.num[i] = (int) ((low | high) & 0xFFFFFFFFL);
        }
    }

    public static void shiftLeftInPlace(BigInt a, int shiftBits) {
        if (shiftBits == 0) return;
        int shiftBlocks = shiftBits / 32;
        int bitShift = shiftBits % 32;
        int nlen = a.num.length;
        if (shiftBlocks >= nlen) {
            for (int i = 0; i < nlen; i++) a.num[i] = 0;
            return;
        }
        if (bitShift == 0) {
            for (int i = nlen - 1; i >= 0; i--) {
                int src = i - shiftBlocks;
                a.num[i] = (src >= 0) ? a.num[src] : 0;
            }
            return;
        }
        for (int i = nlen - 1; i >= 0; i--) {
            int src = i - shiftBlocks;
            long high = 0;
            if (src >= 0) high = ((a.num[src] & 0xFFFFFFFFL) << bitShift) & 0xFFFFFFFFL;
            long low = 0;
            if (src - 1 >= 0) low = (a.num[src - 1] & 0xFFFFFFFFL) >>> (32 - bitShift);
            a.num[i] = (int) ((high | low) & 0xFFFFFFFFL);
        }
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
        BigInt aa = new BigInt(n);
        BigInt bb = new BigInt(n);
        System.arraycopy(a.num, 0, aa.num, 0, n);
        System.arraycopy(b.num, 0, bb.num, 0, n);

        if (longCmp(aa, BigInt.constZero()) == 0) {
            return bb;
        }
        if (longCmp(bb, BigInt.constZero()) == 0) {
            return aa;
        }
        BigInt d = BigInt.constOne();
        while (even(aa) && even(bb)) {
            shiftRightInPlace(aa, 1);
            shiftRightInPlace(bb, 1);
            shiftLeftInPlace(d, 1);
        }
        while (even(aa)) {
            shiftRightInPlace(aa, 1);
        }
        while (longCmp(bb, BigInt.constZero()) != 0) {
            while (even(bb)) {
                shiftRightInPlace(bb, 1);
            }
            if (longCmp(aa, bb) > 0) {
                BigInt tmp = aa;
                aa = bb;
                bb = absDiff(tmp, bb);
            } else {
                bb = absDiff(bb, aa);
            }
        }
        return longMul(d, aa);
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

    public BigInt modAdd(BigInt a, BigInt b, BigInt n, BigInt mu) {
        BigInt aRed = barrettRedc(a, n, mu);
        BigInt bRed = barrettRedc(b, n, mu);
        BigInt sum = aRed.longAdd(bRed).sum;
        if (longCmp(sum, n) >= 0) {
            sum = sum.longSub(n).sub;
        }
        return sum;
    }

    public BigInt modSub(BigInt a, BigInt b, BigInt n, BigInt mu) {
        BigInt aRed = barrettRedc(a, n, mu);
        BigInt bRed = barrettRedc(b, n, mu);
        BigInt.SubResult s = aRed.longSub(bRed);
        BigInt diff = s.sub;
        if (s.borrow == 1) {
            diff = diff.longAdd(n).sum;
        }
        return diff;
    }

    public BigInt modMul(BigInt a, BigInt b, BigInt n, BigInt mu) {
        BigInt mul = longMul(a, b);
        return barrettRedc(mul, n, mu);
    }


    public BigInt modSquare(BigInt a, BigInt n) {
        BigInt sq = longSquare(a);
        BigInt muVal = mu(n);
        return barrettRedc(sq, n, muVal);
    }
    public BigInt modSquare(BigInt a, BigInt n, BigInt mu) {
        BigInt sq = longSquare(a);
        return barrettRedc(sq, n, mu);
    }

    public int findK(BigInt x) {
        int i = BigInt.n - 1;
        while (i > 0 && x.num[i] == 0) {
            i--;
        }
        return i + 1;
    }

    public BigInt mu(BigInt n) {
        int k = findK(n);
        BigInt betaPower = BigInt.constZero();
        if (2 * k < BigInt.n) {
            betaPower.num[2 * k] = 1;
        }
        else {
            throw new IllegalArgumentException("n is too large for Barrett mu");
        }

        DivModResult div = longDivMod(betaPower, n);
        return div.q;
    }

    private BigInt killLastDigits(BigInt a, int digits) {
        BigInt res = BigInt.constZero();
        int len = a.num.length;
        if (digits >= len) {
            return res;
        }
        int assignmentInd = 0;
        for (int src = digits; src < len && assignmentInd < BigInt.n; src++, assignmentInd++) {
            res.num[assignmentInd] = a.num[src];
        }
        return res;
    }

    public BigInt barrettRedc(BigInt x, BigInt n, BigInt mu) {
        int k = findK(n);
        if (k == 1) {
            long nVal = n.num[0] & 0xFFFFFFFFL;
            if (nVal == 0) {
                throw new ArithmeticException("modulus n is zero");
            }
            long rem = 0;
            for (int i = x.num.length - 1; i >= 0; i--) {
                rem = ((rem << 32) + (x.num[i] & 0xFFFFFFFFL)) % nVal;
            }
            BigInt r = new BigInt(BigInt.n);
            r.num[0] = (int) rem;
            return r;
        }
        BigInt q = killLastDigits(x, k - 1);
        q = longMul(q, mu);
        q = killLastDigits(q, k + 1);
        BigInt tmp = longMul(q, n);
        BigInt r = x.longSub(tmp).sub;
        while (longCmp(r, n) >= 0) {
            r = r.longSub(n).sub;
        }
        return r;
    }

    public int getBit(BigInt a, int bitIndex) {
        int block = bitIndex / 32;
        int bitPos = bitIndex % 32;
        if (block >= BigInt.n) {
            return 0;
        }
        return (a.num[block] >>> bitPos) & 1;
    }

    public BigInt longModPowerBarrett(BigInt a, BigInt b, BigInt n, BigInt mu) {
        a = barrettRedc(a, n, mu);
        BigInt c = BigInt.constOne();
        int l = BitLength(b);
        for (int i = 0; i < l; i++) {
            if (getBit(b, i) == 1) {
                c = barrettRedc(longMul(c, a), n, mu);
            }
            a = barrettRedc(longSquare(a), n, mu);
        }
        return c;
    }

}