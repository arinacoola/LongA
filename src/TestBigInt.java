public class TestBigInt {
    public static void main(String[] args) {
        BigInt A = BigInt.fromHex(
                "bed3383adc31c436890a48b637952e861dda5fbe20e67bc48efd60bfebd4bc2d644870956b8974ce06adc399309aa5cda2a9ac864c7725fb5b733c3287916468c8ae743bf4966cacd23070f21aa92c58902a3848bd53d7ba72cd81112e87f4bdb5326b216a61dc8fcf5028198514b5d88095f9748b28695074d3070089681113dbbdd94bd0fa72cabb21e427d9ac026df33db498ffca13fc73a7dee567a5ea998f7a830357aa720ef60aa26681e7115fb4df9bec6ceec004a7e0e282ab93e6a57d4ce4b1d1c7a7ce7f3a9604a92cc55c5d03b1ed76455ab3545899caf52f2628230193d6beadecf495e85f6558e9d8bc20834317969ec130dd37a0ef6e39da3"
        );
        BigInt B = BigInt.fromHex(
                "92e3b2c9b8a8ad2beac258cc788cd12ad486907fef3ab74183b53cf46f5d40804df65e1f3b3a59c7cae27ceeed98e3b50342cd848c7c326d62775259a34b158027c2f4072b6166e838d01546049f6d62971e0eb41d9c4f9dd5e38e73f396c4a44014d39b60ef5f0f3dab2d681d4e7ccebd8e23ad0e9e50235889ebdfc989bc20983956d7bec2090fd52a25783254a80919c407fae75730f28e1769f93483e2622d9670a0e228a078f7b2dd82703f41ffb4a495b494fa9c9ddaf79ba65a167d869fcfc585ca642c233b175a0f9398eb61b927f00b1467178940b12d048b90aacae137359822537b7cf098031db0b9adb7e7500a6ab077efe0b7affec31c333417"
        );
        BigInt M = BigInt.fromHex(
                "d567f03a12a01ff9d3e26999366834cb6f6adf811e22087d7db65757d2a85264ce9c3819525fb23d1c246715b0eca51bb2a219f29a61b3299135307e5eb878ad3f330e9bc092ed7d516c4fd3e3108b9b02ba2defc80914ab87b474847fa139afaa034132fba01e134e6ce332f8deecf9b1574a18e75e9432cdfe9c122833cd9773394094c37c5f2fb5261c032d34893063a22f8c3fdc9f7cc29d6e7b47daea9397e6d580f9b5a71394cffabe15b97202b8c2cb6ecaf0116d3b0cd13cf090aed2573e0fd3b009fa1ffbbf268fca63956c83f9e0492f5127682584cbdda6ee54fa96df242e908e2002c3e9f0a89f16be97080d1e57571c9089bc7d6733e251c421"
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
