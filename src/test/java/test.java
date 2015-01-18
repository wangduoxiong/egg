import java.io.File;

/**
 * Created by andrew on 15-1-16.
 */
class FileUtilsTest {
    public static void main(String[] args){
        //System.out.println(System.getProperty("user.dir"));
        //File file = new File("/home/andrew/code/java/Egg/test/test");
        //file.getParentFile().mkdirs();
        //String filename = file.getParent();
        //System.out.println(file.getAbsolutePath());
        File file = new File("1");
        //System.out.println(FileUtils.getPath());
        System.out.println(file.getAbsolutePath());
        file.delete();
    }
}
