public class TestBigInt {
    public static void main(String[] args) {
        BigInt A = BigInt.fromHex(
                "4311368f8b2477d26f7ad342ccb45f2af9434ff3cf916c140a21e8d9890ec19250a8cb99122dd36602be0291453eb38bc883edce0c1a2fa79a0e9ec7c44786344f0c60be496825e9537785641c9fda776d9711d3b46c9b80946764b4a3f78a86791f4f57e34db1fe86f1c3332382913d4b7cbc068bd041a45ee851bd7772e73051fb80e4f09042255df312f74300e9ebc940a5c5bded11493f28e4eb26ddb79c4fe189c333f10b7d7e8a27a9cf0f1d13ae8c91c260c2e41a89456bcf022576699edb19c3466278585f2dcbd491afa9a2fbaa37551492fed460142287b8c4b1ea9c839b5bf3255094ef44c3baee9b432e37f7a3adf29700e426e5d52d66f7a429"
        );
        BigInt B = BigInt.fromHex(
                "e623cbf7689b424b6752e6e0da2352f6b38d139ed2c4a7e10c20efff0ea371275e708e75ac3154aaf9fea195ad3da3ea9c5d28258bef62950b6e410dd18484d9a4d308b9edc712db662c123e3764d5920c3f2e18c21673026a3a2a06f887926fcd9607cb490208c72ae0f873cceeae9bec724f91610df0f35f62d15a89853f2a9d908a317b3e8db7030189a6ce2e2e2d1ffb47ab99e782a607734a72c19900619cc09b48321b47af307a065f0ec2fff65989bfd1844832a424f6daad249cb342a3f45e52b9c0b0e5ccca5b6b84b7e9698ecf0a1686cbe11ee61eaffd2150683db1a95920df6dba3bd28956dd2c507c227188bfbb5fdfe341e16194a006a83fc3"
        );
        BigInt M = BigInt.fromHex(
                "c96c3b135fd937cfe2dcdde54493d2ed9c37907c825824a03b99089235a5e392afd399dd584229dede2d48facd2a65f2a6d83bb3d6a2847aad666195a6c40c4d5052cc144625e9aa347af109efcc0c3de21dcfb4ac74702ba61943a718631100831e3090d5aff172c166cf8ca5e24c25f2bec4f9a9061ca3c8ec092837bb0675e32becaf4181b95da7cde98a835286af8cb8607763b9d8c3e03f0bdcb898ae98927e89abbf3ea5829d8f468eca6182b30c3c04ac3418803e16fad84eb78704fb3dd4f0fae884410e5d5c31a1ef934c4588550dd6ba3b84ad91d0d1c52f1cd104b55866c79476f4d09663127b5586208adbb8916cd243435dde9047f9aa607304"
        );

        System.out.println("A = " + A.toHex());
        System.out.println("B = " + B.toHex());
        System.out.println("M = " + M.toHex());

        System.out.print("A + B = ");
        System.out.println(A.longAdd(B).toHex());

        System.out.print("A - B = ");
        System.out.println(A.subSigned(B).toHexSigned());

        System.out.print("A * B = ");
        System.out.println(A.longMul(B).toHex());

        System.out.print("A / B = ");
        System.out.println(A.longDiv(B).toHex());

        System.out.print("A^2 = ");
        System.out.println(A.longSq().toHex());

        System.out.print("A mod B = ");
        System.out.println(A.mod(B).toHex());

        System.out.print("A + B mod M =");
        System.out.println(A.modAdd(B, M).toHex());

        System.out.print("A - B mod M =");
        System.out.println(A.modSub(B, M).toHex());

        System.out.print("A * B mod M =");
        System.out.println(A.modMul(B, M).toHex());

        System.out.print("A^2 mod M =");
        System.out.println(A.modSq(M).toHex());

        System.out.print("A^B mod M =");
        System.out.println(A.longModPowerBarrett(B, M).toHex());

        System.out.print("gcd =");
        System.out.println(A.gcd(B).toHex());

        System.out.print("lcm =");
        System.out.println(A.lcm(B).toHex());

    }
}
