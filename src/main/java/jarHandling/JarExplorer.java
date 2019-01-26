package jarHandling;

import javafx.scene.control.ListView;
import javassist.*;
import sun.applet.Main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.jar.*;

public class JarExplorer {

    private ArrayList<JarEntry> files = new ArrayList<>();
    private String pathToJar;
    private Manifest manifest;
    private JarFile jarFile;
    public ClassPool classPool;

    public JarExplorer(String pathToJar) {
        this.pathToJar = pathToJar;
    }

    public ArrayList<JarClass> getJarClass() throws IOException, NotFoundException {

        jarFile = new JarFile(this.pathToJar);

        files = new ArrayList<>();
        ArrayList<String> classNames = new ArrayList<>();

        JarInputStream jarFile = new JarInputStream(new FileInputStream(this.pathToJar));
        manifest = jarFile.getManifest();
        JarEntry jarEntry;
        while (true) {
            jarEntry = jarFile.getNextJarEntry();
            if (jarEntry == null) {
                break;
            }
            if (jarEntry.getName().endsWith(".class")) {
                classNames.add(jarEntry.getName().replaceAll("/", "\\."));
            } else if (!jarEntry.getName().endsWith("/")) {
                files.add(jarEntry);
            }
        }

        classPool = ClassPool.getDefault();

        classPool.insertClassPath(this.pathToJar);

        ClassPool.doPruning = false;

        ArrayList<JarClass> jarClassList = new ArrayList<>();
        for (String className : classNames) {
            jarClassList.add(new JarClass(classPool.get(className.replaceAll(".class", ""))));
        }
        return jarClassList;
    }

    public void replaceJarFile(ListView<JarClass> classItems, File fileToSave) throws IOException, CannotCompileException {
        String destinationPath = fileToSave.getAbsolutePath();
        Manifest manifest = this.manifest;
        JarOutputStream jarOutputStream = null;

        File savingFile = new File(destinationPath);
        OutputStream outputStream = new FileOutputStream(savingFile);
        jarOutputStream = new JarOutputStream(outputStream, manifest);

        List<String> fileList = new ArrayList<>();
        for (int i = 0; i < classItems.getItems().size(); i++)
            fileList.add(((classItems.getItems().get(i).getCtClass().getName()).replaceAll("\\.", "/")) + ".class");

        int len;
        byte[] buffer = new byte[1024];
        int i = 0;
        for (String file : fileList) {
            JarEntry je = new JarEntry(file);

            Objects.requireNonNull(jarOutputStream).putNextEntry(je);

            InputStream is = new BufferedInputStream(new ByteArrayInputStream(classItems.getItems().get(i).getCtClass().toBytecode()));
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                jarOutputStream.write(buffer, 0, len);
            }
            i++;
            is.close();
            jarOutputStream.closeEntry();
        }
        for (JarEntry file : this.files) {
            JarEntry jf = file;
            InputStream is = this.jarFile.getInputStream(jf);
            Objects.requireNonNull(jarOutputStream).putNextEntry(file);
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                jarOutputStream.write(buffer, 0, len);
            }
            jarOutputStream.closeEntry();
            is.close();
        }
        Objects.requireNonNull(jarOutputStream).close();
    }
}
