import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class Main {

    final static BigDecimal MZP = BigDecimal.valueOf(42_500);

    final static BigDecimal MRP = BigDecimal.valueOf(2525);


    public static void main(String[] args) {


        Main app = new Main();

        boolean hasOPV = true;


        BigDecimal mrp25 = MRP.multiply(BigDecimal.valueOf(25));//.setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal zp =
                BigDecimal.valueOf(
                        55500
//                        56500
//                        57500
                )
//                app.getZP(BigDecimal.valueOf(60709.1),true);
//                app.getZP(mrp25, hasOPV);
//                MRP.multiply(BigDecimal.valueOf(25))
                ;
        System.out.println("zp: " + zp);

        //        BigDecimal zpOfDauke = BigDecimal.valueOf(55_500);
        BigDecimal zpOfDauke = zp;
        BigDecimal minOklad = zpOfDauke.multiply(BigDecimal.valueOf(1.0));
        BigDecimal maxOklad = zpOfDauke.multiply(BigDecimal.valueOf(1.2));
        BigDecimal midOklad = minOklad.add(maxOklad).divide(BigDecimal.valueOf(2)).setScale(2, RoundingMode.HALF_DOWN);

        //----------------
        long lStartTime = Instant.now().toEpochMilli();
        BigDecimal oklad1 = app.getOkladBinarySearchRecursive(zpOfDauke, minOklad, midOklad, hasOPV);
        BigDecimal oklad2 = app.getOkladBinarySearchRecursive(zpOfDauke, midOklad, maxOklad, hasOPV);
        long lEndTime = Instant.now().toEpochMilli();
        long output = lEndTime - lStartTime;
        System.out.println("oklad1 binary recursive: " + oklad1);
        System.out.println("oklad2 binary recursive: " + oklad2);
        System.out.println("oklad binary recursive: " + oklad1.min(oklad2));
        System.out.println("BinarySearch Recursive Elapsed time in milliseconds: " + output);
        System.out.println();
        //----------------
        lStartTime = Instant.now().toEpochMilli();
        BigDecimal oklad11 = app.getOkladBinarySearchWhile(zpOfDauke, minOklad, midOklad, hasOPV);
        BigDecimal oklad22 = app.getOkladBinarySearchWhile(zpOfDauke, midOklad, maxOklad, hasOPV);
        lEndTime = Instant.now().toEpochMilli();
        output = lEndTime - lStartTime;

        System.out.println("oklad1 binary iterative: " + oklad11);
        System.out.println("oklad2 binary iterative: " + oklad22);
        System.out.println("oklad binary iterative: " + oklad11.min(oklad22));
        System.out.println("BinarySearch Iterative Elapsed time in milliseconds: " + output);
        System.out.println();
        //----------------
        lStartTime = Instant.now().toEpochMilli();
        Set<BigDecimal> okladEmperative = app.getOkladFor(zpOfDauke, minOklad, maxOklad, hasOPV);
        okladEmperative.forEach(i -> System.out.println("oklad forik: " + i));
        lEndTime = Instant.now().toEpochMilli();
        output = lEndTime - lStartTime;
        System.out.println("Brute Force Elapsed time in milliseconds: " + output);

    }

    public Set<BigDecimal> getOkladFor(BigDecimal zpOfDauke, BigDecimal minOklad, BigDecimal maxOklad, boolean hasOPV) {
        Set<BigDecimal> list = new HashSet<>();
        for (BigDecimal i = minOklad; i.compareTo(maxOklad) < 0; i = i.add(BigDecimal.valueOf(0.01))) {
            BigDecimal zp = getZP(i, hasOPV);
            if (zp.compareTo(zpOfDauke) == 0) {
                list.add(i.setScale(0, RoundingMode.FLOOR));
            }
        }
        return list;

    }

    public BigDecimal getOkladBinarySearchRecursive(BigDecimal zp, BigDecimal minOklad, BigDecimal maxOklad, boolean hasOPV) {
        BigDecimal midOklad = minOklad.add(maxOklad).divide(BigDecimal.valueOf(2)).setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal midZP = getZP(midOklad, hasOPV);

        if (zp.compareTo(midZP) == 0) {
            return midOklad;
        } else if (minOklad.compareTo(maxOklad) == 0) {
            // if number is not found here, return big enough number to compare with other
            return zp.multiply(BigDecimal.TEN);
        } else if (zp.compareTo(midZP) < 0) {
            return getOkladBinarySearchRecursive(zp, minOklad, midOklad, hasOPV);
        } else //if (zp.compareTo(midZP) > 0)
        {
            return getOkladBinarySearchRecursive(zp, midOklad, maxOklad, hasOPV);
        }
    }

    public BigDecimal getOkladBinarySearchWhile(BigDecimal zp, BigDecimal minOklad, BigDecimal maxOklad, boolean hasOPV) {
        BigDecimal midOklad = minOklad.add(maxOklad).divide(BigDecimal.valueOf(2)).setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal midZP = getZP(midOklad, hasOPV);
        while (zp.compareTo(midZP) != 0) {
            midOklad = minOklad.add(maxOklad).divide(BigDecimal.valueOf(2)).setScale(2, RoundingMode.HALF_DOWN);
            midZP = getZP(midOklad, hasOPV);
            if (minOklad.compareTo(maxOklad) == 0) {
                return zp.multiply(BigDecimal.TEN);
            }
            if (zp.compareTo(midZP) < 0) {
                maxOklad = midOklad;
            } else if (zp.compareTo(midZP) > 0) {
                minOklad = midOklad;
            }
        }
        return midOklad;
    }


    private BigDecimal getOPV(BigDecimal oklad) {
        BigDecimal opv = null;
        if (oklad.compareTo(MRP.multiply(BigDecimal.valueOf(25))) <= 0) {
//            opv = (oklad - getKOR(oklad, true)) * 0.1;
            opv = oklad.subtract(getKOR(oklad, true)).multiply(BigDecimal.valueOf(0.1));
//        } else if (oklad >= 50 * MRP) {
        } else if (oklad.compareTo(MZP.multiply(BigDecimal.valueOf(50))) >= 0) {
//            opv = 50 * MZP * 0.1;
            opv = MZP.multiply(BigDecimal.valueOf(5));
        } else {
//            opv = oklad * 0.1;
            opv = oklad.multiply(BigDecimal.valueOf(0.1));
        }

        return opv.setScale(2,
                RoundingMode.HALF_DOWN);
    }

    private BigDecimal getKOR(BigDecimal oklad, boolean hasOPV) {
//        oklad = hasOPV ? oklad * 0.1 : oklad;
        BigDecimal opvs = hasOPV ? oklad.multiply(BigDecimal.valueOf(0.1)) : BigDecimal.ZERO;

//        double kor = (oklad - MZP) * 0.9;
        BigDecimal kor = oklad.subtract(opvs).subtract(MZP).multiply(BigDecimal.valueOf(0.9)).setScale(2,
                RoundingMode.HALF_DOWN);

//        return kor < 0 ? 0 : kor;
        return kor.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : kor;
    }

    private BigDecimal getIPN(BigDecimal oklad, boolean hasOPV) {
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
