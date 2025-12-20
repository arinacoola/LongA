import java.util.Locale;

public class BigInt {
    public static final int n = 256; 
    private final int[] a = new int[n];
    public boolean errorFlag = false;

    public BigInt() {}

    public static BigInt zero() {
        return new BigInt();
    }

    public static BigInt constWord(long v) {
        BigInt x = new BigInt();
        x.a[0] = (int) (v & 0xFFFFFFFFL);
        return x;
    }

    public static BigInt fromHex(String hex) {
        return hexToBigInt(hex);
    }

    public static BigInt hexToBigInt(String hex) {
        BigInt r = new BigInt();
        if (hex == null) {
            r.errorFlag = true;
            return r;
        }
        hex = hex.trim();
        if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
        if (hex.isEmpty()) {
            r.errorFlag = true;
            return r;
        }
        int len = hex.length();
        for (int i = 0; i < len; i++) {
            int digit = hexToDigit(hex.charAt(len - 1 - i));
            if (digit < 0) {
                r.errorFlag = true;
                return r;
            }
            int word = i / 8;
            if (word >= n) {
                continue;
            }
            int shift = (i % 8) * 4;
            r.a[word] |= (digit << shift);
        }
        return r;
    }

    private static int hexToDigit(char c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        }
        if ('a' <= c && c <= 'f'){
            return (c - 'a') + 10;
        }
        if ('A' <= c && c <= 'F'){
            return (c - 'A') + 10;
        }
        return -1;
    }

    public String toHex() {
        if (errorFlag) {
            return "error";
        }
        if (isZero()) {
            return "0";
        }
        StringBuilder sb = new StringBuilder(n * 8);
        for (int i = n - 1; i >= 0; i--) {
            sb.append(String.format(Locale.ROOT, "%08x", a[i]));
        }
        int k = 0;
        while (k < sb.length() - 1 && sb.charAt(k) == '0'){
            k++;
        }
        return sb.substring(k);
    }


    public BigInt copy() {
        BigInt c = new BigInt();
        System.arraycopy(this.a, 0, c.a, 0, n);
        c.errorFlag = this.errorFlag;
        return c;
    }

    public boolean isZero() {
        for (int v : a) {
            if (v != 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isEven() {
        return (a[0] & 1) == 0;
    }

    public int longCmp(BigInt b) {
        for (int i = n - 1; i >= 0; i--) {
            long x = this.a[i] & 0xFFFFFFFFL;
            long y = b.a[i] & 0xFFFFFFFFL;
            if (x > y) {
                return 1;
            }
            if (x < y) {
                return -1;
            }
        }
        return 0;
    }

    public int bitLength() {
        for (int i = n - 1; i >= 0; i--) {
            int w = a[i];
            if (w != 0) {
                for (int bit = 31; bit >= 0; bit--) {
                    if (((w >>> bit) & 1) != 0) return i * 32 + bit + 1;
                }
            }
        }
        return 0;
    }

    public int digitLength() {
        for (int i = n - 1; i >= 0; i--) {
            if (a[i] != 0) return i + 1;
        }
        return 0;
    }

    public int getBit(int pos) {
        if (pos < 0 || pos >= n * 32) return 0;
        return (a[pos / 32] >>> (pos % 32)) & 1;
    }

    public long extractBit(int i) {
        if (i < 0 || i >= n * 32) {
            return 0;
        }
        int block = i / 32;
        int bit = i % 32;
        return (a[block] >>> bit) & 1L;
    }

    public BigInt shiftLeft(int bits) {
        if (bits <= 0) {
            if (bits == 0) {
                return this.copy();
            }

            return zero();
        }
        BigInt r = new BigInt();
        int digitShift = bits / 32;
        int bitShift = bits % 32;
        if (digitShift >= n) {
            return r;
        }
        long carry = 0;
        for (int i = 0; i < n - digitShift; i++) {
            long cur = this.a[i] & 0xFFFFFFFFL;
            long out = ((cur << bitShift) & 0xFFFFFFFFL) | carry;
            r.a[i + digitShift] = (int) out;
            if (bitShift == 0) {
                carry = 0;
            }
            else {
                carry = (cur >>> (32 - bitShift)) & 0xFFFFFFFFL;
            }
        }
        return r;
    }

    public BigInt rightShift(int bits) {
        if (bits <= 0) {
            if (bits == 0){
                return this.copy();
            }
            return zero();
        }
        BigInt r = new BigInt();
        int digitShift = bits / 32;
        int bitShift = bits % 32;
        if (digitShift >= n) {
            return r;
        }
        for (int i = digitShift; i < n; i++) {
            long low = (this.a[i] & 0xFFFFFFFFL) >>> bitShift;
            long high = 0;
            if (bitShift != 0 && i + 1 < n) {
                high = (this.a[i + 1] & 0xFFFFFFFFL) << (32 - bitShift);
            }
            r.a[i - digitShift] = (int) ((low | high) & 0xFFFFFFFFL);
        }
        return r;
    }

    public BigInt longShiftDigitsToLow(int shift) {
        BigInt r = new BigInt();
        if (shift <= 0) {
            if (shift == 0) return this.copy();
            return r;
        }
        if (shift >= n) return r;
        for (int i = 0; i < n - shift; i++) {
            r.a[i] = this.a[i + shift];
        }
        return r;
    }

    public BigInt longShiftDigitsToHigh(int shift) {
        BigInt r = new BigInt();
        if (shift <= 0) {
            if (shift == 0) return this.copy();
            return r;
        }
        if (shift >= n){
            return r;
        }
        for (int i = n - 1; i >= shift; i--) {
            r.a[i] = this.a[i - shift];
        }
        return r;
    }

    public BigInt longAdd(BigInt b) {
        BigInt r = new BigInt();
        long carry = 0;
        for (int i = 0; i < n; i++) {
            long sum = (this.a[i] & 0xFFFFFFFFL) + (b.a[i] & 0xFFFFFFFFL) + carry;
            r.a[i] = (int) sum;
            carry = sum >>> 32;
        }
        return r;
    }

    public BigInt subAbs(BigInt b) {
        BigInt x = this;
        BigInt y = b;
        if (x.longCmp(y) < 0) {
            x = b;
            y = this;
        }
        BigInt r = new BigInt();
        long borrow = 0;
        for (int i = 0; i < n; i++) {
            long diff = (x.a[i] & 0xFFFFFFFFL) - (y.a[i] & 0xFFFFFFFFL) - borrow;
            if (diff >= 0) {
                r.a[i] = (int) diff;
                borrow = 0;
            }
            else {
                r.a[i] = (int) (diff + (1L << 32));
                borrow = 1;
            }
        }
        return r;
    }

    public BigInt subSigned(BigInt b) {
        BigInt res = this.longSub(b);
        if (this.longCmp(b) < 0) {
            res = res.twosComplement();
        }
        return res;
    }

    private BigInt twosComplement() {
        BigInt r = new BigInt();
        for (int i = 0; i < n; i++) {
            r.a[i] = ~this.a[i];
        }
        return r.longAdd(BigInt.constWord(1));
    }

    private BigInt longSub(BigInt b) {
        BigInt res = new BigInt();
        long borrow = 0;
        for (int i = 0; i < n; i++) {
            long diff = (this.a[i] & 0xffffffffL)
                    - (b.a[i] & 0xffffffffL)
                    - borrow;
            if (diff < 0) {
                diff += (1L << 32);
                borrow = 1;
            }
            else {
                borrow = 0;
            }
            res.a[i] = (int) diff;
        }
        return res;
    }


    public BigInt longMulOneDigit(long b) {
        BigInt r = new BigInt();
        long carry = 0;
        long bb = b & 0xFFFFFFFFL;
        for (int i = 0; i < n; i++) {
            long aa = this.a[i] & 0xFFFFFFFFL;
            long tmp = aa * bb + carry;
            r.a[i] = (int) tmp;
            carry = tmp >>> 32;
        }
        r.a[n - 1] = (int) (carry & 0xFFFFFFFFL);
        return r;
    }

    public BigInt longMul(BigInt b) {
        BigInt res = new BigInt();
        for (int i = 0; i < n; i++) {
            long bi = b.a[i] & 0xFFFFFFFFL;
            BigInt temp = this.longMulOneDigit(bi);
            temp = temp.longShiftDigitsToHigh(i);
            res = res.longAdd(temp);
        }
        return res;
    }

    public String toHexSigned() {
        StringBuilder sb = new StringBuilder();
        boolean negative = (a[n - 1] & 0x80000000) != 0;
        for (int i = n - 1; i >= 0; i--) {
            sb.append(String.format("%08x", a[i]));
        }
        String hex = sb.toString();
        if (negative) {
            hex = hex.replaceFirst("^f+", "");
            return "-" + hex;
        }
        else {
            hex = hex.replaceFirst("^0+", "");
            return hex.isEmpty() ? "0" : hex;
        }
    }


    public BigInt longSq() {
        return this.longMul(this);
    }

    public BigInt longDiv(BigInt b) {
        if (b == null || b.isZero()) {
            BigInt e = new BigInt();
            e.errorFlag = true;
            return e;
        }
        if (this.isZero()){
            return zero();
        }
        if (b.bitLength() == 1 && (b.a[0] & 0xFFFFFFFFL) == 1L){
            return this.copy();
        }
        BigInt q = new BigInt();
        BigInt r = this.copy();
        while (r.longCmp(b) >= 0) {
            int shift = r.bitLength() - b.bitLength();
            if (shift < 0) {
                break;
            }
            BigInt shifted = b.shiftLeft(shift);
            if (r.longCmp(shifted) < 0) {
                shift--;
                if (shift < 0) {
                    break;
                }
                shifted = b.shiftLeft(shift);
            }
            r = r.subAbs(shifted);
            q = q.longAdd(constWord(1).shiftLeft(shift));
        }
        return q;
    }

    public BigInt mod(BigInt m) {
        if (m == null || m.isZero()) {
            BigInt e = new BigInt();
            e.errorFlag = true;
            return e;
        }
        BigInt q = this.longDiv(m);
        BigInt prod = q.longMul(m);
        BigInt r = this.subAbs(prod);
        if (r.longCmp(m) >= 0) {
            r = r.mod(m);
        }
        return r;
    }

    public BigInt modAdd(BigInt b, BigInt m) {
        return this.longAdd(b).mod(m);
    }

    public BigInt modSub(BigInt b, BigInt m) {
        return this.longAdd(m).subSigned(b).mod(m);
    }

    public static BigInt mu(BigInt mod) {
        if (mod == null || mod.isZero()) {
            throw new IllegalArgumentException("mod is zero");
        }
        int k = mod.digitLength();
        BigInt beta = constWord(1).longShiftDigitsToHigh(2 * k);
        BigInt mu = beta.longDiv(mod);
        if (mu.isZero()){
            throw new IllegalStateException("mu invalid");
        }
        return mu;
    }

    public BigInt barrettRedc(BigInt mod, BigInt mu) {
        int k = mod.digitLength();
        if (this.longCmp(mod) < 0){
            return this;
        }
        BigInt Q = this.longShiftDigitsToLow(k - 1);
        Q = Q.longMul(mu);
        Q = Q.longShiftDigitsToLow(k + 1);
        Q = Q.longMul(mod);
        BigInt R = this.subAbs(Q);
        if (R.longCmp(mod) >= 0) {
            R = R.mod(mod);
        }
        return R;
    }

    public BigInt modMul(BigInt b, BigInt mod) {
        BigInt mu = BigInt.mu(mod);
        return this.longMul(b).barrettRedc(mod, mu);
    }

    public BigInt modSq(BigInt mod) {
        return this.longSq().mod(mod);
    }

    public BigInt longModPowerBarrett(BigInt exp, BigInt mod) {
        if (mod == null || mod.isZero()){
            throw new IllegalArgumentException("mod zero");
        }
        BigInt mu = BigInt.mu(mod);
        BigInt base = this.barrettRedc(mod, mu);
        BigInt res = constWord(1);
        for (int i = exp.bitLength() - 1; i >= 0; i--) {
            res = res.longMul(res).barrettRedc(mod, mu);
            if (exp.getBit(i) == 1) {
                res = res.longMul(base).barrettRedc(mod, mu);
            }
        }
        return res;
    }

    public BigInt gcd(BigInt other) {
        BigInt a = this.copy();
        BigInt b = other.copy();
        BigInt d = constWord(1);
        while (a.isEven() && b.isEven()) {
            a = a.rightShift(1);
            b = b.rightShift(1);
            d = d.shiftLeft(1);
        }
        while (a.isEven()) {
            a = a.rightShift(1);
        }
        while (!b.isZero()) {
            while (b.isEven()){
                b = b.rightShift(1);
            }
            if (a.longCmp(b) > 0) {
                BigInt tmp = a; a = b; b = tmp;
            }
            b = b.subAbs(a);
        }
        return a.longMul(d);
    }

    public BigInt lcm(BigInt other) {
        BigInt g = this.gcd(other);
        BigInt prod = this.longMul(other);
        return prod.longDiv(g);
    }

}
