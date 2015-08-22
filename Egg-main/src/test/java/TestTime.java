/**
 * Created by andrew on 15-8-22.
 */
public class TestTime {

    public static void main(String[] args) {
//        System.out.println();;
        System.out.printf(currentTime2String(17315L));
    }
    public static String currentTime2String(long currentTime) {
        if (currentTime < 0) {
            throw new IllegalArgumentException("time must bigger 0");
        }
        if (currentTime == 0) {
            return "00:00:00:000";
        }
        int hours = (int) (currentTime / (60 * 60 * 1000));
        int minutes = (int)(currentTime - hours * 60 * 60 * 1000) / 60000;
        int second = (int)(currentTime - hours*60*60*1000 - minutes*60*1000)/1000;
        int mills = (int)(currentTime%1000);

        return new StringBuilder().append(hours).append(":").append(minutes).append(":").append(second).append(":").append(mills).toString();
    }
}
