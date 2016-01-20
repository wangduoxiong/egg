package edu.xiyou.andrew.egg.utils;

import com.google.common.collect.Lists;
import edu.xiyou.andrew.egg.scheduler.Scheduler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * 对指定包下的Class类进行操作
 * Created by andrew on 15-9-9.
 */
@Deprecated
public class ClassUtils {

    @SuppressWarnings("unchecked")
    public static List<Class> getAllClassByInterface(Class clazz) throws IOException, ClassNotFoundException {
        List<Class> classList = Lists.newArrayList();
        if (clazz == null){
            return classList;
        }
        if (clazz.isInterface()){
            String packageName = clazz.getPackage().getName();
            List<Class<?>> allClass = getClasses(packageName);
            for (Class c : allClass){
                if (clazz.isAssignableFrom(c)){
                    if (!c.equals(clazz)){
                        classList.add(c);
                    }
                }
            }
        }
        return classList;
    }

    public static String[] getPackageAllClassName(String packagetAbsolutePath, boolean recursive) throws IOException {
        String[] allFileName = ArrayUtils.EMPTY_STRING_ARRAY;
        if (!StringUtils.isNotBlank(packagetAbsolutePath)) return allFileName;
        File packageFile = new File(packagetAbsolutePath);

        if (packageFile.isDirectory() && !recursive){
            allFileName = packageFile.list(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    return !file.isFile();
                }
            });
        }else if (packageFile.isDirectory() && (packageFile.listFiles() != null)){
            List<String> fileNameList = Lists.newArrayList();
            List<File> packageFileList = Lists.newArrayList();
            for (File file : packageFileList){
                if (file.isFile()){
                    fileNameList.add(file.getName());
                }else if(file.isDirectory()){
                    fileNameList.addAll(Arrays.asList(getPackageAllClassName(file.getAbsolutePath(), true)));
                }
            }
            allFileName = fileNameList.toArray(new String[fileNameList.size()]);
        }

        return allFileName;
    }

    public static void getPackageAllClassName(String name) throws IOException {
        getPackageAllClassName(name, false);
    }

    public static List<Class<?>> getClasses(String packageName, boolean recursive) throws ClassNotFoundException {
        List<Class<?>> classList = Lists.newArrayList();
        String packagePath = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        return classList;
    }

    public static List<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
        return getClasses(packageName, false);
    }

    public static void main(String[] args) throws IOException {
//        System.out.println(ClassUtils.class.getResource(""));
//        System.out.println(ClassUtils.class.getClassLoader().getResource("edu/xiyou/andrew/egg"));
        getPackageAllClassName(ClassUtils.class.getClassLoader().getResource("").getFile() + Scheduler.class.getPackage().getName().replace('.', '/'), true);
    }
}
