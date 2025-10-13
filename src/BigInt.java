public class BigInt{
    public final static int w=32;
    int[] num;
    public final static int n=64;
    public final static String Hex ="0123456789ABCDEF";
    public BigInt(int size) {
        num =new int[size];
    }


    public String stringToHex(String string){
        if(string.length()<2) return string ;
        if(string.charAt(0) == '0'){
            if (string.charAt(1)=='x'||string.charAt(1)=='X'){
                    string=string.substring(2);
                }
        }

        string=string.toUpperCase();
        return string;

    }

    public int[]  hexToBlock(String string) {
        int blocks = string.length() / 8;
        if (string.length() % 8 != 0) {
            blocks = blocks + 1;
        }
        int[] num = new int[blocks];
        int endBlock = string.length();
        while (endBlock > 0) {
            int startBlock = endBlock - 8;
            if (startBlock < 0) {
                startBlock = 0;
            }
            String block = string.substring(startBlock, endBlock);
            endBlock = endBlock - 8;
        }
        return num;
    }

    public String blockToHex(int[] num){
        String hex="";
        for (int i= num.length-1;i>=0;i--){
            int currentNum = num[i];
            String stringForBlock = "";
            if(currentNum==0){
                stringForBlock = "00000000";
            }
            while (currentNum>0){
                int res =currentNum % 16;
                char symbol=Hex.charAt(res);
                stringForBlock =symbol+stringForBlock;
                currentNum =  currentNum / 16;
            }
            if(stringForBlock.length()<8){
                int notEnough = 8 - stringForBlock.length();
                for(int j = 0; j<notEnough;j++){
                    stringForBlock = "0"+stringForBlock;
                }
            }
            hex=hex+stringForBlock;
            }
        return hex;
        }

    public static BigInt constZero(){
        BigInt cZero=new BigInt(n);
        return cZero;
    }

    public static BigInt constOne(){
        BigInt cOne = new BigInt(n);
        cOne.num[0]=1;
        return cOne;
    }

    public class AddResult{
        public BigInt sum;
        public long carry;
    }

    public AddResult longAdd(BigInt other){
        BigInt c = new BigInt(n);
        long carry = 0;
        for (int i=0;i<n;i++){
            long temp = this.num[i]+other.num[i]+carry;
            c.num[i]= (int) (temp& 0xFFFFFFFFL);
            carry=temp>>w;
        }
        AddResult result = new AddResult();
        result.sum=c;
        result.carry=carry;
        return result;
    }

    public class SubResult{
        public BigInt sub;
        public int borrow;
    }

    public SubResult longSub(BigInt other){
        BigInt c = new BigInt(n);
        int borrow = 0;
        for(int i=0;i<n;i++){
            long temp =(this.num[i]& 0xFFFFFFFFL)-(other.num[i]& 0xFFFFFFFFL)-borrow;
            if(temp>=0){
                c.num[i]= (int) temp;
                borrow=0;
            }
            else{
                c.num[i]= (int) (0x100000000L+temp);
                borrow=1;
            }
        }
        SubResult result = new SubResult();
        result.sub=c;
        result.borrow=borrow;
        return result;
    }

    public BigInt longMulOneDigit(BigInt a ,int b){
        BigInt c = new BigInt(n);
        long carry =0;
        for(int i=0;i<n;i++){
            long temp = (a.num[i]& 0xFFFFFFFFL)*(b& 0xFFFFFFFFL)+carry;
            c.num[i]=(int) (temp& 0xFFFFFFFFL);
            carry=temp>>w;
        }
        c.num[n]= (int) carry;
        return c;
    }

    public BigInt longShiftDigitsToHigh(BigInt a,int shift){
        BigInt c = new BigInt(n);
        for (int i=0;i+shift<n;i++){
            c.num[i+shift] =a.num[i];
        }
        return c;
    }

    public BigInt longMul(BigInt a, BigInt b){
        BigInt c = new BigInt(2n);
        for(int i=0;i<n;i++){
            BigInt temp = longMulOneDigit(a,b.num[i]);
            temp=longShiftDigitsToHigh(temp,i);
            AddResult result = c.longAdd(temp);
            c = result.sum;
        }
        return c;
    }

    public BigInt longSquare(BigInt a){
        BigInt c = new BigInt(2*n);
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                long productPair = (a.num[i] & 0xFFFFFFFFL) * (a.num[j] & 0xFFFFFFFFL);
                if(i!=j){
                    productPair=productPair*2;
                }
                int pos=i+j;
                long carry = productPair;
                while(carry!=0 && pos<2*n){
                    long sum = c.num[pos] + carry;
                    if(sum<0x100000000L){
                        c.num[pos] = (int) sum;
                        carry=0;
                    }
                    else {
                        c.num[pos] = (int) (sum -0x100000000L);
                        carry=sum / 0x100000000L;
                    }
                    pos++;
                }
            }
        }
        return c;
    }


    }


