public class BigInt{
    public final static int w=32;
    int[] num;
    public final static int n=64;
    public final static String Hex ="0123456789ABCDEF";
    public BigInt() {
        num =new int[n];
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
        BigInt cZero=new BigInt();
        return cZero;
    }

    public static BigInt constOne(){
        BigInt cOne = new BigInt();
        cOne.num[0]=1;
        return cOne;
    }
    }


