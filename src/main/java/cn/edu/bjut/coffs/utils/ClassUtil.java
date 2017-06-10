package cn.edu.bjut.coffs.utils;

import cn.edu.bjut.coffs.enums.URLTypeEnum;
import com.google.common.base.*;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by chenshouqin on 2016-07-07 16:58.
 */
public class ClassUtil {

    private static Joiner joiner = Joiner.on(".").skipNulls();

    /**
     * 获取制定包下面的类
     * @param pack
     * @return
     */
    public static Set<Class<?>> getClasses(Set<String> pack) {
        pack = Optional.fromNullable(pack).or(Collections.<String>emptySet());
        checkArgument(!pack.isEmpty(), "please set base scan controller package !");

        Set<Class<?>> classes = Sets.newLinkedHashSet();
        boolean recursive = true;

        Map<String, String> packageDirMap = formatPackageName(pack);
        Enumeration<URL> dirs;
        Set<String> packageDirNames = packageDirMap.keySet();
        for(String packageDirName : packageDirNames) {
            try {
                dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);

                while(dirs.hasMoreElements()) {
                    URL url = dirs.nextElement();
                    String protocol = url.getProtocol();
                    if(URLTypeEnum.FILE.getName().equals(protocol)) {
                        LOGGER.infoLog(ClassUtil.class, "getClasses", "file type is scanning ... ");
                        String filePath = URLDecoder.decode(url.getFile(), Charsets.UTF_8.name());

                        findAndAddClassByFile(packageDirMap.get(packageDirName), filePath, recursive, classes);
                    } else if(URLTypeEnum.JAR.getName().equals(protocol)) {
                        LOGGER.infoLog(ClassUtil.class, "getClasses", "jar type is scanning ...");
                        JarFile jarFile = ((JarURLConnection)url.openConnection()).getJarFile();
                        Enumeration<JarEntry> enumeration = jarFile.entries();

                        while(enumeration.hasMoreElements()) {
                            JarEntry entry = enumeration.nextElement();
                            String name = entry.getName();

                            /**如果是以/开头，则获取后面的字符串**/
                            if('/' == name.charAt(0)) {
                                name = name.substring(1);
                            }
                            if(name.startsWith(packageDirName)) {
                                int index = name.lastIndexOf('/');
                                if(-1 != index) {
                                    packageDirMap.put(packageDirName, CharMatcher.is('/').replaceFrom(
                                            name.substring(0, index), '.'));

                                    if(-1 != index || recursive) {
                                        if(name.endsWith(".class") && ! entry.isDirectory()) {
                                            String className = name.substring(
                                              packageDirMap.get(packageDirName).length() + 1,
                                                    name.length() - 6);

                                            classes.add(Class.forName(getClassPath(packageDirMap.get(packageDirName), className)));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.errorLog(ClassUtil.class, "getClasses", e);
            }
        }

        return classes;
    }

    /**
     * 将包名 . 替换成 /
     * @param pack
     * @return
     */
    private static Map<String, String> formatPackageName(Set<String> pack) {
        Map<String, String> result = Maps.newHashMap();
        for(String s : pack) {
            String packageDirName = CharMatcher.is('.').replaceFrom(s, '/');
            result.put(packageDirName, s);
        }
        return result;
    }

    private static void findAndAddClassByFile(String packName,
                                              String packagePath,
                                              final boolean recursive,
                                              Set<Class<?>> classes) {
        File dir = new File(packagePath);
        if(!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });

        for(File file : dirFiles) {
            if(file.isDirectory()) {
                findAndAddClassByFile(getClassPath(packName, file.getName()), file.getAbsolutePath(),
                        recursive, classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(getClassPath(packName,className)));
                } catch (ClassNotFoundException e) {
                    LOGGER.errorLog(ClassUtil.class, "findAndAddClassByFile", e);
                }
            }
        }
    }

    private static String getClassPath(String packName, String className) {
        return joiner.join(Strings.nullToEmpty(packName), Strings.nullToEmpty(className));
    }
}
