import java.math.BigDecimal;
import java.math.RoundingMode;

public class Main {

    final static BigDecimal MZP = BigDecimal.valueOf(42_500);

    final static BigDecimal MRP = BigDecimal.valueOf(2525);


    public static void main(String[] args) {


        Main app = new Main();

        boolean hasOPV = true;


        BigDecimal mrp25 = MRP.multiply(BigDecimal.valueOf(25));//.setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal zp =
//                BigDecimal.valueOf(42500)
                app.getZP(BigDecimal.valueOf(60709.1),true);
//                app.getZP(mrp25, hasOPV);
//                MRP.multiply(BigDecimal.valueOf(25))
        ;
        System.out.println("zp: " + zp);

        //        BigDecimal zpOfDauke = BigDecimal.valueOf(55_500);
        BigDecimal zpOfDauke = zp;
        BigDecimal minOklad = zpOfDauke;
        BigDecimal maxOklad = zpOfDauke.multiply(BigDecimal.valueOf(2));
        BigDecimal oklad = app.findOklad(zpOfDauke, minOklad, maxOklad, hasOPV);
        System.out.println("oklad binary search: " + oklad);

        app.findOkladEmperative(zpOfDauke, minOklad, maxOklad, hasOPV);

    }

    private void findOkladEmperative(BigDecimal zpOfDauke, BigDecimal minOklad, BigDecimal maxOklad, boolean hasOPV) {
        for (BigDecimal i = minOklad; i.compareTo(maxOklad) < 0; i = i.add(BigDecimal.valueOf(0.01))) {
            BigDecimal zp = getZP(i, hasOPV);

            if (zp.compareTo(zpOfDauke) == 0) {
                System.out.println("oklad forik: " + i);
            }
        }

    }

    private BigDecimal findOklad(BigDecimal zpOfDauke, BigDecimal minOPV, BigDecimal maxOPV, boolean hasOPV) {
        BigDecimal middleOklad = minOPV.add(maxOPV).divide(BigDecimal.valueOf(2)).setScale(2,
                RoundingMode.HALF_DOWN);

        BigDecimal middleZP = getZP(middleOklad, hasOPV);

        if (zpOfDauke.compareTo(middleZP) == 0) {
            return middleOklad;
        } else if (zpOfDauke.compareTo(middleZP) <= 0) {
            return findOklad(zpOfDauke, minOPV, middleOklad, hasOPV);
        } else {
            return findOklad(zpOfDauke, middleOklad, maxOPV, hasOPV);
        }
    }


    public BigDecimal getOPV(BigDecimal oklad) {
        BigDecimal opv = null;
        if (oklad.compareTo(MRP.multiply(BigDecimal.valueOf(25))) <= 0) {
//            opv = (oklad - getKOR(oklad, true)) * 0.1;
            opv = oklad.subtract(getKOR(oklad, true)).multiply(BigDecimal.valueOf(0.1));
//        } else if (oklad >= 50 * MRP) {
        } else if (oklad.compareTo(MRP.multiply(BigDecimal.valueOf(50))) >= 0) {
//            opv = 50 * MZP * 0.1;
            opv = MZP.multiply(BigDecimal.valueOf(5));
        } else {
//            opv = oklad * 0.1;
            opv = oklad.multiply(BigDecimal.valueOf(0.1));
        }

        return opv.setScale(2,
                RoundingMode.HALF_DOWN);
    }

    public BigDecimal getKOR(BigDecimal oklad, boolean hasOPV) {
//        oklad = hasOPV ? oklad * 0.1 : oklad;
        oklad = hasOPV ? oklad.multiply(BigDecimal.valueOf(0.1)) : oklad;

//        double kor = (oklad - MZP) * 0.9;
        BigDecimal kor = oklad.subtract(MZP).multiply(BigDecimal.valueOf(0.9)).setScale(2,
                RoundingMode.HALF_DOWN);

//        return kor < 0 ? 0 : kor;
        return kor.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : kor;
    }

    public BigDecimal getIPN(BigDecimal oklad, boolean hasOPV) {
        BigDecimal ipn = null;
        BigDecimal opv = hasOPV ? getOPV(oklad) : BigDecimal.ZERO;
//        if (oklad <= 25 * MRP) {
        if (oklad.compareTo(MRP.multiply(BigDecimal.valueOf(25))) <= 0) {
//            ipn = (oklad - opv - getKOR(oklad, hasOPV) - MZP) * 0.1;
            ipn = oklad.subtract(opv).subtract(getKOR(oklad, hasOPV)).subtract(MZP).multiply(BigDecimal.valueOf(0.1));
        } else {
//            ipn = (oklad - opv - MZP) * 0.1;
            ipn = oklad.subtract(opv).subtract(MZP).multiply(BigDecimal.valueOf(0.1));
        }

        return ipn.setScale(2,
                RoundingMode.HALF_DOWN);
    }

    private BigDecimal getZP(BigDecimal oklad, boolean hasOPV) {

        BigDecimal opv = hasOPV ? getOPV(oklad) : BigDecimal.ZERO;

//        BigDecimal zp = oklad - opv - getIPN(oklad, hasOPV) - getOCMC();
        BigDecimal zp = oklad.subtract(opv).subtract(getIPN(oklad, hasOPV));
        return zp.setScale(2,
                RoundingMode.HALF_DOWN);
    }

    private static double getOCMC() {
        //TODO fill this later
        return 0;
    }
}
