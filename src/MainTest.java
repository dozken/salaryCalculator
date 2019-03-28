import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

class MainTest {
    final static BigDecimal MZP = BigDecimal.valueOf(42_500);

    @org.junit.jupiter.api.Test
    void findOkladEmperative() {
        Main app = new Main();
        long lStartTime = Instant.now().toEpochMilli();

        for (BigDecimal i = BigDecimal.valueOf(42500); i.compareTo(MZP.multiply(BigDecimal.valueOf(50))) < 0; i = i.add(BigDecimal.valueOf(0.0001))) {

            BigDecimal zpOfDauke = i;
            BigDecimal minOklad = zpOfDauke;
            BigDecimal maxOklad = zpOfDauke.multiply(BigDecimal.valueOf(1.2));
            boolean hasOPV = true;

            Set<BigDecimal> okladEmperative = app.getOkladFor(zpOfDauke, minOklad, maxOklad, hasOPV);
            if (okladEmperative.size() > 1) {
                System.out.println("zp:" + zpOfDauke + " size:" + okladEmperative.size());

            }
//

        }
        long lEndTime = Instant.now().toEpochMilli();
        long output = lEndTime - lStartTime;
        System.out.println("Elapsed time in milliseconds: " + output);

    }

    @org.junit.jupiter.api.Test
    void findOklad() {
    }
}