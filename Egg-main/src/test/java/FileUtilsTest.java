/**
 * Created by andrew on 15-1-16.
 */
public class FileUtilsTest {
    String aa(){
        return String.valueOf(this.getClass().getResource(""));
    }

    public static void main(String[] args){
        //System.out.println(System.getProperty("user.dir"));
        //File file = new File("/home/andrew/code/java/Egg/test/test");
        //file.getParentFile().mkdirs();
        //String filename = file.getParent();
        //System.out.println(file.getAbsolutePath());
        //System.out.println(FileUtils.getPath());
        System.out.println(new FileUtilsTest().aa());
    }
}
